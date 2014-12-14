package de.uni_potsdam.hpi.data;

import com.hp.hpl.jena.rdf.model.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Holds all the information about a species
 *
 * @author Stephan
 * @version 11/11/14
 */
public class SpeciesData {;
    /** Holds the scientific Name of the species */
    private String entityURI;
    private String dBpediaURI;
    private String scientificName;
    private String thumbnailURL;
    private String description;
    private String kingdom;
    private String phylum;
    private String family;
    private String binomial;
    private String order;
    private String genus;
    private String taxonClass;
    private List<String> equivalentWebpages;
    private List<String> imageUrls;

    public SpeciesData(String scientificName, String binomial) {
        this.scientificName = scientificName;
        this.binomial = binomial;
    }

    public SpeciesData(String entityURI) {
        this.entityURI = entityURI;
    }

    /* BEGIN: RDF encoding */

    public void encodeSpeciesInRDF(Model model){
        Resource resource = model.createResource(getEntityURI());
        resource.addProperty(ResourceFactory.createProperty("http://www.w3.org/2002/07/owl#sameAs"),
                ResourceFactory.createResource(dBpediaURI));
        resource.addProperty(ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                ResourceFactory.createResource("http://rs.tdwg.org/dwc/terms/Taxon"));
        addNamePropertiesToSpecies(resource);
        addIdentificationToSpecies(resource);
        addMediaToSpecies(resource);
        addTaxonToSpecies(resource);
        addAbstractToSpecies(resource);
    }

    private void addTaxonToSpecies(Resource resource) {
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/kingdom"),
                ResourceFactory.createResource(kingdom));
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/phylum"),
                ResourceFactory.createResource(phylum));
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/family"),
                ResourceFactory.createResource(family));
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/class"),
                ResourceFactory.createResource(taxonClass));
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/order"),
                ResourceFactory.createResource(order));
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/genus"),
                ResourceFactory.createResource(genus));
//        resource.addProperty(
//                ResourceFactory.createProperty("http://dbpedia.org/property/binomial"), getBinomial());
    }

    private void addMediaToSpecies(Resource resource) {
        if (null != thumbnailURL) {
            resource.addProperty(ResourceFactory.createProperty("http://dbpedia.org/ontology/thumbnail"), thumbnailURL);
        }
        for (String url : imageUrls) {
            resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/associatedMedia"), url);
        }
    }

    private void addIdentificationToSpecies(Resource resource) {
        // TODO Auto-generated method stub
        
    }

    private void addNamePropertiesToSpecies(Resource resource) {
//        resource.addProperty(
//                ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/binomial"), getScientificName());
//        resource.addProperty(
//                ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/scientificName"), getScientificName());
    }

    private void addAbstractToSpecies(Resource resource) {
        resource.addProperty(ResourceFactory.createProperty("http://dbpedia.org/ontology/abstract"), getDescription());
    }

    /* BEGIN: Getter and Setter */
    public String getEntityURI() {
        return entityURI;
    }

    public void setEntityURI(String entityURI) {
        this.entityURI = entityURI;
    }

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

    public String getBinomial() {
        return binomial;
    }

    public void setBinomial(String binomial) {
        this.binomial = binomial;
    }
    
    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getGenus() {
        return genus;
    }

    public void setGenus(String genus) {
        this.genus = genus;
    }

    public String getdBpediaURI() {
        return dBpediaURI;
    }

    public void setdBpediaURI(String dBpediaURI) {
        this.dBpediaURI = dBpediaURI;
    }
    
    /* END: Getter and Setter */
}
