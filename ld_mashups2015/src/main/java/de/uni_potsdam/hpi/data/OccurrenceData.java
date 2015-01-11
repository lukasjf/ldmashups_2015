package de.uni_potsdam.hpi.data;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.datatypes.xsd.impl.XSDDateType;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class OccurrenceData {
    public static final String FILE_URL = "resource/rdf/occurrence.rdf";
    private String latitude;
    private String longitude;
    private String geodeticDatum;
    private SpeciesData species;
    private String entityURI;
    private String occurrenceID;
    private String year;
    private String month;
    private String day;
    Resource resource;
    
    public OccurrenceData(){
    
    }
    
    public OccurrenceData(Model model, String URI){

        this.setEntityURI(URI);
        resource = model.createResource(URI);
        resource.addProperty(ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                ResourceFactory.createResource("http://rs.tdwg.org/dwc/terms/Occurrence"));
    }

    /* BEGIN: Getter and Setter */
    public String getDay() {
        return day;
    }
    public void setDay(String day) {
        if(null != day){
            this.day = day;
            resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/day"),
                ResourceFactory.createTypedLiteral(day, XSDDatatype.XSDgDay));
        }
    }
    public String getMonth() {
        return month;
    }
    public void setMonth(String month) {
        if(null != year){
            this.month = month;
            resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/month"),
                ResourceFactory.createTypedLiteral(month, XSDDatatype.XSDgMonth));
        }
    }
    public String getYear() {
        return year;
    }
    public void setYear(String year) {
        if (null != year){
            this.year = year;
            resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/year"),
                ResourceFactory.createTypedLiteral(year, XSDDatatype.XSDgYear));
        }  
    }
    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        if(null != latitude){
            this.latitude = latitude;
            resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/decimalLatitude"),
                ResourceFactory.createTypedLiteral(latitude, XSDDatatype.XSDdouble));
        }
    }
    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        if(null != latitude){
            this.longitude = longitude;
            resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/decimalLongitude"),
                ResourceFactory.createTypedLiteral(longitude, XSDDatatype.XSDdouble));
        }        
    }
    public String getGeodeticDatum() {
        return geodeticDatum;
    }
    public void setGeodeticDatum(String geodeticDatum) {
        if(null != geodeticDatum){
            this.geodeticDatum = geodeticDatum;
            resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/geodeticDatum"), 
                ResourceFactory.createTypedLiteral(geodeticDatum, XSDDatatype.XSDstring));
        }        
    }
    public SpeciesData getSpecies() {
        return species;
    }
    public void setSpecies(SpeciesData occurredSpecies) {
        if(null != occurredSpecies){
            this.species = occurredSpecies;
            resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/associatedTaxa"),
                    ResourceFactory.createResource(species.getEntityURI()));            
        }
    }

    public String getEntityURI() {
        return entityURI;
    }
    public void setEntityURI(String entityURI) {
        this.entityURI = entityURI;
    }

    public String getOccurrenceID() {
        return occurrenceID;
    }

    public void setOccurrenceID(String occurrenceID) {
        if(null != occurrenceID){
            this.occurrenceID = occurrenceID;
            resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/occurrenceID"),
                ResourceFactory.createTypedLiteral(occurrenceID, XSDDateType.XSDint));
        }        
    }
}
