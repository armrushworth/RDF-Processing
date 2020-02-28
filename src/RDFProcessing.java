import java.io.InputStream;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;

public class RDFProcessing {
    
    static final String inputFileName = "Nobeldump.nt";
    
    public static void main (String args[]) {
        // Make a TDB-backed dataset
        String directory = "NobelDB";
        Dataset dataset = TDBFactory.createDataset(directory);
        dataset.begin(ReadWrite.WRITE);
        
        try {
            // Create an empty model
            Model model = dataset.getDefaultModel();
            
            // Use the FileManager to find the input file
            InputStream inputStream = FileManager.get().open(inputFileName);
            if (inputStream == null) throw new IllegalArgumentException("File: " + inputFileName + " not found");
            
            // Load the data from the Nobel prize data dump file
            model.read(inputStream, null, "N-TRIPLES");
            
            // Print the first 20 elements
            try (QueryExecution query = QueryExecutionFactory.create("SELECT * WHERE {?subject ?predicate ?object} LIMIT 20", dataset)) {
                ResultSet results = query.execSelect();
                ResultSetFormatter.out(results);
            }
            
            // Find the name(s) of the most recent Nobel Chemistry award winner(s)
            try (QueryExecution query = QueryExecutionFactory.create(
                    "PREFIX category: <http://data.nobelprize.org/resource/category/> "
                    + "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                    + "PREFIX nobel: <http://data.nobelprize.org/terms/> "
                    + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                    + "SELECT ?name "
                    + "WHERE {"
                        + "?subject rdf:type nobel:NobelPrize; "
                        + "nobel:category category:Chemistry; "
                        + "nobel:year ?year; "
                        + "nobel:laureate ?laureate. "
                        + "?laureate foaf:name ?name. "
                        + "FILTER (?year = 2016)"
                    + "}"
                    , dataset)) {
                ResultSet results = query.execSelect();
                ResultSetFormatter.out(results);
            }
            
            // Find the categories awarded during the first edition of the Nobel prize
            try (QueryExecution query = QueryExecutionFactory.create(
                    "PREFIX nobel: <http://data.nobelprize.org/terms/> "
                    + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                    + "SELECT ?category "
                    + "WHERE {"
                        + "?subject rdf:type nobel:NobelPrize; "
                        + "nobel:category ?category; "
                        + "nobel:year ?year; "
                        + "FILTER (?year = 1901)"
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
