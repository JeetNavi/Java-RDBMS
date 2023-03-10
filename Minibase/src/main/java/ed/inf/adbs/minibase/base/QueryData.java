package ed.inf.adbs.minibase.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueryData {

    private static QueryData queryData = null;

    private final Query query;
    private List<String> relationsInOrder;
    private HashMap<Integer, List<Variable>> relationVariables;
    private HashMap<Variable, Integer> globalVarPositions;
    private HashMap<Integer, HashMap<Variable, Integer>> localVarPositions;

    private QueryData(Query query) {
        this.query = query;
        relationsInOrder = new ArrayList<>();
        relationVariables = new HashMap<>();
        globalVarPositions = new HashMap<>();
        localVarPositions = new HashMap<>();
        storeRelationsInOrder();
        storeRelationVariables();
        storeLocalVarPositions();
        storeGlobalVarPositions();
    }

    public static QueryData getInstance() {

        if (queryData == null) {
            throw new AssertionError("A QueryData instance has not been initialized.");
        }

        return queryData;
    }

    public synchronized static QueryData init(Query query) {
        if (queryData != null) {
            throw new AssertionError("A QueryData instance has already been initialized.");
        }

        queryData = new QueryData(query);
        return queryData;
    }

    private void storeGlobalVarPositions() {
        // Assuming all duplicate variables have been dealt with.
        int globalPosCounter = 0;
        for (Atom atom : query.getBody()) {
            if (atom instanceof RelationalAtom) {
                for (Term term : ((RelationalAtom) atom).getTerms()) {
                    if (term instanceof Variable) { // Should always be true after rewriting constants.
                        globalVarPositions.put((Variable) term, globalPosCounter);
                    }
                    globalPosCounter += 1;
                }
            }
        }
    }

    private void storeLocalVarPositions() {
        int atomCounter = 0;
        for (Atom atom : query.getBody()) {
            if (atom instanceof RelationalAtom) {
                HashMap<Variable, Integer> variablePositions = new HashMap<>();
                int termCcounter = 0;
                for (Term term : ((RelationalAtom) atom).getTerms()) {
                    if (term instanceof Variable) { // Should always be true after rewriting constants.
                        variablePositions.put((Variable) term, termCcounter);
                    }
                    termCcounter += 1;
                }
                localVarPositions.put(atomCounter, variablePositions);
            }
            atomCounter += 1;
        }
    }

    private void storeRelationsInOrder() {
        for (Atom atom : query.getBody()) {
            if (atom instanceof RelationalAtom) {
                String relationName = ((RelationalAtom) atom).getName();
                relationsInOrder.add(relationName);
            }
        }
    }

    private void storeRelationVariables() {
        int relationCounter = 0;
        for (Atom atom : query.getBody()) {
            if (atom instanceof RelationalAtom) {
                List<Variable> variables = new ArrayList<>();
                for (Term term : ((RelationalAtom) atom).getTerms()) {
                    if (term instanceof Variable) { // Should always be true after rewriting constants.
                        variables.add((Variable) term);
                    }
                }
                relationVariables.put(relationCounter, variables);
                        relationCounter += 1;
            }
        }
    }

    public List<String> getRelationsInOrder() {
        return relationsInOrder;
    }

    public HashMap<Integer, List<Variable>> getRelationVariables() {
        return relationVariables;
    }

    public HashMap<Variable, Integer> getGlobalVarPositions() {
        return globalVarPositions;
    }

    public HashMap<Integer, HashMap<Variable, Integer>> getLocalVarPositions() {
        return localVarPositions;
    }
}
