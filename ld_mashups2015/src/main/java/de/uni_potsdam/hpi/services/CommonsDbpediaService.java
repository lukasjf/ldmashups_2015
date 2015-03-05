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

/**
 * This class accesses the Dbpedia commons via the SPARQL-Endpoint.
 * Create a instance of this class and execute the includeDataFromCommonsDBpedia on an existing SpeciesData-Object.
 * The species will be updated with the information retrieved from the DBpedia-Commons.
 */
public class CommonsDbpediaService {
    private String sparqlEndpoint = "http://commons.dbpedia.org/sparql";
    private String queryTemplate = "SELECT DISTINCT ?image ?url "
            + "WHERE {"
            + "{{{?animal <http://dbpedia.org/ontology/wikiPageInterLanguageLink> <%s> .} UNION"
            + "{?animal <http://commons.dbpedia.org/property/en> \"%s\"@en .}} UNION "
            + "{?animal <http://www.w3.org/2000/01/rdf-schema#label> \"%s\"@en .}}"
            + "?image <http://dbpedia.org/ontology/wikiPageWikiLink> ?animal ."
            + "?image <http://dbpedia.org/ontology/fileURL> ?url ."
            + "} ";

    public void includeDataFromCommonsDBpedia(SpeciesData species){
        List<String> imageURLs = new LinkedList<String>();
        String sparqlQueryString = String.format(queryTemplate, species.getdBpediaURI(), species.getName(), species.getShortSynonyme());
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
