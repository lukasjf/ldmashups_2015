package de.uni_potsdam.hpi.data;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Stephan on 11.12.2014.
 */
@XmlRootElement
public class OccurrenceXML {
    public double longitude;
    public double latitude;
    public String thumbnailURL;

    public OccurrenceXML(double longitude, double latitude, String thumbnailURL) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.thumbnailURL = thumbnailURL;
    }
}
