package de.uni_potsdam.hpi.data;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class OccurrenceData {
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
        addCoordinatePropertiesToOccurrence(resource);
        addTimePropertiesToOccurrence(resource);
        addSpeciesToOccurrence(resource);
        model.write(System.out, "TURTLE");
        writeRdfToFile(model);
    }

    
    private void writeRdfToFile(Model model) {
        File f = new File(FILE_URL);
        try {
            f.createNewFile();
            FileWriter fw = new FileWriter(f);
            model.write(fw, "RDF/XML-ABBREV");
        } catch (IOException e) {
            System.err.println("Could not write File");
            e.printStackTrace();
        }
    }
    
    private void addSpeciesToOccurrence(Resource resource) {
        resource.addProperty(ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/associatedTaxa"),
                ResourceFactory.createResource(species.getEntityURI()));
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
        resource.addProperty(
                ResourceFactory.createProperty("http://rs.tdwg.org/dwc/terms/geodeticDatum"), geodeticDatum);
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