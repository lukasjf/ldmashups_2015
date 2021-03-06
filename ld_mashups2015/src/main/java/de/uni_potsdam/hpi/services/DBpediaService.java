package de.uni_potsdam.hpi.services;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Resource;
import de.uni_potsdam.hpi.data.SpeciesData;

/**
 * The DBpediaService contains the functionality to retrieve Species information from the DBpedia.
 * The DBpedia will accessed via its SPARQL enpoint. The data will then be included in an existing species Object.
 * This species Object mus include the binomial to execute the sparql-Query.
 */
public class DBpediaService {
    private String sparqlEndpoint = "http://dbpedia.org/sparql";
    private String queryTemplate = "SELECT ?name ?image ?binomial ?abstract ?kingdom ?phylum ?family ?animal ?class ?order ?genus ?english_name " +
            "FROM <http://dbpedia.org> " +
            "WHERE { " +
            "{{ ?animal <http://dbpedia.org/property/binomial> \"%s\"@en} UNION " +
            "{ ?animal <http://dbpedia.org/property/synonyms> ?name FILTER <http://www.w3.org/2005/xpath-functions/#contains>(?name, \"%s\") }}" +
            "?animal <http://dbpedia.org/property/binomial> ?binomial ." +
            "?animal <http://dbpedia.org/ontology/thumbnail> ?image . " +
            "?animal <http://dbpedia.org/ontology/abstract> ?abstract . " +
            "?animal <http://dbpedia.org/ontology/kingdom> ?kingdom ." +
            "OPTIONAL { ?animal <http://dbpedia.org/ontology/phylum> ?phylum . }" +
            "?animal <http://dbpedia.org/ontology/family> ?family ." +
            "?animal <http://dbpedia.org/ontology/class> ?class ." +
            "?animal <http://dbpedia.org/ontology/order> ?order ." +
            "?animal <http://dbpedia.org/ontology/genus> ?genus ." +
            "?animal <http://dbpedia.org/property/name> ?english_name ." +
            "FILTER (LANG(?abstract) = \"en\")" +
            "} LIMIT 10";


    // searching for strings does not work yet, nor does it in the online endpoint
	public void includeDataFromDBpedia(SpeciesData species){
	    String sparqlQueryString = String.format(queryTemplate, species.getBinomial(), species.getBinomial());
        Query query = QueryFactory.create(sparqlQueryString);
        QueryExecution qexec = QueryExecutionFactory
          .sparqlService(sparqlEndpoint, query);
        ResultSet results = qexec.execSelect();
        if (results.hasNext()){
            QuerySolution answer = results.next();
            species.setdBpediaURI(answer.getResource("animal").getURI());
            species.setDescription(answer.getLiteral("abstract").getString());
            species.setThumbnailURL(answer.getResource("image").getURI());     
            Resource phylum;
            if (null != (phylum = answer.getResource("phylum"))) {
                species.setPhylum(phylum.getURI());
            }
            species.setKingdom(answer.getResource("kingdom").getURI());
            species.setFamily(answer.getResource("family").getURI());
            species.setTaxonClass(answer.getResource("class").getURI());
            species.setOrder(answer.getResource("order").getURI());
            species.setGenus(answer.getResource("genus").getURI());
            if (answer.getLiteral("name") == null || "".equals(answer.getLiteral("name").toString())) {
                species.setSynonyme(species.getBinomial());
            } else if (null != answer.getLiteral("binomial")) {
                species.setSynonyme(answer.getLiteral("binomial").getString());
            } else {
                species.setSynonyme(answer.getLiteral("name").getString());
            }
            species.setName(answer.get("english_name").toString());
        }
     }
}
