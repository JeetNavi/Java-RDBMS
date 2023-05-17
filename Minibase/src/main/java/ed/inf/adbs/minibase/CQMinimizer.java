package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * Minimization of conjunctive queries
 *
 */
public class CQMinimizer {

    /**
     *
     * Main method.
     * Parses a CQ from an input file and minimizes it.
     * The minimized CQ will be written to an output file.
     *
     * @param args Input file and Output file.
     */
    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("Usage: CQMinimizer input_file output_file");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        // Parse the CQ from the input file to a Query object.
        Query CQ = parseQuery(inputFile);
        assert CQ != null;

        // Minimize the CQ.
        Query minimizedCQ = minimizeCQ(CQ);



        // Write the minimized CQ to the output file.
        try {
            FileWriter myWriter = new FileWriter(outputFile);
            myWriter.write(minimizedCQ.toString());
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to file.");
            e.printStackTrace();
        }
    }

    /**
     * CQ minimization procedure
     *
     * Assume the body of the query from inputFile has no comparison atoms
     * but could potentially have constants in its relational atoms.
     *
     * @param query The parsed CQ as a Query object from the input file.
     */
    public static Query minimizeCQ(Query query) {

        Head queryHead = query.getHead();
        List<RelationalAtom> queryBody = new ArrayList<>();

        // Cast all the Atom objects in the body to RelationalAtom and store in a list.
        for (Atom a : query.getBody()) {
            queryBody.add((RelationalAtom) a);
        }

        HashMap<String, List<RelationalAtom>> nameToAtoms = new HashMap<>();
        for (RelationalAtom relationalAtom : queryBody) {
            String name = relationalAtom.getName();
            if (!nameToAtoms.containsKey(name)) {
                nameToAtoms.put(name, new ArrayList<>());
            }
            List<RelationalAtom> atoms = nameToAtoms.get(name);
            atoms.add(relationalAtom);
            nameToAtoms.put(name, atoms);
        }

        int numOfNames = nameToAtoms.size();

        HashMap<Term, Term> potentialMapping = new HashMap<>();

        // Variable to keep track of change in the procedure.
        boolean changed = true;

        while (changed){
            changed = false;
            int numOfAtomsToMapOn = 0;
            ArrayList<RelationalAtom> atomsUsed = new ArrayList<>();

            while (numOfAtomsToMapOn < numOfNames) {
                numOfAtomsToMapOn += 1;
                for (RelationalAtom atomToRemove : queryBody) {
                    List<RelationalAtom> pairs = createPairs(numOfAtomsToMapOn, queryHead, nameToAtoms, queryBody);
                }
            }
        }
        return null;
    }

    private static List<RelationalAtom> createPairs(int numOfAtomsToMapOn, Head queryHead, HashMap<String, List<RelationalAtom>> nameToAtoms, List<RelationalAtom> body) {

        List<String> names = new ArrayList<>(nameToAtoms.keySet());
        List<String> namesToMap = names.subList(0, numOfAtomsToMapOn);
        List<RelationalAtom[]> pairs = new ArrayList<>();

        for (int i = 0; i < numOfAtomsToMapOn; i++) {
            for (RelationalAtom relationalAtom : body) {
                String name = relationalAtom.getName();
                List<RelationalAtom> atomsWithSameName = nameToAtoms.get(name);
                for (RelationalAtom atomWithSameName : atomsWithSameName) {
                    if (!(atomWithSameName == relationalAtom)) {
                        RelationalAtom[] pair = {relationalAtom, atomWithSameName};
                        pairs.add(pair);
                    }
                }
            }
        }
    }

    /**
     * Method to parse a CQ from a provided file (provided as filename).
     * Reads CQ from a file and prints different parts of the query to the screen.
     * @param filename The name of the file of which the CQ is in.
     * @return Parsed CQ as a Query object.
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
        }
        return null; // This should never be returned unless the query is invalid.
    }
}