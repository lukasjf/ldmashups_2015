package de.uni_potsdam.hpi.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import de.uni_potsdam.hpi.data.SpeciesData;

/**
 * This class was used to retrieve MediaInformation from the wikimedia.
 * It is recommended to use the CommonsDbpediaService instead.
 */
@Deprecated
public class WikimediaService {
    static String imageListString = 
            "http://commons.wikimedia.org/w/api.php?action=query&titles=%s&prop=images&continue=&format=json";
    static String imageURLString = 
            "http://commons.wikimedia.org/w/api.php?action=query&titles=%s&prop=imageinfo&iiprop=url&continue=&format=json";


    public void includeImagesFromWikimedia(SpeciesData species){ 
        try{
            List<String> imageURLs = new LinkedList<String>();
            JSONArray images = getImageNames(species.getBinomial());
            for(int i = 0; i < images.length(); i++){
                JSONObject o = images.getJSONObject(i);
                imageURLs.add(getImageURLForImageName(o.getString("title")));
            }
            species.setImageUrls(imageURLs);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    private String getImageURLForImageName(String imageName) {
        String imageUnderscoreName = imageName.replace(' ', '_');
        try {
            URL url = new URL(String.format(imageURLString, imageUnderscoreName));
            HttpURLConnection wikimediaConnection = (HttpURLConnection)url.openConnection();
            wikimediaConnection.setRequestMethod("GET");
            JSONObject response = getResponse(wikimediaConnection);
            JSONObject image = parseToImageURL(response);
            return image.getString("url");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private JSONObject parseToImageURL(JSONObject response) {
        JSONObject temp = response.getJSONObject("query").getJSONObject("pages");
        String[] s = JSONObject.getNames(temp);
        return (temp.getJSONObject(s[0]).getJSONArray("imageinfo").getJSONObject(0));
    }

    private JSONArray getImageNames(String binomial) {
        System.out.println(binomial);
        JSONArray images = null;
        try {
            URL url = new URL(String.format(imageListString, binomial.replace(' ', '_')));
            HttpURLConnection wikimediaConnection = (HttpURLConnection)url.openConnection();
            wikimediaConnection.setRequestMethod("GET");
            System.out.println(wikimediaConnection.getResponseCode());
            JSONObject response = getResponse(wikimediaConnection);
            images = parseToImageArray(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return images;
    }

    private JSONArray parseToImageArray(JSONObject response) {
       JSONObject temp = response.getJSONObject("query").getJSONObject("pages");
       String[] s = JSONObject.getNames(temp);
       JSONArray images = temp.getJSONObject(s[0]).getJSONArray("images");
       return images;
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
