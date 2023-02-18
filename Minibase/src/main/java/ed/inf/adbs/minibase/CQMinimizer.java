package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.FileWriter;
import java.io.IOException;
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

        System.out.println(query.toString());

        try {
            FileWriter myWriter = new FileWriter(outputFile);
            myWriter.write(query.toString());
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static boolean isContained(Query query, Query tempQuery, Atom removeAtom) {

        Head head = query.getHead();
        Head tempHead = tempQuery.getHead();
        List<RelationalAtom> body = new ArrayList<>();
        List<RelationalAtom> tempBody = new ArrayList<>();

        for (Atom atom : query.getBody()){
            body.add((RelationalAtom) atom);
            tempBody.add((RelationalAtom) atom);
        }

        tempBody.remove((RelationalAtom) removeAtom);
        RelationalAtom removedAtom = (RelationalAtom) removeAtom;

        List<Term> removedAtomTerms = removedAtom.getTerms();
        List<Variable> headVariables = head.getVariables();

        // Check body is not empty.
        if (tempBody.size() == 0){
            return false;
        }

        // Check if the output variables still exist in body.
        for (Variable headVariable : headVariables){
            boolean varExistsInBody = false;
            CheckAllAtoms:
            for (RelationalAtom atom : tempBody){
                for (Term term : atom.getTerms()){
                    if (term instanceof Variable && ((Variable) term).equals(headVariable)){
                        varExistsInBody = true;
                        break CheckAllAtoms;
                    }
                }
            }
            if (!varExistsInBody){
                return false;
            }
        }

        // Check if all atoms in longer body appear in shorter body.
        boolean allAtomsFound = true;
        for (RelationalAtom atom : body){
            if (!tempBody.contains(atom)){
                allAtomsFound = false;
                break;
            }
        }
        if (allAtomsFound){
            return true;
        }

        // Check if constants in removed atom gives problems.
        int position = 0;
        boolean constantRemoved = true;
        for (Term term : removedAtomTerms){
            if (!(term instanceof Variable)){
                for (RelationalAtom rAtom : tempBody){
                    if (rAtom.getName().equals(removedAtom.getName()) && rAtom.getTerms().get(position).equals(term)) {
                        constantRemoved = false;
                        break;
                    }
                }
                if (constantRemoved) {
                    return false;
                } else {
                    constantRemoved = true;
                }
            }
            else {
                position += 1;
            }
        }

        // Find all possible mappings.
        HashMap<Term, List<Term>> possibleMapping = new HashMap<>();
        List<Term> possibleTerms = new ArrayList<>();
        position = 0;
        boolean isHeadVariable = false;
        for (Term term : removedAtomTerms){
            possibleTerms.clear();
            if (term instanceof Variable){
                for (Variable headVariable : headVariables){
                    if (headVariable.equals((Variable) term)){
                        isHeadVariable = true;
                        possibleMapping.put(term, Collections.singletonList(term));
                        break;
                    }
                }

                if (!isHeadVariable) { // Not an output variable, but it is a term, so we need to map.
                    for (RelationalAtom atom : tempBody){
                        if (atom.getName().equals(removedAtom.getName())){
                            possibleTerms.add(atom.getTerms().get(position));
                        }
                    }
                    possibleMapping.put(term, possibleTerms);
                }
            }
            else {
                possibleTerms.add(term);
                possibleMapping.put(term, possibleTerms);
            }
            position += 1;
            isHeadVariable = false;
        }

        // Find a query homomorphism.
        HashMap<Term, Term> mapping = new HashMap<>();
        int numOfTermsInRemovedAtom = removedAtomTerms.size();
        List<Term> potentialMap = new ArrayList<>();

        for (int i = 0; i < possibleMapping.get(removedAtomTerms.get(0)).size(); i++){
            for (Term removedAtomTerm : removedAtomTerms) {
                mapping.put(removedAtomTerm, possibleMapping.get(removedAtomTerm).get(i));
            }
            // Apply mapping to big body and check if all of big body is in small body
            for (RelationalAtom atom : body){
                atom.mapVariables(mapping);
            }

            allAtomsFound = true;
            for (RelationalAtom atom : body){
                for (RelationalAtom atom2 : tempBody){
                    if (atom2.equals(atom)){
                        break;
                    }
                    allAtomsFound = false;
                }
            }
            if (allAtomsFound){
                return true;
            }
            else {
                mapping.clear();
            }

        }

        return false;
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
