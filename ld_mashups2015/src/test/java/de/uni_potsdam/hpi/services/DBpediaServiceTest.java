package de.uni_potsdam.hpi.services;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import de.uni_potsdam.hpi.data.SpeciesData;
import de.uni_potsdam.hpi.services.DBpediaService;

public class DBpediaServiceTest{

    SpeciesData parusMajor;
    
    @Before
    public void initializeParusMajor(){
        parusMajor = new SpeciesData("Parus major, Linnaeus, 1758", "Parus major");
    }
    @Test
    public void testIncludeDataFromDBpedia() {
        new DBpediaService().includeDataFromDBpedia(parusMajor);
        assertNotNull("could not extract Thumbnail", parusMajor.getThumbnailURL());
        assertNotNull("could not extract Descritpion", parusMajor.getDescription());
        assertNotNull("could not extract Phylum", parusMajor.getPhylum());
        assertNotNull("could not extract Kingdom", parusMajor.getKingdom());
        assertNotNull("could not extract Family", parusMajor.getFamily());
        assertNotNull("could not extract Class", parusMajor.getTaxonClass());
    }
    @Test
    public void testAbstractSpeciesName() {
        new DBpediaService().includeDataFromDBpedia(parusMajor);
        assertTrue("did extract wrong information", parusMajor.getDescription().toLowerCase().contains("great tit"));
    }
}
