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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

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
    @Produces(MediaType.APPLICATION_JSON)
    @Path("occurrences")
    public Response getCoordinates
        (@QueryParam("longitude1") double longitude1, @QueryParam("longitude2") double longitude2, 
                @QueryParam("latitude1") double latitude1, @QueryParam("latitude2") double latitude2) {
        ResultSet results = getOccurrencesInRange(latitude1, latitude2, longitude1, longitude2);
        JSONArray output = new JSONArray();
        while (results.hasNext()){
            QuerySolution answer =  results.next();
            addSpeciesDataToOccurrence(answer.get("species"));
            QuerySolution sol = getOutputInformation(answer.get("occurrence"));
            output.put(addOccurenceToJSON(sol));
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
        Response response = Response
            .ok(output.toString())
            .header("Access-Control-Allow-Origin", "http://localhost:63342")
            .type(MediaType.APPLICATION_JSON)
            .build();
        return response;
    }
    
    private JSONObject addOccurenceToJSON(QuerySolution sol) {
        JSONObject occurrence = new JSONObject();
        occurrence.put("longitude", sol.get("longitude"));
        occurrence.put("latitude", sol.get("latitude"));
        occurrence.put("thumbnailURL", sol.get("thumbnailURL"));
        occurrence.put("abstract", sol.get("abstract"));
        return occurrence;
    }

    private QuerySolution getOutputInformation(RDFNode occurence) {
        String occurrenceQuery = "Select ?latitude ?longitude ?abstract ?thumbnailURL where{"+
                "<"+occurence.toString()+">"+ "<http://rs.tdwg.org/dwc/terms/decimalLatitude> ?latitude ."+
                "<"+occurence.toString()+">"+ "<http://rs.tdwg.org/dwc/terms/decimalLongitude> ?longitude ."+
                "<"+occurence.toString()+">"+ "<http://rs.tdwg.org/dwc/terms/associatedTaxa> ?species ."+
                "?species <http://dbpedia.org/ontology/abstract> ?abstract ."+
                "?species <http://dbpedia.org/ontology/thumbnail> ?thumbnail .";
        Query query = QueryFactory.create(occurrenceQuery);
        QueryExecution qexec = QueryExecutionFactory
          .create(query, getModel());
        return qexec.execSelect().next();
    }

    private void addSpeciesDataToOccurrence(RDFNode speciesID) {
        if (!isSpeciesStored(speciesID)){
            SpeciesData species = getSpecies(speciesID);
            new DBpediaService().includeDataFromDBpedia(species);
            new FreebaseService().includeDataFromFreebase(species);
            species.encodeSpeciesInRDF(getModel());
        }
    }

    private ResultSet getOccurrencesInRange(double latitude1, double latitude2,
            double longitude1, double longitude2) {
        GbifService gbif = new GbifService();
        new GbifParser().parseData
            (gbif.getOccurrenceForRange(latitude1, latitude2, longitude1, longitude2), getModel());       

        String occurrenceQuery = "Select ?occurrence ?latitude ?longitude ?species where{"+
                "?occurrence <http://rs.tdwg.org/dwc/terms/decimalLatitude> ?latitude ."+
                "?occurrence <http://rs.tdwg.org/dwc/terms/decimalLongitude> ?longitude ."+
                "?occurence <http://rs.tdwg.org/dwc/terms/associatedTaxa> ?species ."+
                "FILTER(?latitude >= "+latitude1+") ."+
                "FILTER(?latitude <= "+latitude2+") ."+
                "FILTER(?longitude >= "+longitude1+") ."+
                "FILTER(?longitude <= "+longitude2+") .}";
        Query query = QueryFactory.create(occurrenceQuery);
        QueryExecution qexec = QueryExecutionFactory
          .create(query, getModel());
        return qexec.execSelect();
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