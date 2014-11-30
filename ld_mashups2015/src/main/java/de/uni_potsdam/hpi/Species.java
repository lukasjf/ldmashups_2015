package de.uni_potsdam.hpi;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import de.uni_potsdam.hpi.data.SpeciesData;
import de.uni_potsdam.hpi.services.DBpediaService;
import de.uni_potsdam.hpi.services.FreebaseService;

@Path("/species")
public class Species {
 // TODO: update the class to suit your needs

    // The Java method will process HTTP GET requests
    @GET
    // The Java method will produce content identified by the MIME Media
    // type "text/html"
    @Produces("text/html")
    public String getIt(@QueryParam("species")String binomial, @QueryParam("scientificName")String scientificName) {
        SpeciesData species = new SpeciesData(scientificName, binomial);
        DBpediaService db = new DBpediaService();
        db.includeDataFromDBpedia(species);
        FreebaseService fs = new FreebaseService();
        fs.includeDataFromFreebase(species);
        StringBuilder sb = new StringBuilder();
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
                "</small>" +
                "</body>\n" +
                "</html>");
    }
}
