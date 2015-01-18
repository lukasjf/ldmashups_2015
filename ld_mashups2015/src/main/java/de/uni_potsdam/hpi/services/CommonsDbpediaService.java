package de.uni_potsdam.hpi.services;

import java.util.LinkedList;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import de.uni_potsdam.hpi.data.SpeciesData;

public class CommonsDbpediaService {

    private String sparqlEndpoint = "http://commons.dbpedia.org/sparql";
    private String queryTemplate = "SELECT DISTINCT * "
            + "WHERE {"
            + "?animal <http://dbpedia.org/ontology/wikiPageInterLanguageLink> <%s> ."
            + "?animal <http://dbpedia.org/ontology/galleryItem> ?image ."
            + "?image <http://dbpedia.org/ontology/fileURL> ?url ."
            + "} ";

    public void includeDataFromCommonsDBpedia(SpeciesData species){
        List<String> imageURLs = new LinkedList<String>();
        String sparqlQueryString = String.format(queryTemplate,species.getdBpediaURI());
        Query query = QueryFactory.create(sparqlQueryString);
        QueryExecution qexec = QueryExecutionFactory
          .sparqlService(sparqlEndpoint, query);
        ResultSet results = qexec.execSelect();
        while (results.hasNext()){
            QuerySolution s = results.next();
            imageURLs.add(s.get("url").toString());
        }
        species.setImageUrls(imageURLs);
     }
}
