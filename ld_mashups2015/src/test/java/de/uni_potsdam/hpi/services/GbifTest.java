package de.uni_potsdam.hpi.services;

import org.junit.Test;

import de.uni_potsdam.hpi.data.Species;
import de.uni_potsdam.hpi.services.GbifService;
import static org.junit.Assert.*;

/**
 * This class provides methods to send queries to the gbif-api.
 * api.gbif.org/v1/
 *
 * @author Stephan
 * @version 2014/11/11
 */


public class GbifTest{
    /**
     * Tests if the returned species has a valid scientific Name
     */
    @Test
    public void testSpeciesScientificName() {
        GbifService gbif = new GbifService();
        Species species = gbif.getSpeciesByLocation(12.0, 13.0, 51.0, 52.0);
        assertNotNull("species was null", species);
        assertNotNull(species.getScientificName(),"scientificName was not set");
    }

    /**
     * In case of a invalid Location null should be returned
     */
    @Test
    public void testLocationIsValid() {
        GbifService gbif = new GbifService();
        assertEquals(null, gbif.getSpeciesByLocation(-181, 0),"allows too low latitude");
        assertEquals(null, gbif.getSpeciesByLocation(181, 0),"allows too high latitude");
        assertEquals(null, gbif.getSpeciesByLocation(0, 90),"allows too low longitude");
        assertEquals(null, gbif.getSpeciesByLocation(0, -90),"allows too high longitude");
    }
}
