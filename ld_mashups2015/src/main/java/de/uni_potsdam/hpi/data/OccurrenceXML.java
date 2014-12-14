package de.uni_potsdam.hpi.data;


import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * Created by Stephan on 11.12.2014.
 */
@XmlRootElement(name = "occurrence")
public class OccurrenceXML {
    private double longitude;
    private double latitude;
    private String thumbnailURL;

    public OccurrenceXML(double longitude, double latitude, String thumbnailURL) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.thumbnailURL = thumbnailURL;
    }

    @XmlValue
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @XmlValue
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @XmlValue
    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    @Override
    public String toString() {
        return "Occurrence [longitude=" + longitude + ", latitude=" + latitude + ", thumbnailURL=" + thumbnailURL + "]";
    }
}
