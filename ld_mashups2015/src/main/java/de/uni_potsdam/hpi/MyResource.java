
package de.uni_potsdam.hpi;

import de.uni_potsdam.hpi.data.Species;
import de.uni_potsdam.hpi.services.DBpediaService;
import de.uni_potsdam.hpi.services.FreebaseService;
import de.uni_potsdam.hpi.services.GbifService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

// The Java class will be hosted at the URI path "/occurrence"
@Path("/occurrences")
public class MyResource {

    // TODO: update the class to suit your needs
    
    // The Java method will process HTTP GET requests
    @GET 
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"
    @Produces("text/html")
    public String getIt(@QueryParam("longitude") double longitude, @QueryParam("latitude") double latitude) {
        GbifService gbif = new GbifService();
        DBpediaService db = new DBpediaService();
        Species species = gbif.getSpeciesByLocation(latitude, longitude);
        /*db.includeDataFromDBpedia(species);*/
        FreebaseService fs = new FreebaseService();
        fs.includeDataFromFreebase(species);
        StringBuilder sb = new StringBuilder();
        species.encodeSpeciesInRDF();
        for (String url : species.getImageUrls()) {
            sb.append("<img src=\"" + url + "\"/>");

        }
        sb.append("See also:");
        for (String url : species.getEquivalentWebpages()) {
            sb.append("<p><a href=\""+ url +"\">"+ url +"</a></p>");
        }
        return ("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head lang=\"en\">\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Occurence</title>\n" +
                "</head>\n" +
                "<body>\n" +
               /* "<img src=\"" + species.getThumbnailURL() + "\"/>"+
                "\n" + "<p>"+ species.getDescription() +"</p>" +*/
                sb.toString() +
                "</body>\n" + "<iframe width=\"425\" height=\"350\" frameborder=\"0\" scrolling=\"no\" marginheight=\"0\" marginwidth=\"0\" src=\"http://www.openstreetmap.org/export/embed.html?bbox=12.148132324218748%2C52.06093458403525%2C14.1119384765625%2C52.8226825580693&amp;layer=mapnik\" style=\"border: 1px solid black\">\n" +
                "</iframe>\n" +
                "<br/>\n" +
                "<small>\n" +
                "<a href=\"http://www.openstreetmap.org/#map=10/"+latitude +"/" + longitude + "\">\n" +
                "Größere Karte anzeigen</a>\n" +
                "</small>" +
                "</html>");
    }
}