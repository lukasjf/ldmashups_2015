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
public class Species {;
    /** Holds the scientific Name of the species */
    private String scientificName;
    private String thumbnailURL;
    private String description;
    private String kingdom;
    private String phylum;
    private String family;
    private String taxonClass;
    private List<String> equivalentWebpages;
    private List<String> imageUrls;

    public Species(String scientificName) {
        this.scientificName = scientificName;
    }

    /* BEGIN: Getter and Setter */
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
        return description;
    }

    public void setDescription(String Description) {
        this.description = Description;
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

    public String getKingdom() {
        return kingdom;
    }

    public void setKingdom(String kingdom) {
        if (null != kingdom && !kingdom.isEmpty())
            this.kingdom = kingdom;
    }

    public String getPhylum() {
        return phylum;
    }

    public void setPhylum(String phylum) {
        if (null != phylum && !phylum.isEmpty())
            this.phylum = phylum;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getTaxonClass() {
        return taxonClass;
    }

    public void setTaxonClass(String taxonClass) {
        this.taxonClass = taxonClass;
    }
    /* END: Getter and Setter */

    /* BEGIN: RDF encoding */
    public void encodeSpeciesInRDF(){
        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource(getEntityURI());
        addNamePropertiesToSpecies(resource);
        addIdentificationToSpecies(resource);
        addMediaToSpecies(resource);
        addTaxonToSpecies(resource);
        addAbstractToSpecies(resource);
        model.write(System.out, "N-Triples");
    }

    private void addTaxonToSpecies(Resource resource) {
        addKingdomToSpecies(resource);
        addPhylumToSpecies(resource);
        addFamilyToSpecies(resource);
        addTaxonClassToSpecies(resource);
        resource.addProperty(
                ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/binomial"), getScientificName());
        
    }

    private void addTaxonClassToSpecies(Resource resource) {
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/class"),
                ResourceFactory.createProperty(taxonClass));
    }

    private void addFamilyToSpecies(Resource resource) {
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/family"),
                ResourceFactory.createProperty(family));
    }

    private void addPhylumToSpecies(Resource resource) {
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/phylum"),
                ResourceFactory.createProperty(phylum));
    }

    private void addKingdomToSpecies(Resource resource) {
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/kingdom"),
                ResourceFactory.createProperty(kingdom));
    }

    private void addMediaToSpecies(Resource resource) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < imageUrls.size(); i++) {
            sb.append(imageUrls.get(i));
            if (i < imageUrls.size() - 1) {
                sb.append(" | ");
            }
        }
        if (getThumbnailURL() != null) {
            sb.append(" | ");
            sb.append(getThumbnailURL());
        }
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/associatedMedia"), sb.toString());
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

    private void addAbstractToSpecies(Resource resource) {
        resource.addProperty(ResourceFactory.createProperty("http://dbpedia.org/ontology/abstract"), getDescription());
    }
}
