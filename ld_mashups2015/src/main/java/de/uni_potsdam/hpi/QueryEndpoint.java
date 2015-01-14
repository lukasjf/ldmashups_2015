package de.uni_potsdam.hpi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import de.uni_potsdam.hpi.data.GbifParser;
import de.uni_potsdam.hpi.data.SpeciesData;
import de.uni_potsdam.hpi.services.DBpediaService;
import de.uni_potsdam.hpi.services.GbifService;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.uni_potsdam.hpi.services.WikimediaService;
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
                model.read(DATA_PATH ,"TTL");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return model;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("occurrences")
    public Response getCoordinates(@QueryParam("longitude") double longitude, @QueryParam("latitude") double latitude,
            @DefaultValue("0.016")@QueryParam("distance") double distance) {
        try{
            ResultSet results = getOccurrencesInRange(latitude - distance, latitude + distance, 
                    longitude - distance, longitude + distance);
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
                model.write(fw,"TTL");
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            Response response = Response
                .ok(output.toString())
                .header("Access-Control-Allow-Origin", "http://localhost:63342")
                .type(MediaType.APPLICATION_JSON)
                .build();
            return response;
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }    
    
    private JSONObject addOccurenceToJSON(QuerySolution sol) {
        if (null == sol)
            return null;
        JSONObject occurrence = new JSONObject();
        occurrence.put("longitude", sol.get("longitude").asLiteral().getDouble());
        occurrence.put("latitude", sol.get("latitude").asLiteral().getDouble());
        occurrence.put("thumbnailURL", sol.get("thumbnailURL").asResource().getURI());
        occurrence.put("abstract", sol.get("abstract").asLiteral().getString());
        occurrence.put("binomial", sol.get("binomial").asLiteral().getString());
        occurrence.put("scientificName", sol.get("scientificName").asLiteral().getString());
        return occurrence;
    }

    private QuerySolution getOutputInformation(RDFNode occurrence) {
        String occurrenceQuery = "Select ?latitude ?longitude ?abstract ?thumbnailURL ?binomial ?scientificName where{"+
                "<"+occurrence.toString()+">"+ "<http://rs.tdwg.org/dwc/terms/decimalLatitude> ?latitude ."+
                "<"+occurrence.toString()+">"+ "<http://rs.tdwg.org/dwc/terms/decimalLongitude> ?longitude ."+
                "<"+occurrence.toString()+">"+ "<http://rs.tdwg.org/dwc/terms/associatedTaxa> ?species ."+
                "?species <http://dbpedia.org/ontology/abstract> ?abstract ."+
                "?species <http://dbpedia.org/ontology/thumbnail> ?thumbnailURL ."+
                "?species <http://rs.tdwg.org/dwc/terms/scientificName> ?scientificName ."+
                "?species <http://dbpedia.org/property/binomial> ?binomial}";
        Query query = QueryFactory.create(occurrenceQuery);
        QueryExecution qexec = QueryExecutionFactory
          .create(query, getModel());        
        ResultSet temp = qexec.execSelect();
        if(temp.hasNext()){
            return temp.next();
        }
        return null;
    }

    private void addSpeciesDataToOccurrence(RDFNode speciesID) {
        if (!isSpeciesStored(speciesID)){
            SpeciesData species = getSpecies(speciesID);
            new DBpediaService().includeDataFromDBpedia(species);
            //new FreebaseService().includeDataFromFreebase(species);
        }
    }

    private ResultSet getOccurrencesInRange(double latitude1, double latitude2,
            double longitude1, double longitude2) {
        GbifService gbif = new GbifService();
        new GbifParser().parseData
            (gbif.getOccurrenceForRange(latitude1, latitude2, longitude1, longitude2), getModel());       

        String occurrenceQuery = "Select distinct ?occurrence ?latitude ?longitude ?species where{"+
                "?occurrence <http://rs.tdwg.org/dwc/terms/decimalLatitude> ?latitude ."+
                "?occurrence <http://rs.tdwg.org/dwc/terms/decimalLongitude> ?longitude ."+
                "?occurrence <http://rs.tdwg.org/dwc/terms/associatedTaxa> ?species ."+
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
                "<"+species.toString()+">" + " <http://dbpedia.org/property/binomial> ?binomial}";
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory
          .create(query, model);
        QuerySolution sol = qexec.execSelect().next();
        SpeciesData s = new SpeciesData(getModel(),species.toString());
        s.setBinomial(sol.get("binomial").asLiteral().getString());
        return s;
    }

    private boolean isSpeciesStored(RDFNode species) {
        String queryString = "ASK WHERE {"+
                "<"+species.toString()+">" + " <http://rs.tdwg.org/dwc/terms/associatedMedia> <>}";
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory
          .create(query, model);
        return qexec.execAsk();
    }
}