package de.uni_potsdam.hpi.services;

import de.uni_potsdam.hpi.data.SpeciesData;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Stpehan on 22.01.2015.
 */
public class SpeciesDataTest {
    @Test
    public void testRegex() {
        SpeciesData species = new SpeciesData("Parus Major","Parus Major");
        species.setSynonyme("Parus caeruleus (Linnaeus, 1758)");
        String s = species.getShortSynonyme();
        assertEquals("Parus caeruleus", s);

    }


    @Test
    public void testRegex2() {
        SpeciesData species = new SpeciesData("Parus Major","Parus Major");
        species.setSynonyme("Parus caeruleus");
        String s = species.getShortSynonyme();
        assertEquals("Parus caeruleus", s);

    }
}
