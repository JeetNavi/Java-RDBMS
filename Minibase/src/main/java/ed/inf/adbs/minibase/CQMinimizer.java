package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.parser.QueryParser;
import ed.inf.adbs.minibase.parser.generated.MinibaseParser;
//import jdk.javadoc.internal.doclets.formats.html.markup.BodyContents;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;

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

        //parsingExample(inputFile);
    }

    /**
     * CQ minimization procedure
     *
     * Assume the body of the query from inputFile has no comparison atoms
     * but could potentially have constants in its relational atoms.
     *
     */
    public static void minimizeCQ(String inputFile, String outputFile) throws IOException {

        Query CQ = QueryParser.parse(Paths.get(inputFile));
        List<Atom> CQBody = CQ.getBody();

        Query tempCQ = QueryParser.parse(Paths.get(inputFile));
        List<Atom> tempCQBody = tempCQ.getBody();

        assert CQBody != null;
        boolean changesMade = true;


        while (changesMade){

            for (Atom atom : CQBody) {
                tempCQBody.remove(atom);
                if (isQueryHomomorphism(CQ, tempCQ, atom)) {
                    CQBody.remove(atom);
                    changesMade = true;
                    break;
                } else {
                    tempCQBody.add(atom);
                    changesMade = false;
                }
            }
        }

    }

    public static boolean isQueryHomomorphism(Query from, Query to, Atom removedAtom){

        List<Term> headVars = new ArrayList<>(from.getHead().getVariables());
        String relationName = removedAtom.getRelationName();

        HashMap<Term, Term> mapping = new HashMap<>();

        int termCounter = 0;

        for (Term term : removedAtom.getAtomTerms()) {
            if (!(headVars.contains(term)) && (term instanceof Variable)) {
                for (Atom atom : to.getBody()) {
                    if (atom.getRelationName().equals(relationName)) {
                        mapping.put(term, atom.getAtomTerms().get(termCounter));
                        if (isContained(from.getBody(), to.getBody(), mapping)) {
                            return true;
                        } else {
                            mapping.remove(term);
                        }
                    }
                }
            }
            termCounter += 1;
        }

        return false;
    }

    private static boolean isContained(List<Atom> fromBody, List<Atom> toBody, HashMap<Term, Term> mapping) {

        List<Atom> mappedBody = new ArrayList<>();

        for (Atom atom : fromBody){
            for (Term term : atom.getAtomTerms()){
                List<Term> mappedTerms = new ArrayList<>();
                mappedTerms.add(mapping.getOrDefault(term, term));
            }


            for (Atom toAtom : toBody){

            }
        }
    }


    /**
     * Example method for getting started with the parser.
     * Reads CQ from a file and prints it to screen, then extracts Head and Body
     * from the query and prints them to screen.
     */

    public static void parsingExample(String filename) {

        try {
            //Query query = QueryParser.parse(Paths.get(filename));
             Query query = QueryParser.parse("Q(x, y) :- R(x, z), S(y, z, w)");
            // Query query = QueryParser.parse("Q(x) :- R(x, 'z'), S(4, z, w)");

            System.out.println("Entire query: " + query);
            Head head = query.getHead();
            System.out.println("Head: " + head);
            List<Atom> body = query.getBody();
            System.out.println("Body: " + body);
        }
        catch (Exception e)
        {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
    }
}
