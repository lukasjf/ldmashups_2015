package de.uni_potsdam.hpi.services;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import de.uni_potsdam.hpi.data.Species;

public class DBpediaService {

    private String sparqlEndpoint = "http://dbpedia.org/sparql";
    private String queryTemplate = "SELECT ?image ?abstract ?kingdom ?phylum ?family ?animal ?class " +
            "FROM <http://dbpedia.org> " +
            "WHERE { " +
            "?animal <http://dbpedia.org/property/binomial> \"%s\"@en . " +
            "?animal <http://dbpedia.org/ontology/thumbnail> ?image . " +
            "?animal <http://dbpedia.org/ontology/abstract> ?abstract . " +
            "?animal <http://dbpedia.org/ontology/kingdom> ?kingdom ." +
            "?animal <http://dbpedia.org/ontology/phylum> ?phylum ." +
            "?animal <http://dbpedia.org/ontology/family> ?family ." +
            "?animal <http://dbpedia.org/ontology/class> ?class ." +
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
            species.setDescription(answer.getLiteral("abstract").toString());
            species.setThumbnailURL(answer.getResource("image").getURI());
            species.setEntityURI(answer.getResource("animal").getURI());
            species.setPhylum(answer.getResource("phylum").getURI());
            species.setKingdom(answer.getResource("kingdom").getURI());
            species.setFamily(answer.getResource("family").getURI());
            species.setTaxonClass(answer.getResource("class").getURI());
        }
     }
}
