package de.uni_potsdam.hpi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;

import de.uni_potsdam.hpi.data.GbifParser;
import de.uni_potsdam.hpi.data.SpeciesData;
import de.uni_potsdam.hpi.services.DBpediaService;
import de.uni_potsdam.hpi.services.FreebaseService;
import de.uni_potsdam.hpi.services.GbifService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;


// The Java class will be hosted at the URI path "/occurrence"
@Path("/")
public class QueryEndpoint {
    
    private static Model model;
    private static String DATA_PATH = "resource/rdf/data.ttl";
    
    private static Model getModel(){
        if (null == model){
            model = ModelFactory.createDefaultModel();
            try {
                if (!new File(DATA_PATH).exists()){
                    new File(DATA_PATH).createNewFile();
                }
                model.read(DATA_PATH);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return model;
    }

    @GET
    @Path("occurrences")
    public Response getCoordinates(@QueryParam("longitude") double longitude, @QueryParam("latitude") double latitude) {
        URI targetURIForRedirection = URI.create("localhost:9998?longitude1="+longitude+"&longitude2="+longitude+
                "&latitude1="+latitude+"&latitude2="+latitude);
        return Response.seeOther(targetURIForRedirection).build();
    }
    
    @GET
    @Produces("text/html")
    @Path("occurrences")
    public String getCoordinates
        (@QueryParam("longitude1") double longitude1, @QueryParam("longitude2") double longitude2, 
                @QueryParam("latitude1") double latitude1, @QueryParam("latitude2") double latitude2) {
        GbifService gbif = new GbifService();
        new GbifParser().parseData
            (gbif.getOccurrenceForRange(latitude1, latitude2, longitude1, longitude2), getModel());       

        String occurrenceQuery = "Select ?occurrence ?latitude ?species where{"+
                "?occurrence <http://rs.tdwg.org/dwc/terms/decimalLatitude> ?latitude ."+
                "?occurrence <http://rs.tdwg.org/dwc/terms/decimalLatitude> ?longitude ."+
                "?occurence <http://rs.tdwg.org/dwc/terms/associatedTaxa> ?species ."+
                "FILTER(?latitude >= "+latitude1+") ."+
                "FILTER(?latitude <= "+latitude2+") ."+
                "FILTER(?longitude >= "+longitude1+") ."+
                "FILTER(?longitude <= "+longitude2+") .}";
        Query query = QueryFactory.create(occurrenceQuery);
        QueryExecution qexec = QueryExecutionFactory
          .create(query, getModel());
        ResultSet results = qexec.execSelect();
        while (results.hasNext()){
            QuerySolution answer =  results.next();
            if (!isSpeciesStored(answer.get("?species"))){
                SpeciesData species = getSpecies(answer.get("species"));
                new DBpediaService().includeDataFromDBpedia(species);
                new FreebaseService().includeDataFromFreebase(species);
                species.encodeSpeciesInRDF(getModel());
            }
        }
        
        try {
            FileWriter fw;
            fw = new FileWriter(new File(DATA_PATH));
            model.write(fw, "TURTLE");
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
//        StringBuilder sb = new StringBuilder();
//        sb.append("<!DOCTYPE html>\n" +
//                "<html>\n" +
//                "<head lang=\"en\">\n" +
//                "    <meta charset=\"UTF-8\">\n" +
//                "    <title>Occurence</title>\n" +
//                "</head>\n" +
//                "<body>\n");
//        for (OccurrenceData occurrence : occurences){
//            db.includeDataFromDBpedia(occurrence.getSpecies());
//            double latitude = Double.parseDouble(occurrence.getLatitude());
//            double longitude = Double.parseDouble(occurrence.getLongitude());
//            sb.append("<h1>" + occurrence.getSpecies().getScientificName() + "</h1>" +
//                "<img src=\"" + occurrence.getSpecies().getThumbnailURL() + "\"/>"+
//                "\n" + "<p>"+ occurrence.getSpecies().getDescription() +"</p>" +
//                "<a href=\"http://localhost:9998/species?species="+occurrence.getSpecies().getBinomial()
//                    +"&scientificName="+occurrence.getSpecies().getScientificName()+"\">Get To Animal Overview</a>");
////                "<h2>Map</h2>" +
////                "<iframe width=\"425\" height=\"350\" frameborder=\"0\" scrolling=\"no\" marginheight=\"0\" marginwidth=\"0\" " +
////                "src=\"http://www.openstreetmap.org/export/embed.html?bbox=" + (longitude - 0.5) + "%2C" +
////                (latitude - 0.5) + "%2C" + (longitude + 0.5) + "%2C" + (latitude + 0.5) + "&amp;layer=mapnik&amp;marker=" + latitude + "%2C" +longitude + ";marker=" + (latitude+0.5) + "%2C" +longitude+(0.5) + "\"" +
////                " style=\"border: 1px solid black\"></iframe>");             
//        }
////        sb.append("<small>" +
////                "<a href=\"http://www.openstreetmap.org/?mlat=" + latitude1 +"&amp;mlon=" + longitude1 + "#map=10/" + latitude2 + "/" + latitude2 + "\">Größere Karte anzeigen</a></small>" +
////                "</small>");
//        
//        return sb.toString();
        return "";
    }
    
    private SpeciesData getSpecies(RDFNode species) {
        String queryString = "SELECT ?binomial ?scientificName WHERE {"+
                "<"+species.toString()+">" + " <http://rs.tdwg.org/dwc/terms/scientificName> ?scientificName ."+
                "<"+species.toString()+">" + " <http://rs.tdwg.org/dwc/terms/binomial> ?binomial}";
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory
          .create(query, model);
        QuerySolution sol = qexec.execSelect().next();
        SpeciesData s = new SpeciesData(sol.get("scientificName").asLiteral().getString(),
                sol.get("binomial").asLiteral().getString());
        s.setEntityURI(species.toString());
        return s;
    }

    private boolean isSpeciesStored(RDFNode species) {
        String queryString = "ASK WHERE {"+
                "<"+species.toString()+">" + " <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rs.tdwg.org/dwc/terms/Taxon>}";
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory
          .create(query, model);
        return qexec.execAsk();
    }

    @GET
    @Produces("text/html")
    @Path("species")
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