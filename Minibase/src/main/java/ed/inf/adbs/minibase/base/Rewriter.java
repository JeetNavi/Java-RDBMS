package ed.inf.adbs.minibase.base;

import java.util.*;

/**
 * Rewriter class used to simplify query bodies.
 * We replace duplicate variables with new variables, with an extra comparison atom added.
 * We also replace constants with new variables, with an extra comparison atom added.
 */
public class Rewriter {

    // The head of the CQ.
    private final Head head;
    // The relational atoms of the CQ body.
    private List<RelationalAtom> relationalAtoms;
    // The comparison atoms of the CQ body.
    private final List<ComparisonAtom> comparisonAtoms;
    // The variables in use, to avoid further duplication of variables when generating new variables.
    private final List<Variable> usedVariables;

    /**
     * Constructor for Rewriter.
     * Extract the head, relational atoms and comparison atoms from the query.
     * Also stores the variables already in use.
     * @param query The CQ to rewrite.
     */
    public Rewriter(Query query) {
        List<Atom> body = query.getBody();
        head = query.getHead();
        relationalAtoms = new ArrayList<>();
        comparisonAtoms = new ArrayList<>();
        usedVariables = new ArrayList<>();

        splitBody(body);
        storeVariables();
    }

    /**
     * The "main" method of the Rewriter class.
     * Used to rewrite the body of the query.
     * Rewriting is done in two steps:
     * 1. Replacing duplicate variables with new variables and comparison atoms. (R(x,x) -> R(x,y), x=y).
     * 2. Replacing constants with new variables and comparison atoms. (R(x,1) -> R(x,y), y=1).
     * @return A new Query object, with the same head and the rewritten body.
     */
    public Query rewriteQuery() {
        rewriteDuplicateVariables();
        rewriteConstants();
        List<Atom> newBody = new ArrayList<>();
        newBody.addAll(relationalAtoms);
        newBody.addAll(comparisonAtoms);
        return new Query(head, newBody);
    }

    /**
     * Method used the split the body of a query into two, relational atoms and comparison atoms.
     * @param body The body of the query; a list of atoms to split.
     */
    private void splitBody(List<Atom> body) {
        for (Atom atom : body) {
            if (atom instanceof RelationalAtom) {
                relationalAtoms.add((RelationalAtom) atom);
            }
            else {
                comparisonAtoms.add((ComparisonAtom) atom);
            }
        }
    }

    /**
     * Method used to store all variables currently in use.
     */
    private void storeVariables() {
        for (RelationalAtom relationalAtom : relationalAtoms) {
            for (Term term : relationalAtom.getTerms()) {
                if (term instanceof Variable) {
                    usedVariables.add((Variable) term);
                }
            }
        }
    }

    /**
     * Method used to rewrite all constants in the relational atoms.
     * Constants in relational atoms will be replaced by new variables and a comparison atom.
     * R(x,1) -> R(x,y), y=1
     */
    private void rewriteConstants() {
        List<RelationalAtom> oldRelationalAtoms = relationalAtoms;
        relationalAtoms = new ArrayList<>();

        // For every relational atom, check the terms to see if there are constants.
        // If there are constants, then replace it with a newly generated variable.
        // Also add new comparison atom.
        for (RelationalAtom relationalAtom : oldRelationalAtoms) {
            boolean constFound = false;
            List<Term> newTerms = new ArrayList<>();
            for (Term term : relationalAtom.getTerms()) {
                if (term instanceof Constant) {
                    Variable newVar = generateNewVar();
                    comparisonAtoms.add(new ComparisonAtom(newVar, term, ComparisonOperator.EQ));
                    newTerms.add(newVar);
                    constFound = true;
                } else {
                    newTerms.add(term);
                }
            }
            // If there was some change, use the new terms. Else just use the old relational atom.
            if (constFound) {
                relationalAtoms.add(new RelationalAtom(relationalAtom.getName(), newTerms));
            } else {
                relationalAtoms.add(relationalAtom);
            }
        }
    }

    /**
     * Method used to rewrite duplicate variables in the relational atoms.
     * duplicate variables in relational atoms will be replaced by new variables and a comparison atom.
     * R(x,x) -> R(x,y), x=y
     */
    private void rewriteDuplicateVariables() {
        List<RelationalAtom> oldRelationalAtoms = relationalAtoms;
        relationalAtoms = new ArrayList<>();

        Set<Variable> currentVariables  = new HashSet<>();

        // For every relational atom, check the terms and keep track of variables
        // to see if there are any duplicates. If a duplicate variable is found,
        // generate a new variables and replace, and add a new comparison atom.
        for (RelationalAtom relationalAtom : oldRelationalAtoms) {
            boolean duplicateFound = false;
            List<Term> newTerms = new ArrayList<>();
            for (Term term : relationalAtom.getTerms()) {
                if (term instanceof Variable) {
                    if (currentVariables.contains(term)) {
                        duplicateFound = true;
                        Variable newVar = generateNewVar();
                        newTerms.add(newVar);
                        currentVariables.add(newVar);
                        comparisonAtoms.add(new ComparisonAtom(term, newVar, ComparisonOperator.EQ));
                    } else {
                        currentVariables.add((Variable) term);
                        newTerms.add(term);
                    }
                } else {
                    newTerms.add(term);
                }
            }
            // If there was some change, use the new terms. Else just use the old relational atom.
            if (duplicateFound){
                relationalAtoms.add(new RelationalAtom(relationalAtom.getName(), newTerms));
            } else {
                relationalAtoms.add(relationalAtom);
            }

        }
    }

    /**
     * Method to generate a new variable that is not currently in use.
     * First we generate a random character. If this character is already a
     * variable in use, then generate another random character and append onto the
     * previously generated character. Keep doing this until you find a new variable.
     * @return A newly generated variable.
     */
    private Variable generateNewVar() {
        Random rnd = new Random();
        Variable rndVar = new Variable(String.valueOf((char) ('a' + rnd.nextInt(26))));
        while (usedVariables.contains(rndVar)) {
            rndVar.changeName(rndVar.getName() + (String.valueOf((char) ('a' + rnd.nextInt(26)))));
        }
        usedVariables.add(rndVar);
        return rndVar;
    }

}