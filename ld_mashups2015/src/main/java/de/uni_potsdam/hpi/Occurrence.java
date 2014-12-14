package de.uni_potsdam.hpi;

import java.util.List;

import de.uni_potsdam.hpi.data.OccurrenceData;
import de.uni_potsdam.hpi.data.OccurrenceXML;
import de.uni_potsdam.hpi.services.DBpediaService;
import de.uni_potsdam.hpi.services.GbifService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

// The Java class will be hosted at the URI path "/occurrence"
@Path("/occurrences")
public class Occurrence {

    // The Java method will process HTTP GET requests
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCoordinates(@QueryParam("longitude") double longitude, @QueryParam("latitude") double latitude,
                                       @DefaultValue("0.016")@QueryParam("distance") double distance) {
        System.out.println(distance);
        GbifService gbif = new GbifService();

        List<OccurrenceData> occurrences = gbif.getOccurenceForLocation(latitude - distance,
                latitude + distance,
                longitude - distance,
                longitude + distance);
        JSONArray occurrencesJson = getJSONArrayFor(occurrences);
        DBpediaService dbPedia = new DBpediaService();
        dbPedia.includeDataFromDBpedia(occurrences.get(0).getSpecies());
        JSONObject result = new JSONObject();
        result.put("longitude",
                Double.parseDouble(occurrences.get(0).getLongitude()));
        result.put("latitude",
                Double.parseDouble(occurrences.get(0).getLatitude()));
        result.put("thumbnailURL",
                occurrences.get(0).getSpecies().getThumbnailURL()
                );
        System.out.println(occurrencesJson);
        Response response = Response
                .ok(occurrencesJson.toString())
                .header("Access-Control-Allow-Origin", "http://localhost:63342")
                .type(MediaType.APPLICATION_JSON)
                .build();
        return response;
    }

    private JSONArray getJSONArrayFor(List<OccurrenceData> occurrences) {
        JSONArray occurrencesJSON = new JSONArray();
        for (OccurrenceData occurrence : occurrences) {
            occurrencesJSON.put(getJSONObjectFor(occurrence));
        }
        return occurrencesJSON;
    }

    private JSONObject getJSONObjectFor(OccurrenceData occurrence) {
        JSONObject occurrenceJSON = new JSONObject();
        DBpediaService dBpediaService = new DBpediaService();
        occurrenceJSON.put("longitude",
                Double.parseDouble(occurrence.getLongitude()));
        occurrenceJSON.put("latitude",
                Double.parseDouble(occurrence.getLatitude()));
        if (null == occurrence.getSpecies().getThumbnailURL()) {
            dBpediaService.includeDataFromDBpedia(occurrence.getSpecies());
        }
        occurrenceJSON.put("thumbnailURL",
                occurrence.getSpecies().getThumbnailURL()
        );
        return occurrenceJSON;
    }

    //@GET
    // The Java method will produce content identified by the MIME Media
    // type "text/html"
    //@Produces("text/html")
   // @Produces(MediaType.APPLICATION_XML)
    public OccurrenceXML getCoordinates
            (@QueryParam("longitude1") double longitude1, @QueryParam("longitude2") double longitude2,
             @QueryParam("latitude1") double latitude1, @QueryParam("latitude2") double latitude2) {
        GbifService gbif = new GbifService();
        DBpediaService db = new DBpediaService();
        List<OccurrenceData> occurrences = gbif.getOccurenceForLocation(latitude1, latitude2, longitude1, longitude2);
        /*OccurrenceXML result = new OccurrenceXML(
                Double.parseDouble(occurrences.get(0).getLongitude()),
                Double.parseDouble(occurrences.get(0).getLatitude()),
                occurrences.get(0).getSpecies().getThumbnailURL()
            );*/
        OccurrenceXML result = new OccurrenceXML(
                13.54983,
                52.55914,
                "http://wiki.dbpedia.org/images/dbpedia_logo.png"
            );

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head lang=\"en\">\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Occurence</title>\n" +
                "</head>\n" +
                "<body>\n");
        for (OccurrenceData occurrence : occurrences){
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
        
        //return sb.toString();
        //return Response.ok(sb.toString()).header("Access-Control-Allow-Origin", "http://localhost:63342/").build();
        return result;
    }
}