import java.io.InputStream;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;

public class RDFProcessing {
    public static void main (String args[]) {
        // Make a TDB-backed dataset
        String directory = "NobelDB";
        Dataset dataset = TDBFactory.createDataset(directory);
        dataset.begin(ReadWrite.WRITE);
        
        try {
            // Create an empty model
            Model model = dataset.getDefaultModel();
            
            // Use the FileManager to find the input file
            String inputFileName = "Nobeldump.nt";
            InputStream inputStream = FileManager.get().open(inputFileName);
            if (inputStream == null) throw new IllegalArgumentException("File: " + inputFileName + " not found");
            
            // Load the data from the Nobel prize data dump file
            model.read(inputStream, null, "N-TRIPLES");
            
            // Print the first 20 elements
            try (QueryExecution query = QueryExecutionFactory.create("SELECT * WHERE { ?subject ?predicate ?object } LIMIT 20", dataset)) {
                ResultSet results = query.execSelect();
                ResultSetFormatter.out(results);
            }
            
            // Find the name of one of the most recent Nobel Chemistry award winners
            try (QueryExecution query = QueryExecutionFactory.create(
                    "PREFIX category: <http://data.nobelprize.org/resource/category/> "
                    + "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                    + "PREFIX nobel: <http://data.nobelprize.org/terms/> "
                    + "SELECT ?name "
                    + "WHERE { "
                        + "?nobel nobel:category category:Chemistry ; "
                        + "nobel:year ?year ; "
                        + "nobel:laureate ?laureate . "
                        + "?laureate foaf:name ?name . "
                    + "} "
                    + "ORDER BY DESC(?year) "
                    + "LIMIT 1"
                    , dataset)) {
                ResultSet results = query.execSelect();
                ResultSetFormatter.out(results);
            }
            
            // Find the categories awarded during the first edition of the Nobel prize
            try (QueryExecution query = QueryExecutionFactory.create(
                    "PREFIX nobel: <http://data.nobelprize.org/terms/> "
                    + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                    + "SELECT ?category "
                    + "WHERE { "
                        + "?nobel rdf:type nobel:NobelPrize ; "
                        + "nobel:category ?category ; "
                        + "nobel:year 1901 . "
                    + "}"
                    , dataset)) {
                ResultSet results = query.execSelect();
                ResultSetFormatter.out(results);
            }
            
            /* List, in ascending order of number of awards, the countries who have Physiology or Medicine prize winners
            affiliated to one of their universities together with the number of awards received by that country */
            try (QueryExecution query = QueryExecutionFactory.create(
                    "PREFIX category: <http://data.nobelprize.org/resource/category/> "
                    + "PREFIX dbo: <http://dbpedia.org/ontology/> "
                    + "PREFIX nobel: <http://data.nobelprize.org/terms/> "
                    + "SELECT ?country (count(?country) as ?count) "
                    + "WHERE { "
                        + "?nobel nobel:category category:Physiology_or_Medicine . "
                        + "OPTIONAL { "
                            + "?nobel nobel:university ?university . "
                            + "?university dbo:country ?country . "
                        + "} "
                        + "FILTER (BOUND(?university)) "
                    + "} "
                    + "GROUP BY ?country "
                    + "ORDER BY ?count"
                    , dataset)) {
                ResultSet results = query.execSelect();
                ResultSetFormatter.out(results);
            }
            
            /* Find all the Nobel Laureates, with the year of the award, who were born either in Germany 
            or in a country that is now known as Germany */
            try (QueryExecution query = QueryExecutionFactory.create(
                    "PREFIX country: <http://data.nobelprize.org/resource/country/> "
                    + "PREFIX dbo: <http://dbpedia.org/ontology/> "
                    + "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                    + "PREFIX nobel: <http://data.nobelprize.org/terms/> "
                    + "SELECT ?name ?year "
                    + "WHERE { "
                        + "?laureate dbo:birthPlace ?country ; "
                        + "foaf:name ?name ; "
                        + "nobel:nobelPrize ?nobel . "
                        + "?nobel nobel:year ?year . "
                        + "FILTER (?country = country:Germany || regex(str(?country), \"now_Germany\")) "
                    + "}"
                    , dataset)) {
                ResultSet results = query.execSelect();
                ResultSetFormatter.out(results);
            }
            
            dataset.commit();
        } finally {
            dataset.end();
        }
    }
}
