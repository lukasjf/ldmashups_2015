package de.uni_potsdam.hpi.services;

import de.uni_potsdam.hpi.data.SpeciesData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LinkServiceTest {

    SpeciesData parus;
    LinkService ls;

    @Before
    public void setUp() {
        parus = new SpeciesData("Parus major", "Parus major");
        parus.setdBpediaURI("http://dbpedia.org/resource/Great_tit");
        parus.setName("Great tit");
        ls = new LinkService();
    }

    @Test
    public void testAddEOFLinkIfExist() {
        ls.includeExternalLinks(parus);
        Assert.assertTrue(parus.getEquivalentWebpages().contains("http://en.wikipedia.org/wiki/Great_tit"));
        Assert.assertTrue(parus.getEquivalentWebpages().contains("http://eol.org/pages/32705/overview"));
        Assert.assertTrue(parus.getEquivalentWebpages().contains("http://bbc.co.uk/nature/life/Great_tit"));
    }
}
