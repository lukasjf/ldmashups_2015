
package de.uni_potsdam.hpi;

import org.glassfish.grizzly.http.server.*;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.message.internal.ReaderWriter;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.ext.RuntimeDelegate;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;


public class Main {

    //public static final URI BASE_URI = UriBuilder.fromUri("http://localhost/").port(9998).build();
    public static final String BASE_URI = "http://localhost:9998";
    public static final String WEB_ROOT = "resource/frontend";
    public static final String APP_PATH = "/taxon-finder";
    public static final String API_PATH = "/api";
    public static final int PORT = 9998;

    protected static HttpServer startServer() throws IOException {
        final HttpServer server = new HttpServer();
        final NetworkListener listener = new NetworkListener("grizzly", "localhost", PORT);

        server.addListener(listener);
        final ResourceConfig rc = new ResourceConfig().packages("de.uni_potsdam.hpi");
        final ServerConfiguration config = server.getServerConfiguration();
        // add handler for serving static content
        config.addHttpHandler(new StaticContentHandler(null),
                APP_PATH);

        config.addHttpHandler(RuntimeDelegate.getInstance().createEndpoint(rc, GrizzlyHttpContainer.class),
                API_PATH);

        try {
            // Start the server.
            server.start();
        } catch (Exception ex) {
            throw new ProcessingException("Exception thrown when trying to start grizzly server", ex);
        }

        return server;
        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        //return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();
    }


    /**
     * Simple HttpHandler for serving static content included in web root
     * directory of this application.
     */
    private static class StaticContentHandler extends HttpHandler {
        private static final HashMap<String, String> EXTENSION_TO_MEDIA_TYPE;

        static {
            EXTENSION_TO_MEDIA_TYPE = new HashMap<String, String>();

            EXTENSION_TO_MEDIA_TYPE.put("html", "text/html");
            EXTENSION_TO_MEDIA_TYPE.put("js", "application/javascript");
            EXTENSION_TO_MEDIA_TYPE.put("css", "text/css");
            EXTENSION_TO_MEDIA_TYPE.put("png", "image/png");
            EXTENSION_TO_MEDIA_TYPE.put("ico", "image/png");
        }

        private final String webRootPath;

        StaticContentHandler(String webRootPath) {
            this.webRootPath = webRootPath;
        }

        @Override
        public void service(Request request, Response response) throws Exception {
            String uri = request.getRequestURI();

            int pos = uri.lastIndexOf('.');
            String extension = uri.substring(pos + 1);
            String mediaType = EXTENSION_TO_MEDIA_TYPE.get(extension);

            if (uri.contains("..") || mediaType == null) {
                response.sendError(HttpStatus.NOT_FOUND_404.getStatusCode());
                return;
            }

            final String resourcesContextPath = request.getContextPath();
            if (resourcesContextPath != null && !resourcesContextPath.isEmpty()) {
                if (!uri.startsWith(resourcesContextPath)) {
                    response.sendError(HttpStatus.NOT_FOUND_404.getStatusCode());
                    return;
                }

                uri = uri.substring(resourcesContextPath.length());
            }

            InputStream fileStream;

            try {
                fileStream = webRootPath == null ?
                        //Main.class.getResourceAsStream(WEB_ROOT + uri) :
                        new FileInputStream(WEB_ROOT + uri) :
                        new FileInputStream(webRootPath + uri);
            } catch (IOException e) {
                fileStream = null;
            }

            if (fileStream == null) {
                response.sendError(HttpStatus.NOT_FOUND_404.getStatusCode());
            } else {
                response.setStatus(HttpStatus.OK_200);
                response.setContentType(mediaType);
                ReaderWriter.writeTo(fileStream, response.getOutputStream());
            }
        }
    }
}
