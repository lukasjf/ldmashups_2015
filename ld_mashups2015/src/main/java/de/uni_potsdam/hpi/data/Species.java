package de.uni_potsdam.hpi.data;

import java.util.LinkedList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.*;

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
    private String Description;
    private List<String> equivalentWebpages;
    private List<String> imageUrls;

    public String getEntityURI() {
        return entityURI;
    }

    public void setEntityURI(String entityURI) {
        this.entityURI = entityURI;
    }

    private String entityURI;

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

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public List<String> getEquivalentWebpages() {
        if (null == imageUrls) {
            return new LinkedList<String>();
        }
        return equivalentWebpages;
    }

    public void setEquivalentWebpages(List<String> equivalentWebpages) {
        this.equivalentWebpages = equivalentWebpages;
    }

    public List<String> getImageUrls() {
        if (null == imageUrls) {
            return new LinkedList<String>();
        }
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
    
    public void encodeSpeciesInRDF(){
        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource(getEntityURI());
        addNamePropertiesToSpecies(resource);
        addIdentificationToSpecies(resource);
        addMediaToSpecies(resource);
        addTaxonToSpecies(resource);
        addDescriptionToSpecies(resource);
        model.write(System.out, "N-Triples");
    }

    private void addTaxonToSpecies(Resource resource) {
        resource.addProperty(
                ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/binomial"), getScientificName());
        
    }

    private void addMediaToSpecies(Resource resource) {
        if (getThumbnailURL() != null) {
            resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/associatedMedia"), ResourceFactory.createResource(getThumbnailURL()));
        }
        for (String url : getImageUrls()) {
            resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/associatedMedia"), ResourceFactory.createResource(url));
        }
        
    }

    private void addIdentificationToSpecies(Resource resource) {
        // TODO Auto-generated method stub
        
    }

    private void addNamePropertiesToSpecies(Resource resource) {
        resource.addProperty(
                ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/binomial"), getScientificName());
        resource.addProperty(
                ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/scientificName"), getScientificName());
    }

    private void addDescriptionToSpecies(Resource resource) {
        
    }
}
