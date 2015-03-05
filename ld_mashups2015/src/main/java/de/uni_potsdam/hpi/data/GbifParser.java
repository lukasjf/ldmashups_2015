package de.uni_potsdam.hpi.data;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * This class parses the results from the GBIF.
 * It receives an JSONArray which contains the result of the GBIF-Querry and saves it to a Jena Model.
 * Therefore the data from the JSON Array will be saved to an Object of the OccurrenceData.
 */
public class GbifParser {
    public void parseData(JSONArray results, Model model){
        for(int i = 0; i < results.length(); i++){
            parseResult(results.getJSONObject(i), model);
        }
    }

    private void parseResult(JSONObject result, Model model) {
        String queryString = "ASK WHERE { " +
                 " ?occurrence <http://rs.tdwg.org/dwc/terms/occurrenceID>" + result.getInt("key")+
                "}";
        Query query = QueryFactory.create(queryString);
        QueryExecution queryExec = QueryExecutionFactory.create(query, model);
        if (queryExec.execAsk()){
            return;
        }
        else{
            createNewOccurrenceData(model, result);
        }
    }

    private OccurrenceData createNewOccurrenceData(Model model, JSONObject result) {
        OccurrenceData occurrence = new OccurrenceData(model, "http://www.gbif.org/occurrence/" + result.getInt("key"));
        occurrence.setLatitude(""+ result.getDouble("decimalLatitude"));
        occurrence.setLongitude("" + result.getDouble("decimalLongitude"));
        occurrence.setOccurrenceID("" + result.getInt("key"));
        try {
            occurrence.setYear("" + result.getInt("year"));
            occurrence.setMonth("" + result.getInt("month"));
            occurrence.setDay(""+ result.getInt("day"));
        } catch (Exception e) {
            System.err.println("The Occurrences is missing a date");
        }
        occurrence.setGeodeticDatum(""+ result.get("geodeticDatum"));
        try {
            SpeciesData occurredSpecies = new SpeciesData(model, "http://www.gbif.org/species/" + result.getInt("speciesKey"));
            occurrence.setSpecies(occurredSpecies);
            occurredSpecies.setBinomial(result.getString("species"));
            occurredSpecies.setScientificName(result.getString("scientificName"));
        } catch (Exception e) {
            System.err.println("Species misses key: " + result.getString("scientificName"));
        }
        return occurrence;
    }
}
