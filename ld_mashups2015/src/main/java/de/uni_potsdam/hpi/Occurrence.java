package de.uni_potsdam.hpi;

import java.net.URI;
import java.util.List;

import de.uni_potsdam.hpi.data.OccurrenceData;
import de.uni_potsdam.hpi.services.DBpediaService;
import de.uni_potsdam.hpi.services.GbifService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;


// The Java class will be hosted at the URI path "/occurrence"
@Path("/occurrences")
public class Occurrence {

    // The Java method will process HTTP GET requests
    @GET

    public Response getCoordinates(@QueryParam("longitude") double longitude, @QueryParam("latitude") double latitude,
            @QueryParam("distance")double distance) {
        System.out.println(distance);
//        URI targetURIForRedirection = URI.create("localhost:9998?longitude1="+longitude+"&longitude2="+longitude+
//                "&latitude1="+latitude+"&latitude2="+latitude);
//        return Response.temporaryRedirect(targetURIForRedirection).build();
        return null;
    }
    
    @GET
    // The Java method will produce content identified by the MIME Media
    // type "text/html"
    @Produces("text/html")
    public String getCoordinates
        (@QueryParam("longitude1") double longitude1, @QueryParam("longitude2") double longitude2, 
                @QueryParam("latitude1") double latitude1, @QueryParam("latitude2") double latitude2) {
        GbifService gbif = new GbifService();
        DBpediaService db = new DBpediaService();
        List<OccurrenceData> occurences = gbif.getOccurenceForLocation(latitude1, latitude2, longitude1, longitude2);
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head lang=\"en\">\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Occurence</title>\n" +
                "</head>\n" +
                "<body>\n");
        for (OccurrenceData occurrence : occurences){
            db.includeDataFromDBpedia(occurrence.getSpecies());
            occurrence.encodeOccurrenceInRDF();
            double latitude = Double.parseDouble(occurrence.getLatitude());
            double longitude = Double.parseDouble(occurrence.getLongitude());
            sb.append("<h1>" + occurrence.getSpecies().getScientificName() + "</h1>" +
                "<img src=\"" + occurrence.getSpecies().getThumbnailURL() + "\"/>"+
                "\n" + "<p>"+ occurrence.getSpecies().getDescription() +"</p>" +
                "<a href=\"http://localhost:9998/species?species="+occurrence.getSpecies().getBinomial()
                    +"&scientificName="+occurrence.getSpecies().getScientificName()+"\">Get To Animal Overview</a>");
//                "<h2>Map</h2>" +
//                "<iframe width=\"425\" height=\"350\" frameborder=\"0\" scrolling=\"no\" marginheight=\"0\" marginwidth=\"0\" " +
//                "src=\"http://www.openstreetmap.org/export/embed.html?bbox=" + (longitude - 0.5) + "%2C" +
//                (latitude - 0.5) + "%2C" + (longitude + 0.5) + "%2C" + (latitude + 0.5) + "&amp;layer=mapnik&amp;marker=" + latitude + "%2C" +longitude + ";marker=" + (latitude+0.5) + "%2C" +longitude+(0.5) + "\"" +
//                " style=\"border: 1px solid black\"></iframe>");             
        }
//        sb.append("<small>" +
//                "<a href=\"http://www.openstreetmap.org/?mlat=" + latitude1 +"&amp;mlon=" + longitude1 + "#map=10/" + latitude2 + "/" + latitude2 + "\">Größere Karte anzeigen</a></small>" +
//                "</small>");
        
        return sb.toString();
    }
}