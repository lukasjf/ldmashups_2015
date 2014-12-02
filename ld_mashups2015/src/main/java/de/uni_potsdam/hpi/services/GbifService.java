package de.uni_potsdam.hpi.services;

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

    public SpeciesData getSpeciesByLocation(double latitude1, double latitude2, double longitude1, double longitude2) {
        SpeciesData result = null;
        if (!locationIsValid(latitude1, latitude2, longitude1, longitude2)) {
            System.err.println("Invalid Location");
            return null;
        }
        URL url = null;
        try {
            url = new URL(occurenceApiString + "search?decimalLongitude=" + longitude1 + "," + longitude2 + "&" +
                    "decimalLatitude=" + latitude1 + "," + latitude2);
            HttpURLConnection occurrenceClient = (HttpURLConnection)url.openConnection();
            occurrenceClient.setRequestMethod("GET");
            JSONObject response = getResponse(occurrenceClient);
            JSONObject firstSpecies = response.getJSONArray("results").getJSONObject(0);
            result = new SpeciesData(firstSpecies.getString("scientificName"),
                    firstSpecies.getString("species"));
            occurrenceClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean locationIsValid(double latitude1, double latitude2, double longitude1, double longitude2) {
        return (latitude1 <= 90.0 && latitude2 <= 90.0 && latitude1 >= -90.0 && latitude2 >= -90.0 &&
                longitude1 <= 180 && longitude2 <= 180 && longitude1 >= -180 && longitude2 >= -180);
    }

    public OccurenceData getOccurenceForLocation(double latitude, double longitude) {
        OccurenceData result = null;
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
            setFields(result,occurence);
            occurrenceClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void setFields(OccurenceData result, JSONObject occurence) {
        result.setLatitude(""+ occurence.getDouble("decimalLatitude"));
        result.setLatitude(""+ occurence.getDouble("decimalLongitude"));
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