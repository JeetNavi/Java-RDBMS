package ed.inf.adbs.minibase.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This class is used to create a query plan for a given query.
 */
public class QueryPlan {

    // The query to base the plan off of.
    private final Query query;

    // Relational and comparison atoms of the body of the query.
    private final List<RelationalAtom> relationalAtoms;
    private final List<ComparisonAtom> comparisonAtoms;

    // Stores which relation/relational atom number a variable is part of.
    // i.e., in the body: R(x), S(y, z): x -> 1, y -> 2, z -> 2.
    // We use numbers instead of relation names to allow duplicate relation names in body.
    private final HashMap<Variable, Integer> variableToRelationNumber;

    // Maps all the relation numbers to their selection conditions (if they have any).
    // i.e., R(x), x=2: 0 -> x=2.
    private final HashMap<Integer, List<ComparisonAtom>> relationToSelectionConditions;
    // Maps relation numbers to join conditions.
    // Join conditions are on two relations, so the join conditions are on the relation number and relation number - 1.
    // i.e., R(x), S(y), x=y: 1 -> x=y
    private final HashMap<Integer, List<ComparisonAtom>> relationToJoinConditions;
    // Used to keep track of used variables (in order) to determine whether projection is needed.
    private final List<Variable> usedVariables;

    // The root operator. This will be our actual query plan; a combination of operators.
    private Operator rootOperator;

    /**
     * Constructor for QueryPlan.
     * Assigns relational and comparison atoms by splitting the body via a helper method splitBody().
     * Groups the comparison atoms into selection conditions and join conditions (on relation number) via a helper method groupComparisonAtoms.
     * groupComparisonAtoms may return false indicating there was a comparison atom that will never hold (i.e., 1=2). In this case
     * We do not proceed further, causing the rootOperator to remain as null, indicating there is no output to the CQ.
     * Otherwise, we proceed to build the query plan using the method buildQueryPlan().
     * @param query The query to base the plan off of.
     */
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

    /**
     * Method used to build a query plan from the query.
     * More information in code comments and README.
     * @return Root operator; the query plan.
     */
    private Operator buildQueryPlan() {

        List<Operator> scansAndSelections = new ArrayList<>();

        // Firstly, all relational atoms will be a scan operator.
        // Some of these will be a select operator depending on if it has selection conditions.
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

        // If there is more than one relational atom, there will be at least one join.
        // If there is n relational atoms, there is n-1 joins.
        // By the end of this if statement, the root operator will be a join operator (if there is >1 relational atoms).
        // If there is 1 relation atom, then the root operator will remain either a scan or a select.
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

        // Get the sumAggregate from the head.
        // If there isn't one, sumAggregate will be null.
        SumAggregate sumAggregate = query.getHead().getSumAggregate();

        // If no sum aggregate, root operator may be projection.
        if (sumAggregate == null) {
            // if head variables are re-ordered or doesn't consist of the same variables, then we need to apply projection.
            if (!query.getHead().getVariables().equals(usedVariables)){
                operator = new ProjectOperator(operator, query.getHead());
            }
        } else {
            // There is a sumAggregate, so the root operator will be a sumOperator.
            operator = new SumOperator(sumAggregate, operator, query.getHead().getVariables());
        }

        return operator;

    }

    /**
     * Getter for rootOperator/query plan.
     * @return root operator.
     */
    public Operator getRootOperator() {
        return rootOperator;
    }

    /**
     * This method is used to group the comparison atoms of the query body into sensible groups.
     * There are two types of groups: selection conditions groups and join conditions groups.
     * A group in the selection condition groups consist of a list of selection conditions for each relational atom (relation number).
     * A group in the join condition groups consist of a join condition (as a SelectionCondition).
     * More information about this method in README.
     * @return false indicating there was a comparison atom that will never hold (i.e., 1=2). Otherwise true.
     */
    private boolean groupComparisonAtoms() {
        for (ComparisonAtom comparisonAtom : comparisonAtoms) {
            Term lhs = comparisonAtom.getTerm1();
            Term rhs = comparisonAtom.getTerm2();

            if (lhs instanceof Variable && rhs instanceof Variable) {
                // This can either be a selection condition or join condition.
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
                // Evaluate on dummy tuple.
                if (!constantCondition.evaluateOnTuple(new Tuple(null, null))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Helper method that adds a given comparison atom onto a hashmap value which is a list.
     * If the given key does not exist yet, we add the key and initialize an empty list before adding the value to this list.
     * If the given key exists, we need to get the corresponding list from the hashmap, add the given value to the end of the list, and put
     * the list back into the hashmap.
     * @param map The hashmap.
     * @param relationNumber The key of the hashmap of the value (list) we want to add to.
     * @param atomToAppend The value we want to add to the list.
     */
    private void appendPut(HashMap<Integer, List<ComparisonAtom>> map, int relationNumber, ComparisonAtom atomToAppend) {
        List<ComparisonAtom> currentConditions = map.get(relationNumber);
        if (currentConditions == null) {
            currentConditions = new ArrayList<>();
        }
        currentConditions.add(atomToAppend);
        map.put(relationNumber, currentConditions);
    }

    /**
     * Method used to store which relation number (relational atom number) each variable belongs to.
     * This method also stores all the used variables in order of which they appear in the body (in the relational atoms).
     * This method assumes splitBody() has been called.
     */
    private void storeVariableRelations() {
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

    /**
     * Method used the split the body of the query into two, relational atoms and comparison atoms.
     */
    private void splitBody() {
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
