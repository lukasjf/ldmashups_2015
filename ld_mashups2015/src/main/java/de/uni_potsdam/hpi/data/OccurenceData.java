package de.uni_potsdam.hpi.data;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class OccurenceData {
    private String latitude;
    private String longitude;
    private String geodeticDatum;
    private SpeciesData species;
    private String basisOfRecord;
    private String entityURI;
    private String year;
    private String month;
    private String day;

    public void encodeSpeciesInRDF(){
        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource(getEntityURI());
        addCoordinatePropertiesToOccurence(resource);
        addTimePropertiesToOccurence(resource);
        model.write(System.out, "N-Triples");
    }

    private void addTimePropertiesToOccurence(Resource resource) {
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/year"),
                ResourceFactory.createProperty(year));
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/month"),
                ResourceFactory.createProperty(month));
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/day"),
                ResourceFactory.createProperty(day));
    }

    private void addCoordinatePropertiesToOccurence(Resource resource) {
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/decimalLatitude"),
                ResourceFactory.createProperty(latitude));
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/decimalLongitude"),
                ResourceFactory.createProperty(longitude));
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/geodeticDatum"),
                ResourceFactory.createProperty(geodeticDatum));
    }
    
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
}
