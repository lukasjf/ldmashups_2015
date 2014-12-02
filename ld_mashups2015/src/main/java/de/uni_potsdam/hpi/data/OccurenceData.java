package de.uni_potsdam.hpi.data;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class OccurenceData {
    public static final String FILE_URL = "resource/rdf/occurrence.rdf";
    private String latitude;
    private String longitude;
    private String geodeticDatum;
    private SpeciesData species;
    private String basisOfRecord;
    private String entityURI;
    private String year;
    private String month;
    private String day;

    /* RDF Encoding */
    public void encodeOccurrenceInRDF(){
        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource(getEntityURI());
        addCoordinatePropertiesToOccurence(resource);
        addTimePropertiesToOccurence(resource);
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/taxonID"),
                ResourceFactory.createResource(species.getEntityURI()));
        writeRdfToFile(model);
        model.write(System.out, "N-Triples");
    }

    private void writeRdfToFile(Model model) {
        File f = new File(FILE_URL);
        try {
            f.createNewFile();
            FileWriter fw = new FileWriter(f, true);
            model.write(fw, "RDF/XML-ABBREV");
        } catch (IOException e) {
            System.err.println("Could not write File");
            e.printStackTrace();
        }
    }

    private void addTimePropertiesToOccurence(Resource resource) {
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/year"),
                year);
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/month"),
                month);
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/day"),
                day);
    }

    private void addCoordinatePropertiesToOccurence(Resource resource) {
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/decimalLatitude"),
                latitude);
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/decimalLongitude"),
                longitude);
        /*resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/geodeticDatum"),
                geodeticDatum);*/
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
}
