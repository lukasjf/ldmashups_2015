package de.uni_potsdam.hpi.services;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;
import de.uni_potsdam.hpi.data.OccurenceData;
import de.uni_potsdam.hpi.data.SpeciesData;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Stephan
 * @version 14/11/11
 */
public class GbifService {
    /** URL (String) to the occurrence API of the gbif */
    String occurenceApiString = "http://api.gbif.org/v1/occurrence/";

    public GbifService() {
    }

    private boolean locationIsValid(double latitude1, double latitude2, double longitude1, double longitude2) {
        return (latitude1 <= 90.0 && latitude2 <= 90.0 && latitude1 >= -90.0 && latitude2 >= -90.0 &&
                longitude1 <= 180 && longitude2 <= 180 && longitude1 >= -180 && longitude2 >= -180);
    }

    public OccurenceData getOccurenceForLocation(double latitude, double longitude) {
        OccurenceData result = null;
        result = searchExistingEntries(latitude, longitude);

        if (null != result) {
           return result;
        }

        if (!locationIsValid(latitude, latitude, longitude, longitude)) {
            System.err.println("Invalid Location");
            return null;
        }
        URL url = null;
        try {
            url = new URL(occurenceApiString + "search?decimalLongitude=" + longitude + "&"
                    + "decimalLatitude=" + latitude);
            HttpURLConnection occurrenceClient = (HttpURLConnection)url.openConnection();
            occurrenceClient.setRequestMethod("GET");
            JSONObject response = getResponse(occurrenceClient);
            JSONObject occurence = response.getJSONArray("results").getJSONObject(0);
            result = new OccurenceData();
            setFieldsFromJson(result, occurence);
            occurrenceClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private OccurenceData searchExistingEntries(double latitude, double longitude) {
        Model model = FileManager.get().loadModel(OccurenceData.FILE_URL, "RDF/XML-ABBREV");
        String queryString = "SELECT ?geodeticDatum ?year ?month ?day WHERE { " +
                "?occurrence <http://rs.tdwg.org/dwc/terms/decimalLatitude> " + latitude + " . " +
                "?occurrence <http://rs.tdwg.org/dwc/terms/decimalLongitude> " + longitude + " . " +
                "?occurrence <http://rs.tdwg.org/dwc/terms/geodeticDatum> ?geodeticDatum . " +
                "?occurrence <http://rs.tdwg.org/dwc/terms/year> ?year . " +
                "?occurrence <http://rs.tdwg.org/dwc/terms/month> ?month . " +
                "?occurrence <http://rs.tdwg.org/dwc/terms/day> ?day . " +
                "}";
        Query query = QueryFactory.create(queryString);
        QueryExecution queryExec = QueryExecutionFactory.create(query, model);
        ResultSet resultSet = queryExec.execSelect();
        if (null != resultSet) {
            while (resultSet.hasNext()) {
                OccurenceData result = new OccurenceData();
                setFieldsFromQuery(result, resultSet.nextSolution());
                result.setLatitude("" + latitude);
                result.setLongitude("" + longitude);
                return result;
            }
        }
        return null;
    }

    private void setFieldsFromQuery(OccurenceData result, QuerySolution querySolution) {
        result.setDay(querySolution.getLiteral("day").toString());
        result.setMonth(querySolution.getLiteral("month").toString());
        result.setYear(querySolution.getLiteral("year").toString());
        result.setGeodeticDatum(querySolution.getLiteral("geodeticDatum").toString());
    }

    private void setFieldsFromJson(OccurenceData result, JSONObject occurence) {
        result.setLatitude(""+ occurence.getDouble("decimalLatitude"));
        result.setLongitude("" + occurence.getDouble("decimalLongitude"));
        result.setEntityURI("http://www.gbif.org/occurrence/" + occurence.getInt("key"));
        result.setSpecies(new SpeciesData(occurence.getString("scientificName"),occurence.getString("species")));
        result.setYear(""+ occurence.getInt("year"));
        result.setMonth(""+ occurence.getInt("month"));
        result.setDay(""+ occurence.getInt("day"));
        result.setGeodeticDatum(""+ occurence.get("geodeticDatum"));
    } 

    private JSONObject getResponse(HttpURLConnection mqlClient) throws IOException {
        InputStream is = mqlClient.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder response = new StringBuilder();
        while((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();
        return new JSONObject(response.toString());
    }
}