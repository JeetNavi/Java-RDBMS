package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/**
 * In-memory database system
 *
 */
public class Minibase {

    public static void main(String[] args) {

        if (args.length != 3) {
            System.err.println("Usage: Minibase database_dir input_file output_file");
            return;
        }

        String databaseDir = args[0];
        String inputFile = args[1];
        String outputFile = args[2];

        evaluateCQ(databaseDir, inputFile, outputFile);
    }

    /**
     * Evaluates the CQ.
     * Initializes the DatabaseCatalog instance with the given databaseDir.
     * Parses they query from the input file into a Query object.
     * Then we rewrite the query such that there is no duplicate variables in the relational atoms of the body
     * and that there are no constants in the relation atoms of the body.
     * Then we build the query plan based off of this rewritten query.
     * We call dump on the root operator from the query plan to get a String, which is the output tuples.
     * Then we write the output tuples to the given output file.
     * @param databaseDir The directory of the database as a string file path.
     * @param inputFile Name of the input file.
     * @param outputFile Name of the output file, to write output tuples to.
     */
    public static void evaluateCQ(String databaseDir, String inputFile, String outputFile) {
        DatabaseCatalog.init(databaseDir);
        Query query = parseQuery(inputFile);
        assert query != null;

        Rewriter rewriter = new Rewriter(query);
        query = rewriter.rewriteQuery();

        QueryPlan queryPlan = new QueryPlan(query);
        Operator rootOperator = queryPlan.getRootOperator();

        String outputTuples = "";
        // Plan could be null if there is a comparison atom that will never hold, i.e. 1=2.
        if (rootOperator != null) {
            outputTuples = rootOperator.dump();
        }

        // Write the output tuples to the output file.
        try {
            FileWriter myWriter = new FileWriter(outputFile);
            myWriter.write(outputTuples);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to file.");
            e.printStackTrace();
        }

    }

    /**
     * Reads CQ from a file and prints it to screen, then extracts Head and Body
     * from the query and prints them to screen.
     */

    public static Query parseQuery(String filename) {
        try {
            Query query = QueryParser.parse(Paths.get(filename));

            System.out.println("Entire query: " + query);
            Head head = query.getHead();
            System.out.println("Head: " + head);
            List<Atom> body = query.getBody();
            System.out.println("Body: " + body);
            return query;
        }
        catch (Exception e)
        {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
            return null;
        }
    }

}
