package de.uni_potsdam.hpi.data;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;

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
            OccurrenceData occurence = createNewOccurrenceData(result);
            occurence.encodeOccurrenceInRDF(model);
        }
    }

    private OccurrenceData createNewOccurrenceData(JSONObject result) {
        OccurrenceData occurrence = new OccurrenceData();
        occurrence.setLatitude(""+ result.getDouble("decimalLatitude"));
        occurrence.setLongitude("" + result.getDouble("decimalLongitude"));
        occurrence.setOccurrenceID("" + result.getInt("key"));
        occurrence.setEntityURI("http://www.gbif.org/occurrence/" + result.getInt("key"));
        occurrence.setSpecies(new SpeciesData(result.getString("scientificName"),result.getString("species")));
        occurrence.setYear(""+ result.getInt("year"));
        occurrence.setMonth(""+ result.getInt("month"));
        occurrence.setDay(""+ result.getInt("day"));
        occurrence.setGeodeticDatum(""+ result.get("geodeticDatum"));
        occurrence.setSpecies(new SpeciesData("http://www.gbif.org/species/" + result.getInt("speciesKey")));
        return occurrence;
    }
}
