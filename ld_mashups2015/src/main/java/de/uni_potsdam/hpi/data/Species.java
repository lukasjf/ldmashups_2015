package de.uni_potsdam.hpi.data;

/**
 * Holds all the informations about a species
 *
 * @author Stpehan
 * @version 11/11/14
 */
public class Species {
    /** Holds the scientific Name of the species */
    private String scientificName;
    private String thumbnailURL;
    private String abstractDescription;

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getAbstractDescription() {
        return abstractDescription;
    }

    public void setAbstractDescription(String abstractDescription) {
        this.abstractDescription = abstractDescription;
    }
}
