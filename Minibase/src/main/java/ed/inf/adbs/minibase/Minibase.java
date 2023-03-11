package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.parser.QueryParser;

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

    public static void evaluateCQ(String databaseDir, String inputFile, String outputFile) {
        DatabaseCatalog.init(databaseDir);
        Query query = parseQuery(inputFile);
        assert query != null;

        Rewriter rewriter = new Rewriter(query);
        query = rewriter.rewriteQuery();

        QueryPlan queryPlan = new QueryPlan(query);
        Operator rootOperator = queryPlan.getRootOperator();
        // Plan could be null if there is a comparison atom that will never hold, i.e. 1=2.
        if (rootOperator != null) {
            rootOperator.dump();
        }

    }

    /**
     * Example method for getting started with the parser.
     * Reads CQ from a file and prints it to screen, then extracts Head and Body
     * from the query and prints them to screen.
     */

    public static Query parseQuery(String filename) {
        try {
            Query query = QueryParser.parse(Paths.get(filename));
            // Query query = QueryParser.parse("Q(x, y) :- R(x, z), S(y, z, w), z < w");
            // Query query = QueryParser.parse("Q(SUM(x * 2 * x)) :- R(x, 'z'), S(4, z, w), 4 < 'test string' ");

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
