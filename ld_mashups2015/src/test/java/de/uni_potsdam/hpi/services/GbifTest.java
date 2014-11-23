package de.uni_potsdam.hpi.services;

import de.uni_potsdam.hpi.data.Species;
import de.uni_potsdam.hpi.services.GbifService;
import junit.framework.TestCase;

/**
 * This class provides methods to send queries to the gbif-api.
 * api.gbif.org/v1/
 *
 * @author Stephan
 * @version 2014/11/11
 */
public class GbifTest extends TestCase {
    /**
     * Tests if the returned species has a valid scientific Name
     */
    public void testSpeciesScientificName() {
        GbifService gbif = new GbifService();
        Species species = gbif.getSpeciesByLocation(12.0, 13.0, 51.0, 52.0);
        assertNotNull(species);
        assertNotNull(species.getScientificName());
    }

    /**
     * In case of a invalid Location null should be returned
     */
    public void testSpeciesIsNull() {
        GbifService gbif = new GbifService();
        Species species = gbif.getSpeciesByLocation(-100, -200);
        assert(null == species);
    }
}
