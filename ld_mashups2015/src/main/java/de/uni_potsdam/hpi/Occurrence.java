package de.uni_potsdam.hpi;

import de.uni_potsdam.hpi.data.OccurenceData;
import de.uni_potsdam.hpi.services.DBpediaService;
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
    public String getCoordinates(@QueryParam("longitude") double longitude, @QueryParam("latitude") double latitude) {
        GbifService gbif = new GbifService();
        DBpediaService db = new DBpediaService();
        OccurenceData occurence = gbif.getOccurenceForLocation(latitude, longitude);
        db.includeDataFromDBpedia(occurence.getSpecies());
        return ("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head lang=\"en\">\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Occurence</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>" + occurence.getSpecies().getScientificName() + "</h1>" +
                "<img src=\"" + occurence.getSpecies().getThumbnailURL() + "\"/>"+
                "\n" + "<p>"+ occurence.getSpecies().getDescription() +"</p>" +
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