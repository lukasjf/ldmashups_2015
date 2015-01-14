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
    Resource resource;
	private String name;

    public SpeciesData(String scientificName, String binomial) {
        this.scientificName = scientificName;
        this.binomial = binomial;
        resource = ModelFactory.createDefaultModel().createResource();
    }

    public SpeciesData(Model model, String entityURI) {
        this.entityURI = entityURI;
        resource = model.createResource(getEntityURI());
        resource.addProperty(ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                ResourceFactory.createResource("http://rs.tdwg.org/dwc/terms/Taxon"));
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
        if(null != scientificName){
            this.scientificName = scientificName;
            resource.addProperty(
                    ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/scientificName"), getScientificName());
        }       
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        if (null != thumbnailURL) {
            this.thumbnailURL = thumbnailURL;
            resource.addProperty(ResourceFactory.createProperty("http://dbpedia.org/ontology/thumbnail"), 
                    ResourceFactory.createResource(thumbnailURL));
        }
        else{
            resource.addProperty(ResourceFactory.createProperty("http://dbpedia.org/ontology/thumbnail"), "");
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if(null == description){
            this.description = "No abstract found";
            resource.addProperty(ResourceFactory.createProperty("http://dbpedia.org/ontology/abstract"), 
                    "No abstract found");
        }
        else{
            this.description = description;
            resource.addProperty(ResourceFactory.createProperty("http://dbpedia.org/ontology/abstract"), 
                    description);
        }
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
        if(null != imageUrls){
            this.imageUrls = imageUrls;
            for (String url : imageUrls) {
                resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/associatedMedia"), 
                        ResourceFactory.createResource(url));
            }
        }        
    }

    public String getKingdom() {
        return kingdom;
    }

    public void setKingdom(String kingdom) {
        if (null != kingdom && !kingdom.isEmpty()){
            this.kingdom = kingdom;
            resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/kingdom"),
                    ResourceFactory.createResource(kingdom));
        }
    }

    public String getPhylum() {
        return phylum;
    }

    public void setPhylum(String phylum) {
        if (null != phylum && !phylum.isEmpty()){
            this.phylum = phylum;
            resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/phylum"),
                    ResourceFactory.createResource(phylum));          
        }           
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        if(null != family){
            this.family = family;
            resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/family"),
                    ResourceFactory.createResource(family));
        }       
    }

    public String getTaxonClass() {
        return taxonClass;
    }

    public void setTaxonClass(String taxonClass) {
        if(null != taxonClass){
            this.taxonClass = taxonClass;
            resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/class"),
                    ResourceFactory.createResource(taxonClass));
        }       
    }

    public String getBinomial() {
        return binomial;
    }

    public void setBinomial(String binomial) {
        if(null != binomial){
            this.binomial = binomial;
            resource.addProperty(
                    ResourceFactory.createProperty("http://dbpedia.org/property/binomial"), binomial);
        }  
    }
    
    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        if(null != order){
            this.order = order;
            resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/order"),
                ResourceFactory.createResource(order));
        }
    }

    public String getGenus() {
        return genus;
    }

    public void setGenus(String genus) {
        if(null != genus){
            this.genus = genus;
            resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/genus"),
                    ResourceFactory.createResource(genus));          
        }
    }

    public String getdBpediaURI() {
        return dBpediaURI;
    }

    public void setdBpediaURI(String dBpediaURI) {
        if(null != dBpediaURI){
            this.dBpediaURI = dBpediaURI;
            resource.addProperty(ResourceFactory.createProperty("http://www.w3.org/2002/07/owl#sameAs"),
                    ResourceFactory.createResource(dBpediaURI));
        }       
    }
    
    /* END: Getter and Setter */
}
