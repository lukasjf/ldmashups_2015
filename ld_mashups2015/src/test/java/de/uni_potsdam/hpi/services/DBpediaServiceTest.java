package de.uni_potsdam.hpi.services;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import de.uni_potsdam.hpi.data.Species;
import de.uni_potsdam.hpi.services.DBpediaService;

public class DBpediaServiceTest{

    Species parusMajor;
    
    @Before
    public void initializeParusMajor(){
        parusMajor = new Species();
        parusMajor.setScientificName("Parus major");
    }
    @Test
    public void testIncludeDataFromDBpedia() {
        new DBpediaService().includeDataFromDBpedia(parusMajor);
        assertNotNull("could not extract Thumbnail", parusMajor.getThumbnailURL());
        assertNotNull("could not extract Descritpion", parusMajor.getDescription());
    }
    @Test
    public void testAbstractSpeciesName() {
        new DBpediaService().includeDataFromDBpedia(parusMajor);
        assertTrue("did extract wrong information", parusMajor.getDescription().toLowerCase().contains("great tit"));
    }
}
