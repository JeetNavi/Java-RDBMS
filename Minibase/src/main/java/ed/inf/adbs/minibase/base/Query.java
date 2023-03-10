package ed.inf.adbs.minibase.base;

import ed.inf.adbs.minibase.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Query {
    private Head head;

    private List<Atom> body;

    private HashMap<Variable, Integer> globalVarPositions;
    private HashMap<Integer, HashMap<Variable, Integer>> localVarPositions;
    private HashMap<Variable, String> varRelations;
    private List<String> relationsInOrder;

    public Query(Head head, List<Atom> body) {
        this.head = head;
        this.body = body;
        globalVarPositions = new HashMap<>();
        localVarPositions = new HashMap<>();
        varRelations = new HashMap<>();
        relationsInOrder = new ArrayList<>();
    }

    public void setBodyInfo() {

        setRelationsInOrder();
        setVarRelations();
        setLocalVarPositions();
        setGlobalVarPositions();

    }

    private void setGlobalVarPositions() {
        // Assuming all duplicate variables have been dealt with.
        int globalPosCounter = 0;
        for (Atom atom : body) {
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

    private void setLocalVarPositions() {
        int atomCounter = 0;
        for (Atom atom : body) {
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

    private void setVarRelations() {
        for (Atom atom : body) {
            if (atom instanceof RelationalAtom) {
                String relationName = ((RelationalAtom) atom).getName();
                for (Term term : ((RelationalAtom) atom).getTerms()) {
                    if (term instanceof Variable) {
                        varRelations.put((Variable) term, relationName);
                    }
                }
            }
        }
    }

    private void setRelationsInOrder() {
        for (Atom atom : body) {
            if (atom instanceof RelationalAtom) {
                String relationName = ((RelationalAtom) atom).getName();
                relationsInOrder.add(relationName);
            }
        }
    }


    public Head getHead() {
        return head;
    }

    public List<Atom> getBody() {
        return body;
    }

    @Override
    public String toString() {
        return head + " :- " + Utils.join(body, ", ");
    }


    /**
     * Method that removes a given atom from the body of the query.
     * @param atom Atom to remove from the body.
     */
    public void removeFromBody(RelationalAtom atom) {
        body.remove(atom);
    }
}
