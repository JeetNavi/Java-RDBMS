package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

        // Variable to keep track of change in the procedure.
        boolean changesMade = true;

        // General minimization procedure.
        while (changesMade) {
            changesMade = false;
            for (RelationalAtom atom : queryBody) {
                if (isQueryHomo(queryHead, queryBody, new RelationalAtom(atom.getName(), atom.getTerms()))) {
                    changesMade = true;
                    queryBody.remove(atom);
                    query.removeFromBody(atom);
                    break;
                }
            }
        }

        return query;
    }

    /**
     *
     * Method that checks whether there exists a query homomorphism from a given CQ to
     * the CQ that results from removing a given atom from it.
     *
     * @param queryHead The head of the CQ, a Head object.
     * @param queryBody The body of the CQ, a list of relational atoms.
     * @param atomToRemove The atom to remove from the body.
     * @return true if there is a query homomorphism else false.
     */
    private static boolean isQueryHomo(Head queryHead, List<RelationalAtom> queryBody, RelationalAtom atomToRemove) {

        HashMap<Term, Term> mapping = new HashMap<>();

        // Map all the head variables to itself; to preserve the output variables.
        for (Variable headVariable : queryHead.getVariables()) {
            mapping.put(headVariable, headVariable);
        }

        // The following block of code (from the for loop) follows an algorithm to check whether there exists a query homomorphism.
        // We iterate through the atoms of the body.
        // If the atom has the same relationName of the atomToRemove, use this atom to create a potential mapping.
        // For instance, if the atomToRemove is R(x, y) and the iterative atom is R(a, b), we will map x and y to a and b respectively.
        // However, if the head contains x for instance, x would get mapped to x rather than a.
        // We would then check if this mapping is a valid query homomorphism. If so, return true, else continue iteration (check other atoms).

        // Positional counter for terms in atoms; in the atom R(x, y), x has position 0 and y has position 1.
        int termCounter;

        for (RelationalAtom atom : queryBody) {
            // We will refer to the current atom of this for loop as "the atom".
            termCounter = -1;
            // If the relationName is the same as the relationName of the atomToRemove, there is a potential mapping here.
            if (atom.getName().equals(atomToRemove.getName())) {
                for (Term term : atom.getTerms()){
                    termCounter += 1;
                    // Get term of atomToRemove in the same position.
                    Term atomToRemoveTerm = atomToRemove.getTerms().get(termCounter);
                    // If this term is a constant, map to itself.
                    if (!(atomToRemoveTerm instanceof Variable)){
                        mapping.put(atomToRemoveTerm, atomToRemoveTerm);
                    }
                    // Else if it is a variable, check that it's not a head variable and map to the term in the correct position in the atom.
                    else {
                        if (!(queryHead.containsVariable((Variable) atomToRemoveTerm))) {
                            mapping.put(atomToRemoveTerm, term);
                        }
                    }
                }
                // Once we have mapped every term according to this atom, check if the mapping is a valid homomorphism.
                if (checkMapping(queryBody, atomToRemove, mapping)){
                    return true;
                }
                // If it is not a homomorphism, we jump back to the top for loop where we look at other atoms for a potential mapping.
                // We first need to remove all the non-head mappings made.
                List<Term> termsToRemoveFromMapping = new ArrayList<>();
                for (Term term : mapping.keySet()){
                    if (term instanceof Variable && (!(queryHead.containsVariable((Variable) term)))){
                        termsToRemoveFromMapping.add(term);
                    }
                }
                for (Term term : termsToRemoveFromMapping) {
                    mapping.remove(term);
                }
                termsToRemoveFromMapping.clear();
            }
        }

        return false;
    }

    /**
     *
     * Method that applies a given mapping to a given body and checks if all the resulting atoms are present
     * in the body with the atomToRemove removed. This is the general idea of a query homomorphism.
     *
     * @param queryBody The body of the CQ, a list of relational atoms. These are the atoms whose terms will be mapped.
     * @param atomToRemove The atom to remove from the body.
     * @param mapping A hashmap which maps terms to terms. Mappings are created in the isQueryHomo method.
     * @return true if the mapping is a query homomorphism, else false.
     */
    private static boolean checkMapping(List<RelationalAtom> queryBody, RelationalAtom atomToRemove, HashMap<Term, Term> mapping) {

        // Creating the smaller body (by removing the atom from the bigger body).
        // Our goal in this method is to check if the mapping given is a query homomorphism from the smaller body to the bigger body.
        List<RelationalAtom> queryBodyRemoved = new ArrayList<>(queryBody);
        queryBodyRemoved.remove(atomToRemove);

        // Creates the resulting body from mapping all the atoms in the bigger body according to the given mapping.
        List<RelationalAtom> mappedQueryBody = new ArrayList<>();
        for (RelationalAtom atom : queryBody) {
            mappedQueryBody.add(atom.mapTerms(mapping));
        }

        // Variable to keep track of whether the atom from the bigger body is present in the smaller body.
        boolean atomFound = false;

        // This block of code checks if all the atoms in the mapped body is present in the smaller body.
        // If so, this for loop will complete till the end and the mapping is a query homomorphism.
        // If not, this for loop will cause the method to return false when the atom is not found.
        for (RelationalAtom mappedAtom : mappedQueryBody) {
            for (RelationalAtom atom : queryBodyRemoved) {
                if (atom.equals(mappedAtom)){
                    atomFound = true;
                    break;
                }
            }
            if (!atomFound){
                return false;
            }
            else {
                atomFound = false;
            }
        }

        return true;
    }

    /**
     * Example method for getting started with the parser.
     * Reads CQ from a file and prints it to screen, then extracts Head and Body
     * from the query and prints them to screen.
     */

    public static Query parseQuery(String filename) {

        try {
            return QueryParser.parse(Paths.get(filename));
        }
        catch (Exception e)
        {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
        return null; // This should never be returned unless the query is invalid.
    }
}