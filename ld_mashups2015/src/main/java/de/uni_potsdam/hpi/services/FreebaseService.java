package de.uni_potsdam.hpi.services;

import de.uni_potsdam.hpi.data.SpeciesData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

public class FreebaseService {
    private static final String MQL_API = "https://www.googleapis.com/freebase/v1/mqlread?query=";
    private static final String TOPIC_API = "https://www.googleapis.com/freebase/v1/topic%s?filter=/common/topic/";

    public String findMidByName(String binomial) {
        String queryJson = "[{"
                + "\"mid\": null, "
                + "\"name\": null, "
                + "\"type\": \"/biology/organism_classification\", "
                + "\"scientific_name\": \"" + binomial + "\""
                + "}]";
        URL url;
        try {
            url = new URL(MQL_API + URLEncoder.encode(queryJson, "UTF-8"));
            HttpURLConnection mqlClient = (HttpURLConnection)url.openConnection();
            mqlClient.setRequestMethod("GET");
            int responseCode = mqlClient.getResponseCode();
            if (200 != responseCode) {
                throw new Exception("Request Failed!");
            }
            JSONObject result = getResponse(mqlClient);
            JSONArray resultArray = result.getJSONArray("result");
            JSONObject firstResult = resultArray.getJSONObject(0);
            String mid = firstResult.getString("mid");
            mqlClient.disconnect();
            return mid;
        } catch (Exception e) {
            System.err.println("Could not send querry to Freebase");
            e.printStackTrace();
        }
        return null;
    }

    public List<String> findImageUrlsByName(String name) {
        String mid = findMidByName(name);
        return findImageUrlsByMid(mid);
    }

    private List<String> findImageUrlsByMid(String mid) {
        if (null == mid) {
            return null;
        }
        try {
            JSONObject result = findTopicInformationByMid(mid);
            JSONArray images = result.getJSONObject("property")
                    .getJSONObject("/common/topic/image")
                    .getJSONArray("values");
            List<String> imageUrls = new LinkedList<String>();
            for (int i = 0; i < images.length(); i++) {
                imageUrls.add("https://www.googleapis.com/freebase/v1/image" + images.getJSONObject(i).getString("id") +
                        "?maxwidth=480");
            }
            return imageUrls;
        } catch (Exception e) {
            System.err.println("Could not parse result from Freebase");
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject findTopicInformationByMid(String mid) {
        URL url;
        try {
            url = new URL(String.format(TOPIC_API, mid));
            HttpURLConnection topicClient = (HttpURLConnection) url.openConnection();
            topicClient.setRequestMethod("GET");
            int responseCode = topicClient.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            if (200 != responseCode) {
                throw new Exception("Request Failed!");
            }
            return getResponse(topicClient);
        } catch (Exception e) {
            System.err.println("Could not send querry to Freebase");
            e.printStackTrace();
        }
        return null;
    }

    public List<String> findEquivalentWebPagesByName(String name) {
        String mid = findMidByName(name);
        return findEquivalentWebPagesByMid(mid);
    }

    public List<String> findEquivalentWebPagesByMid(String mid) {
        if (null == mid) {
            return null;
        }
        JSONObject topic = findTopicInformationByMid(mid);
        JSONArray urls = topic.getJSONObject("property").getJSONObject("/common/topic/topic_equivalent_webpage").getJSONArray("values");
        List<String> results = new LinkedList<String>();
        for (int i = 0; i < urls.length(); i++) {
            results.add(urls.getJSONObject(i).getString("value"));
        }
        return results;
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


    public void includeDataFromFreebase(SpeciesData species) {
        String mid = findMidByName(species.getBinomial());
        species.setEquivalentWebpages(findEquivalentWebPagesByMid(mid));
        species.setImageUrls(findImageUrlsByMid(mid));
    }
}