package de.uni_potsdam.hpi.services;

import org.junit.Test;

import de.uni_potsdam.hpi.data.SpeciesData;
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
        SpeciesData species = gbif.getSpeciesByLocation(51.0, 53.0, 12.0, 15.0);
        assertNotNull("species was null", species);
        assertNotNull("scientificName was not set", species.getScientificName());
        assertNotNull("binomial was not set", species.getScientificName());
    }

    /**
     * In case of a invalid Location null should be returned
     */
    @Test
    public void testLocationIsValid() {
        GbifService gbif = new GbifService();
        assertEquals("allows too low latitude", gbif.getSpeciesByLocation(0, -181), null);
        assertEquals("allows too high latitude", gbif.getSpeciesByLocation(0, 181), null);
        assertEquals("allows too low longitude", gbif.getSpeciesByLocation(91, 0), null);
        assertEquals("allows too high longitude", gbif.getSpeciesByLocation(-91, 0), null);
    }
}
