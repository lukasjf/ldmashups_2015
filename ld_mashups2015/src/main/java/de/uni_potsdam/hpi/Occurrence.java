
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
public class Occurrence {

    // TODO: update the class to suit your needs

    // The Java method will process HTTP GET requests
    @GET
    // The Java method will produce content identified by the MIME Media
    // type "text/html"
    @Produces("text/html")
    public String getIt(@QueryParam("longitude") double longitude, @QueryParam("latitude") double latitude) {
        GbifService gbif = new GbifService();
        DBpediaService db = new DBpediaService();
        Species species = gbif.getSpeciesByLocation(latitude, longitude);
        db.includeDataFromDBpedia(species);
        FreebaseService fs = new FreebaseService();
        fs.includeDataFromFreebase(species);
        StringBuilder sb = new StringBuilder();
        species.encodeSpeciesInRDF();
        sb.append("<p>");
        for (String url : species.getImageUrls()) {
            sb.append("<img src=\"" + url + "\"/>");

        }
        sb.append("</p>");
        sb.append("<h2>See also:</h2>");
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
                "<h1>" + species.getScientificName() + "</h1>" +
                "<img src=\"" + species.getThumbnailURL() + "\"/>"+
                "\n" + "<p>"+ species.getDescription() +"</p>" +
                sb.toString() +
                "<h2>Map</h2>" +
                "<iframe width=\"425\" height=\"350\" frameborder=\"0\" scrolling=\"no\" marginheight=\"0\" marginwidth=\"0\" " +
                "src=\"http://www.openstreetmap.org/export/embed.html?bbox=" + (longitude - 0.5) + "%2C" +
                (latitude - 0.5) + "%2C" + (longitude + 0.5) + "%2C" + (latitude + 0.5) + "&amp;layer=mapnik&amp;marker=" + latitude + "%2C" +longitude + "\"" +
                " style=\"border: 1px solid black\"></iframe>" +
                "<br/>" +
                "<small>" +
                "<a href=\"http://www.openstreetmap.org/?mlat=" + latitude +"&amp;mlon=" + longitude + "#map=10/" + latitude + "/" + latitude + "\">Größere Karte anzeigen</a></small>" +
                "</small>" +
                "</body>\n" +
                "</html>");
    }
}