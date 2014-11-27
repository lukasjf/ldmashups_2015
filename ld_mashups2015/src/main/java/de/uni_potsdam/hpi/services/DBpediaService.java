package de.uni_potsdam.hpi.services;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import de.uni_potsdam.hpi.data.Species;

public class DBpediaService {

    private String sparqlEndpoint = "http://de.dbpedia.org/sparql";
    private String queryTemplate = "SELECT ?image ?abstract " +
            "FROM <http://de.dbpedia.org> " +
            "WHERE { " +
            "?animal <http://dbpedia.org/property/binomial> \"%s\"@en . " +
            "?animal <http://dbpedia.org/ontology/thumbnail> ?image . " +
            "?animal <http://dbpedia.org/ontology/abstract> ?abstract . " +
            "FILTER (LANG(?abstract) = \"en\")" +
            "} LIMIT 10";


    // searching for strings does not work yet, nor does it in the online endpoint
	public void includeDataFromDBpedia(Species species){
	    System.out.println(species.getScientificName());
		String sparqlQueryString = String.format(queryTemplate, species.getScientificName());
        Query query = QueryFactory.create(sparqlQueryString);
        QueryExecution qexec = QueryExecutionFactory
          .sparqlService(sparqlEndpoint, query);
        ResultSet results = qexec.execSelect();
        if (results.hasNext()){
            QuerySolution answer = results.next();
            System.out.println(answer.getLiteral("abstract")); // instead assigning it to the species
            System.out.println(answer.getResource("image").toString()); //assign to animal
            species.setDescription(answer.getLiteral("abstract").toString());
            species.setThumbnailURL(answer.getResource("image").toString());
        }
     }
}
