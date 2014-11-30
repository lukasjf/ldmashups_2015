package de.uni_potsdam.hpi.services;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.uni_potsdam.hpi.data.SpeciesData;

public class BBCService {
    public void getMediaFromBBC(SpeciesData species){
        try {
            URL url = new URL("http://www.bbc.co.uk/nature/life/Great_Tit.rdf");
            URLConnection urlConnection = url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            //your code
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse( in );
            NodeList clips = doc.getElementsByTagName("po:Clip");
            for (int i = 0; i < clips.getLength(); i++){
                Element clip = (Element)clips.item(i);
                System.out.println(clip.getAttribute("rdf:about"));
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args){
        new BBCService().getMediaFromBBC(null);
    }
}
