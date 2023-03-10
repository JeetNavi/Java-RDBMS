package ed.inf.adbs.minibase.base;

import java.util.*;

public class Rewriter {

    private Head head;
    private List<Atom> body;
    private List<RelationalAtom> relationalAtoms;
    private List<ComparisonAtom> comparisonAtoms;
    private Set<Variable> variables;

    public Rewriter(Query query) {
        body = query.getBody();
        head = query.getHead();
        relationalAtoms = new ArrayList<>();
        comparisonAtoms = new ArrayList<>();
        variables = new HashSet<>();
        splitBody();
        storeVariables();
    }

    private void splitBody() {
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
                    variables.add((Variable) term);
                }
            }
        }
    }

    public Query rewriteQuery() {

        storeVariables();
        rewriteDuplicateVariables();
        rewriteConstants();
        body = new ArrayList<>();
        body.addAll(relationalAtoms);
        body.addAll(comparisonAtoms);
        //Query rewrittenQuery = new Query(head, body);
        //storeVariableInformation();
        //simplifyCompAtoms();
        return new Query(head, body);
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

    private Variable generateNewVar() {
        Random rnd = new Random();
        Variable rndVar = new Variable(String.valueOf((char) ('a' + rnd.nextInt(26))));
        while (variables.contains(rndVar)) {
            rndVar.changeName(rndVar.getName() + (String.valueOf((char) ('a' + rnd.nextInt(26)))));
        }
        variables.add(rndVar);
        return rndVar;
    }

}
