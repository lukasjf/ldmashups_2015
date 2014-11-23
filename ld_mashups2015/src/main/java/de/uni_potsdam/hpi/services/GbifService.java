package de.uni_potsdam.hpi.services;

import de.uni_potsdam.hpi.data.Species;

import javax.ws.rs.Path;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

    public Species getSpeciesByLocation(double latitude1, double latitude2, double longitude1, double longitude2) {
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
            int responseCode = occurrenceClient.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            occurrenceClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean locationIsValid(double latitude1, double latitude2, double longitude1, double longitude2) {
        return (latitude1 <= 90.0 && latitude2 <= 90.0 && latitude1 >= -90.0 && latitude2 >= -90.0 &&
                longitude1 <= 180 && longitude2 <= 180 && longitude1 >= -180 && longitude2 >= -180);
    }

    public Species getSpeciesByLocation(double latitude, double longitude) {
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
            int responseCode = occurrenceClient.getResponseCode();
            System.out.println("Response Code : " + responseCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
