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
    private String basisOfRecord;
    private String entityURI;
    private String occurrenceID;
    private String year;
    private String month;
    private String day;

    /* RDF Encoding */
    public void encodeOccurrenceInRDF(Model model){
        Resource resource = model.createResource(getEntityURI());
        resource.addProperty(ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                ResourceFactory.createResource("http://rs.tdwg.org/dwc/terms/Occurrence"));
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/occurrenceID"),
                ResourceFactory.createTypedLiteral(occurrenceID, XSDDateType.XSDint));
        addCoordinatePropertiesToOccurrence(resource);
        addTimePropertiesToOccurrence(resource);
        addSpeciesToOccurrence(resource, model);
    }
    
    private void addSpeciesToOccurrence(Resource resource, Model model) {
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/associatedTaxa"),
                ResourceFactory.createResource(species.getEntityURI()));
        Resource taxon = model.createResource(species.getEntityURI());
        taxon.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/scientificName"), 
                ResourceFactory.createTypedLiteral(species.getScientificName(),XSDDatatype.XSDstring));
        taxon.addProperty(ResourceFactory.createProperty("http://dbpedia.org/property/binomial"), 
                species.getBinomial());
    }
    
    private void addTimePropertiesToOccurrence(Resource resource) {
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/year"),
                ResourceFactory.createTypedLiteral(year, XSDDatatype.XSDgYear));
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/month"),
                ResourceFactory.createTypedLiteral(month, XSDDatatype.XSDgMonth));
//        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/day"),
//                ResourceFactory.createTypedLiteral(day, XSDDatatype.XSDgDay));
    }

    private void addCoordinatePropertiesToOccurrence(Resource resource) {
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/decimalLatitude"),
                ResourceFactory.createTypedLiteral(latitude, XSDDatatype.XSDdouble));
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/decimalLongitude"),
                ResourceFactory.createTypedLiteral(longitude, XSDDatatype.XSDdouble));
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/geodeticDatum"), 
                ResourceFactory.createTypedLiteral(geodeticDatum, XSDDatatype.XSDstring));
    }

    /* BEGIN: Getter and Setter */
    public String getDay() {
        return day;
    }
    public void setDay(String day) {
        this.day = day;
    }
    public String getMonth() {
        return month;
    }
    public void setMonth(String month) {
        this.month = month;
    }
    public String getYear() {
        return year;
    }
    public void setYear(String year) {
        this.year = year;
    }
    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public String getGeodeticDatum() {
        return geodeticDatum;
    }
    public void setGeodeticDatum(String geodeticDatum) {
        this.geodeticDatum = geodeticDatum;
    }
    public SpeciesData getSpecies() {
        return species;
    }
    public void setSpecies(SpeciesData occuredSpecies) {
        this.species = occuredSpecies;
    }
    public String getBasisOfRecord() {
        return basisOfRecord;
    }
    public void setBasisOfRecord(String basisOfRecord) {
        this.basisOfRecord = basisOfRecord;
    }
    public String getEntityURI() {
        return entityURI;
    }
    public void setEntityURI(String entityURI) {
        this.entityURI = entityURI;
    }
    /* END: Getter and Setter */


    public String getOccurrenceID() {
        return occurrenceID;
    }


    public void setOccurrenceID(String occurrenceID) {
        this.occurrenceID = occurrenceID;
    }
}
