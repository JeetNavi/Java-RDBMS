package ed.inf.adbs.minibase.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class QueryPlan {

    private final Query query;
    
    private final List<RelationalAtom> relationalAtoms;
    private final List<ComparisonAtom> comparisonAtoms;
    private final HashMap<Variable, Integer> variableToRelationNumber;
    private final HashMap<Integer, List<ComparisonAtom>> relationToSelectionConditions;
    private final HashMap<Integer, List<ComparisonAtom>> relationToJoinConditions;
    private final List<Variable> usedVariables;


    private Operator rootOperator;

    public QueryPlan(Query query) {
        this.query = query;

        relationalAtoms = new ArrayList<>();
        comparisonAtoms = new ArrayList<>();
        variableToRelationNumber = new HashMap<>();

        relationToJoinConditions = new HashMap<>();
        relationToSelectionConditions = new HashMap<>();
        usedVariables = new ArrayList<>();

        splitBody();
        storeVariableRelations();
        if (!groupComparisonAtoms()) {
            return;
        }
        
        rootOperator = buildQueryPlan();
    }

    private Operator buildQueryPlan() {
        List<Operator> scansAndSelections = new ArrayList<>();

        Operator operator = null;
        int atomCounter = 0;
        for (RelationalAtom relationalAtom : relationalAtoms) {
            operator = new ScanOperator(relationalAtom);
            List<ComparisonAtom> selectionConditions = relationToSelectionConditions.get(atomCounter);
            if (selectionConditions != null) {
                operator = new SelectOperator(new SelectionCondition(selectionConditions), operator);
            }
            atomCounter += 1;
            scansAndSelections.add(operator);
        }


        if (scansAndSelections.size() > 1) {
            List<ComparisonAtom> joinCondition = relationToJoinConditions.get(1);
            if (joinCondition == null) {
                operator = new JoinOperator(scansAndSelections.get(0), scansAndSelections.get(1),  null);
            } else {
                operator = new JoinOperator(scansAndSelections.get(0), scansAndSelections.get(1), new SelectionCondition(joinCondition));
            }
            for (int i = 2; i < scansAndSelections.size(); i++) {
                joinCondition = relationToJoinConditions.get(i);
                if (joinCondition == null) {
                    operator = new JoinOperator(operator, scansAndSelections.get(i), null);
                } else {
                    operator = new JoinOperator(operator, scansAndSelections.get(i), new SelectionCondition(joinCondition));
                }
            }
        }

        SumAggregate sumAggregate = query.getHead().getSumAggregate();

        if (sumAggregate == null) {
            if (!query.getHead().getVariables().equals(usedVariables)){
                operator = new ProjectOperator(operator, query.getHead());
            }
        } else {
            operator = new SumOperator(sumAggregate, operator, query.getHead().getVariables());
        }

        return operator;

    }

    public Operator getRootOperator() {
        return rootOperator;
    }

    private boolean groupComparisonAtoms() {
        for (ComparisonAtom comparisonAtom : comparisonAtoms) {
            Term lhs = comparisonAtom.getTerm1();
            Term rhs = comparisonAtom.getTerm2();

            if (lhs instanceof Variable && rhs instanceof Variable) {
                int lhsRelationNumber = variableToRelationNumber.get(lhs);
                int rhsRelationNumber = variableToRelationNumber.get(rhs);
                if (lhsRelationNumber == rhsRelationNumber) {
                    // Selection condition on 1 relation.
                    appendPut(relationToSelectionConditions, lhsRelationNumber, comparisonAtom);
                } else {
                    // Join condition with 2 relations.
                    appendPut(relationToJoinConditions, Math.max(lhsRelationNumber, rhsRelationNumber), comparisonAtom);
                }

            } else if (lhs instanceof Variable && rhs instanceof Constant) {
                // Selection condition on 1 relation.
                int relationNumber = variableToRelationNumber.get(lhs);
                appendPut(relationToSelectionConditions, relationNumber, comparisonAtom);

            } else if (lhs instanceof Constant && rhs instanceof Variable) {
                // Selection condition on 1 relation.
                int relationNumber = variableToRelationNumber.get(rhs);
                appendPut(relationToSelectionConditions, relationNumber, comparisonAtom);

            } else if (lhs instanceof Constant && rhs instanceof Constant) {
                // Condition of constants.
                // Condition that holds -> removed.
                // Condition that doesn't hold -> no results to CQ.
                SelectionCondition constantCondition  = new SelectionCondition(Collections.singletonList(comparisonAtom));
                if (!constantCondition.evaluateOnTuple(new Tuple(null, null))) {
                    return false;
                }
            }
        }
        return true;
    }

    private void appendPut(HashMap<Integer, List<ComparisonAtom>> map, int relationNumber, ComparisonAtom atomToAppend) {
        List<ComparisonAtom> currentConditions = map.get(relationNumber);
        if (currentConditions == null) {
            currentConditions = new ArrayList<>();
        }
        currentConditions.add(atomToAppend);
        map.put(relationNumber, currentConditions);
    }

    private void storeVariableRelations() {
        // Assumes split body
        // Also stores variables in order into usedVariables.
        int relationCounter = 0;
        for (RelationalAtom relationalAtom : relationalAtoms) {
            for (Term term : relationalAtom.getTerms()) {
                // Term will be a distinct variable after rewriting.
                variableToRelationNumber.put((Variable) term, relationCounter);
                usedVariables.add((Variable) term);
            }
            relationCounter += 1;
        }
    }

    private void splitBody() {
        // Split the body into two; relational and comparison atoms.
        for (Atom atom : query.getBody()) {
            if (atom instanceof RelationalAtom) {
                relationalAtoms.add((RelationalAtom) atom);
            }
            else {
                comparisonAtoms.add((ComparisonAtom) atom);
            }
        }
    }

    }


