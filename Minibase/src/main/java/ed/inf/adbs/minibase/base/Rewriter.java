package ed.inf.adbs.minibase.base;

import java.util.*;

public class Rewriter {

    private Head head;
    private List<RelationalAtom> relationalAtoms;
    private List<ComparisonAtom> comparisonAtoms;
    private List<Variable> usedVariables;

    public Rewriter(Query query) {
        List<Atom> body = query.getBody();
        head = query.getHead();
        relationalAtoms = new ArrayList<>();
        comparisonAtoms = new ArrayList<>();
        usedVariables = new ArrayList<>();

        splitBody(body);
        storeVariables();
    }

    public Query rewriteQuery() {
        rewriteDuplicateVariables();
        rewriteConstants();
        List<Atom> newBody = new ArrayList<>();
        newBody.addAll(relationalAtoms);
        newBody.addAll(comparisonAtoms);
        return new Query(head, newBody);
    }

    private void splitBody(List<Atom> body) {
        // Split the body into two; relational and comparison atoms.
        for (Atom atom : body) {
            if (atom instanceof RelationalAtom) {
                relationalAtoms.add((RelationalAtom) atom);
            }
            else {
                comparisonAtoms.add((ComparisonAtom) atom);
            }
        }
    }

    private void storeVariables() {
        for (RelationalAtom relationalAtom : relationalAtoms) {
            for (Term term : relationalAtom.getTerms()) {
                if (term instanceof Variable) {
                    usedVariables.add((Variable) term);
                }
            }
        }
    }

    private void rewriteConstants() {
        List<RelationalAtom> oldRelationalAtoms = relationalAtoms;
        relationalAtoms = new ArrayList<>();

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
            if (constFound) {
                relationalAtoms.add(new RelationalAtom(relationalAtom.getName(), newTerms));
            } else {
                relationalAtoms.add(relationalAtom);
            }
        }
    }

    private void rewriteDuplicateVariables() {
        List<RelationalAtom> oldRelationalAtoms = relationalAtoms;
        relationalAtoms = new ArrayList<>();

        Set<Variable> currentVariables  = new HashSet<>();

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
            if (duplicateFound){
                relationalAtoms.add(new RelationalAtom(relationalAtom.getName(), newTerms));
            } else {
                relationalAtoms.add(relationalAtom);
            }

        }
    }

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