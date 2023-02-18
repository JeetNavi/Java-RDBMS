package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.nio.file.Paths;
import java.util.*;

/**
 *
 * Minimization of conjunctive queries
 *
 */
public class CQMinimizer {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("Usage: CQMinimizer input_file output_file");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        minimizeCQ(inputFile, outputFile);
    }

    /**
     * CQ minimization procedure
     *
     * Assume the body of the query from inputFile has no comparison atoms
     * but could potentially have constants in its relational atoms.
     *
     */
    public static void minimizeCQ(String inputFile, String outputFile) {

        Query query = parseQuery(inputFile);
        Query tempQuery = parseQuery(inputFile);
        assert query != null;
        assert tempQuery != null;
        List<Atom> body = query.getBody();
        List<Atom> tempBody = tempQuery.getBody();

        boolean changesMade = true;

        while (changesMade){

            for (Atom atom : body) {
                if (isContained(query, tempQuery, atom)) {
                    body.remove(atom);
                    changesMade = true;
                    break;
                } else {
                    changesMade = false;
                }
            }
        }
    }

    private static boolean isContained(Query query, Query tempQuery, Atom removeAtom) {
        List<Atom> body = query.getBody();
        List<Atom> tempBody = tempQuery.getBody();
        Head head = query.getHead();
        Head tempHead = tempQuery.getHead();

        tempBody.remove(removeAtom);
        List<Term> removedAtomTerms = removeAtom.getAtomTerms();

        List<Variable> headVariables = head.getVariables();

        // Check body is not empty.
        // Check if the output variables still exist in body.
        // Check if output variable is preserved. (This is always the case, no need to check).
        // Check subset -> True
        // Else, if there exists a mapping -> True else False.
        // MAPPING CHECK: For every term in removedAtom, if term in head, return False.
        //                                               Else, if Const, check Const appears in same position in diff atom with same relation name -> If not then return False.
        //                                               Else if Variable, map to other positionally equivalent variables. Check if new Atom exists in toBody. -> True.
        //                                                  If false for all positionally equivalent variables, return False. (use hashmap from relationname to position)

        // Check body is not empty.
        if (tempBody.size() == 0){
            return false;
        }

        // Check if the output variables still exist in body.
        for (Variable headVariable : headVariables){
            boolean varExistsInBody = false;
            CheckAllAtoms:
            for (Atom atom : tempBody){
                for (Term term : atom.getAtomTerms()){
                    if (term.equals(headVariable)){
                        varExistsInBody = true;
                        break CheckAllAtoms;
                    }
                }
            }
            if (!varExistsInBody){
                return false;
            }
        }

        // Check subset -> True
        // Check if all atoms in longer body appear in shorter body.
        boolean allAtomsFound = true;
        for (Atom atom : body){
            if (!tempBody.contains(atom)){
                allAtomsFound = false;
                break;
            }
        }
        if (allAtomsFound){
            return true;
        }

        // Check for possible mapping.
        int position = 0;
        HashMap<Term, Term> mapping = new HashMap<>();
        String removedAtomRelationName = removeAtom.getRelationName();
        for (Atom atom : tempBody){
            if (atom.getRelationName().equals(removedAtomRelationName)){
                List<Term> atomTerms = atom.getAtomTerms();
                for (Term term : removedAtomTerms){
                    if (term instanceof Variable){
                        mapping.put(term, atomTerms.get(position));
                    }
                }
                for (Atom a : body){

                }
                Query potentialQuery = new Query(head, );
            }
        }
    }

    /**
     * Example method for getting started with the parser.
     * Reads CQ from a file and prints it to screen, then extracts Head and Body
     * from the query and prints them to screen.
     */

    public static Query parseQuery(String filename) {

        try {
            return QueryParser.parse(Paths.get(filename));
            // Query query = QueryParser.parse("Q(x, y) :- R(x, z), S(y, z, w)");
            // Query query = QueryParser.parse("Q(x) :- R(x, 'z'), S(4, z, w)");
        }
        catch (Exception e)
        {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
        return null; // This should never be returned unless the query is invalid.
    }
}
