package de.uni_potsdam.hpi.services;

import de.uni_potsdam.hpi.data.SpeciesData;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * This class checks for different Webresources and adds the link to the species.
 */
public class LinkService {

    public void includeExternalLinks(SpeciesData species) {
        if (species.getdBpediaURI() != null) {
            List<String> links = new LinkedList<String>();
            addWikipediaLinkIfExists(links, species);
            addEOFLinkIfExists(links, species);
            addBBCLinkIfExists(links, species);
            links.add(species.getEntityURI());
            species.setEquivalentWebpages(links);
        }
    }

    private void addEOFLinkIfExists(List<String> links, SpeciesData species) {
        String uri = "http://eol.org/search?q=" + species.getBinomial();
        URL url;
        try {
            url = new URL(uri);
            HttpURLConnection eolClient = (HttpURLConnection) url.openConnection();
            eolClient.setRequestMethod("GET");
            int responseCode = eolClient.getResponseCode();
            if (302 != responseCode && 200 != responseCode) {
                return;
            }
            links.add(eolClient.getURL().toString());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void addBBCLinkIfExists(List<String> links, SpeciesData species) {
        String uri = "http://bbc.co.uk/nature/life/" + species.getName().replace(" ", "_").replace("@en", "");
        URL url;
        try {
            url = new URL(uri);
            HttpURLConnection bbcClient = (HttpURLConnection) url.openConnection();
            bbcClient.setRequestMethod("GET");
            int responseCode = bbcClient.getResponseCode();
            if (200 != responseCode && 302 != responseCode && 303 != responseCode) {
                return;
            }
            links.add(uri);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void addWikipediaLinkIfExists(List<String> links, SpeciesData species) {
        if (species.getdBpediaURI() != null) {
            links.add(species.getdBpediaURI().replace("dbpedia.org/resource/", "en.wikipedia.org/wiki/"));
        }
    }
}
