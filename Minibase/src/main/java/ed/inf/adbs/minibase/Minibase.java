package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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

        List<Atom> body = query.getBody();
        Operator operation = identifyOperation(body);
        operation.dump();

    }

    public static Operator identifyOperation(List<Atom> body) {
        
        Operator operation;
        
        ScanOperator scanOperator = null;
        
        List<ComparisonAtom> conditions = new ArrayList<>();

        List<Atom> rewrittenBody = new ArrayList<>();

        DatabaseCatalog catalog = DatabaseCatalog.getCatalogInstance();

        // First we add all variable positions to the varPositions mapping.
        for (Atom atom : body) {
            if (atom instanceof RelationalAtom) {
                for (int i = 0; i < ((RelationalAtom) atom).getTerms().size(); i++) {
                    Term term = ((RelationalAtom) atom).getTerms().get(i);
                    if (term instanceof Variable) {
                        catalog.setVarPos((Variable) term, i);
                    }
                }
            }
        }

        // Then we rewrite any relational atoms of the form R(x, 2) to R(x, z), z = 2 (remembering to add any new variable to pos mapping).
        for (Atom atom : body) {
            if (atom instanceof RelationalAtom) {
                if ((((RelationalAtom) atom).getTerms()).stream().anyMatch(Constant.class::isInstance)) {
                    List<Atom> extendedAtom = rewriteAtom((RelationalAtom) atom);
                    rewrittenBody.addAll(extendedAtom);
                }
                else {
                    rewrittenBody.add(atom);
                }
            }
            else {
                rewrittenBody.add(atom);
            }
        }

        for (Atom atom : rewrittenBody) {
            if (atom instanceof RelationalAtom){
                // Scan
                RelationalAtom relationalAtom = (RelationalAtom) atom;
                scanOperator = new ScanOperator(relationalAtom);
            }
            else {
                // Selection
                ComparisonAtom comparisonAtom = (ComparisonAtom) atom;
                conditions.add(comparisonAtom);
            }
        }
        
        if (conditions.size() > 0) {
            operation = new SelectOperator(conditions, scanOperator);
        }
        else {
            operation = scanOperator;
        }

        return operation;
    }

    private static List<Atom> rewriteAtom(RelationalAtom atom) {

        HashMap<Integer, Constant> posOfConstants = new HashMap<>();

        int posCounter = 0;

        for (Term term : atom.getTerms()) {
            if (term instanceof Constant){
                posOfConstants.put(posCounter, (Constant) term);
            }
            posCounter += 1;
        }


        Random rnd = new Random();

        HashMap<Integer, Variable> posOfNewVars = new HashMap<>();

        for (Integer pos : posOfConstants.keySet()) {
            Variable rndVar = new Variable(String.valueOf((char) ('a' + rnd.nextInt(26))));

            while (DatabaseCatalog.getCatalogInstance().getVarPositions().containsKey((rndVar))){
                rndVar.changeName(rndVar.getName() + (String.valueOf((char) ('a' + rnd.nextInt(26)))));
            }
            // We have generated a new variable in rndVar
            DatabaseCatalog.getCatalogInstance().setVarPos(rndVar, pos);
            posOfNewVars.put(pos, rndVar);
        }

        // Construct the new atoms.
        posCounter = 0;

        List<ComparisonAtom> conditions = new ArrayList<>();

        List<Term> newVars = new ArrayList<>();

        for (Term term : atom.getTerms()) {
            if (term instanceof Constant) {
                newVars.add(posOfNewVars.get(posCounter));
                conditions.add(new ComparisonAtom(posOfNewVars.get(posCounter), posOfConstants.get(posCounter), ComparisonOperator.EQ));
            }
            else {
                newVars.add(term);
            }
            posCounter += 1;
        }

        Atom scanAtom = new RelationalAtom(atom.getName(), newVars);

        List<Atom> result = new ArrayList<>();
        result.add(scanAtom);
        result.addAll(conditions);

        return result;
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
