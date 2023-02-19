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

    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("Usage: CQMinimizer input_file output_file");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        Query CQ = parseQuery(inputFile);
        assert CQ != null;

        Query minimizedCQ = minimizeCQ(CQ);

        //System.out.println(minimizedCQ.toString());

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
     */
    public static Query minimizeCQ(Query query) {

        boolean changesMade = true;
        Head queryHead = query.getHead();
        List<RelationalAtom> queryBody = new ArrayList<>();

        for (Atom a : query.getBody()) {
            queryBody.add((RelationalAtom) a);
        }

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

    private static boolean isQueryHomo(Head queryHead, List<RelationalAtom> queryBody, RelationalAtom atomToRemove) {

        HashMap<Term, Term> mapping = new HashMap<>();
        int termCounter;

        for (Variable headVariable : queryHead.getVariables()) {
            mapping.put(headVariable, headVariable);
        }

        // For every atom in the big body
        for (RelationalAtom atom : queryBody) {
            termCounter = -1;
            // If the relationName is the same as the relationName of the atom to remove, there is a potential mapping here.
            if (atom.getName().equals(atomToRemove.getName())){
                // For every term of this atom.
                for (Term term : atom.getTerms()){
                    // Keep positional counter.
                    termCounter += 1;
                    // Get term of atomToRemove in the same position.
                    Term atomToRemoveTerm = atomToRemove.getTerms().get(termCounter);
                    // If this term in the atomToRemove is a constant, map to itself.
                    if (!(atomToRemoveTerm instanceof Variable)){
                        mapping.put(atomToRemoveTerm, atomToRemoveTerm);
                    }
                    // If it is a variable, check that it's not a head variable and map.
                    else {
                        if (!(queryHead.containsVariable((Variable) atomToRemoveTerm))) {
                            mapping.put(atomToRemoveTerm, term);
                        }
                    }
                }
                // Once we have mapped every term according to this atom, Check for containment.
                if (isContained(queryBody, atomToRemove, mapping)){
                    return true;
                }
                // If it is not contained, this jumps to top for loop where we look at other atoms for a potential mapping.
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

    private static boolean isContained(List<RelationalAtom> queryBody, RelationalAtom atomToRemove, HashMap<Term, Term> mapping) {

        List<RelationalAtom> queryBodyRemoved = new ArrayList<>(queryBody);
        for (RelationalAtom atom : queryBodyRemoved) {
            if (atom.equals(atomToRemove)){
                atomToRemove = atom;
                break;
            }
        }
        queryBodyRemoved.remove(atomToRemove);
        List<RelationalAtom> mappedQueryBody = new ArrayList<>();

        for (RelationalAtom atom : queryBody) {
            mappedQueryBody.add(atom.mapTerms(mapping));
        }

        boolean atomFound = false;

        for (RelationalAtom q1atom : mappedQueryBody) {
            for (RelationalAtom q2atom : queryBodyRemoved) {
                if (q2atom.equals(q1atom)){
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
            //return QueryParser.parse("Q(x, y) :- R(x, z), S(y, z, w)");
            //return QueryParser.parse("Q(x) :- R(x, 'z'), S(4, z, w)");
            //return QueryParser.parse("Q(x) :- P(x,a), P(x,a), T(b,b,x), T(z,z,a)");
        }
        catch (Exception e)
        {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
        return null; // This should never be returned unless the query is invalid.
    }
}