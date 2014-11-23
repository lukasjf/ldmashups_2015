package de.uni_potsdam.hpi.services;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Stephan
 * @version 14/11/23
 */
public class FreebaseTest {
    static FreebaseService fs;

    @Test
    public void testFindMidBySpeciesName() {
        String mid = fs.findMidByName("Parus Major");
        assertNotNull(mid);
        assertEquals("/m/01kzcv", mid);
    }

    @Test
    public void testMidIsNull() {
        assertNull(fs.findMidByName("Pabcdus Mabcdjor"));
    }

    @Test
    public void testFindImageUrlsByName() {
        List<String> urls = fs.findImageUrlsByName("Parus Major");
        assertNotNull(urls);
        for (String url : urls) {
            System.out.println(url);
        }
    }

    @Test
    public void testImageUrlsAreNull() {
        assertNull(fs.findImageUrlsByName("Pabcdus Mabcdjor"));
    }

    @Test
    public void testFindEquivalentWebpagesAreNull() {
        assertNull(fs.findEquivalentWebPagesByName("Pabcdus Mabcdjor"));
    }

    @Test
    public void testFindEquivalentWebpages() {
        List<String> websites = fs.findEquivalentWebPagesByMid("/m/01kzcv");
        assertNotNull(websites);
        assert(websites.contains("http://de.wikipedia.org/wiki/Kohlmeise"));
        for (String link : websites) {
            System.out.println(link);
        }
    }

    @BeforeClass
    public static void setUp() {
        fs = new FreebaseService();
    }
}
