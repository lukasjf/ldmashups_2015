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

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }
}
