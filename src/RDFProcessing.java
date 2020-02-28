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
            if (inputStream == null) {
                throw new IllegalArgumentException("File: " + inputFileName + " not found");
            }
            
            // Load the data in the Nobel prize data dump file
            model.read(inputStream, null, "N-TRIPLES");
            
            // TODO Prefixes and formatting
            // Print the first 20 elements in the triple store
            try (QueryExecution query = QueryExecutionFactory.create("SELECT * WHERE {?subject ?predicate ?object} LIMIT 20", dataset)) {
                ResultSet results = query.execSelect();
                ResultSetFormatter.out(results);
            }
             
            dataset.commit();
        } finally {
            dataset.end();
        }
    }
}
