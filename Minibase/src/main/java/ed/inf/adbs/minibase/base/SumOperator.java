package ed.inf.adbs.minibase.base;

import java.util.*;

/**
 * SumOperator class that is inherited from Operator class.
 * This operator is used when there is a sum aggregation in the head.
 */
public class SumOperator extends Operator{

    // Child Operator to aggregate over its tuples.
    private final Operator childOperator;
    // The SumAggregate object which tells us what to aggregate over.
    private final SumAggregate sumAggregate;
    // The variables that appears in the head before the SumAggregate.
    private final List<Variable> groupByVariables;

    // Output Tuple length is needed for new tuple creation.
    // Tuple length will be the # of group by variables + 1 for the aggregation.
    private final int tupleLength;

    // The output tuple has an extra value at the end (hence the +1 above).
    // Tuples need to specify their variables corresponding to each position.
    // So we make up a dummy variable with name "sumaggregatevariable" for this last value in the tuple.
    private static final Variable SUM_VARIABLE  = new Variable("sumaggregatevariable");

    // Specifying the variables of each position of the output tuples.
    // Will be the group variables with the SUM_VARIABLE added to the end.
    private final List<Variable> tupleVariables;

    // SumOperator is a blocking operator, i.e., we need to see all of its input before outputting.
    // So upon seeing all the input, we will have calculated all the output tuples, which we store here.
    private final List<Tuple> tuplesToReturn;


    /**
     * Constructor for SumOperator.
     * Assigns child operator, sum aggregate, group by variables, output tuple variables and output tuple length.
     * Finally, we use a method (computeAggregate()) to read all the child tuples, compute the aggregates, and store the tuples to return.
     * @param sumAggregate The SumAggregate object which tells us what to aggregate over.
     * @param childOperator Child Operator to aggregate over its tuples.
     * @param groupByVariables The variables that appears in the head before the SumAggregate.
     */
    public SumOperator(SumAggregate sumAggregate, Operator childOperator, List<Variable> groupByVariables) {
            this.childOperator = childOperator;
            this.sumAggregate = sumAggregate;
            this.groupByVariables = groupByVariables;
            tupleVariables = new ArrayList<>(groupByVariables);
            tupleVariables.add(SUM_VARIABLE);
            tupleLength = groupByVariables.size() + 1;
            tuplesToReturn = computeAggregate();
        }

    /**
     * Method that reads all the child tuples, does the aggregation, and returns the results of the aggregation as a list of tuples.
     * This method is used upon constructing a SumOperator object, so it is used only once for each SumOperator object.
     * This is because we only need to compute the aggregation once and store the results, then we can call getNextTuple repeatedly to pull
     * results from the results list one by one.
      * @return A list of tuples grouped and aggregated over.
     */
    private List<Tuple> computeAggregate() {

        // Assign the first child tuple in childTuple.
        Tuple childTuple = childOperator.getNextTuple();
        // aggregatedChildTuples will store child tuples grouped by the groupByVariables.
        HashMap<List<Constant>, Integer> aggregatedChildTuples = new HashMap<>();
        // The terms appearing in the aggregation.
        List<Term> sumAggregateTerms = sumAggregate.getProductTerms();

        // This loop groups the child tuples while computing aggregates on the fly.
        // Sum allows us to do this as we can just add onto our last results.
        while (childTuple != null) {
            // We start at one here because we add 1*constant at the beginning.
            int sumToAdd = 1;

            // This will store the child tuple constants that correspond to the variables in the groupByVariables.
            List<Constant> group = new ArrayList<>();
            for (Variable variable : groupByVariables) {
                group.add(childTuple.getConstantFromVariable(variable));
            }

            // For every term in the aggregation variables/constants, get its integer value and multiply and add accordingly.
            for (Term term : sumAggregateTerms) {
                IntegerConstant intToAdd;
                if (term instanceof Variable) {
                    intToAdd = (IntegerConstant) childTuple.getConstantFromVariable((Variable) term);
                } else {
                    intToAdd = ((IntegerConstant) term);
                }
                sumToAdd = sumToAdd * intToAdd.getValue();

            }

            // We have computed the aggregation on this tuple, we add the sumToAdd to our previous results for the respective group.
            sumPut(aggregatedChildTuples, group, sumToAdd);

            //Ready to repeat for next child tuple.
            childTuple = childOperator.getNextTuple();
        }


        List<Tuple> returnTuples = new ArrayList<>();

        // We iterate over each group and populate the returnTuples list.
        for (List<Constant> group : aggregatedChildTuples.keySet()) {

            int counter = 0;
            Constant[] values = new Constant[tupleLength];

            for (Constant constant : group) {
                values[counter] = constant;
                counter += 1;
            }

            values[counter] = new IntegerConstant(aggregatedChildTuples.get(group));

            returnTuples.add(new Tuple(values, tupleVariables));
        }

        return returnTuples;
    }

    /**
     * GetNextTuple method for SumOperator.
     * Aggregates will have been computed and stored in tuplesToReturn.
     * So we just remove a tuple (the first element) from this list and return. (Note order of output tuples does not matter).
     * If we cannot remove a tuple because the length of the list is 0, then return null indicating there are no more output tuples.
     * @return An aggregated tuple.
     */
    @Override
    public Tuple getNextTuple() {
        if (tuplesToReturn.size() > 0) {
            return tuplesToReturn.remove(0);
        } else {
            return null;
        }
    }

    /**
     * Reset method for SumOperator.
     * Resets the child operator until ultimately the scanner of the scan operator has reset.
     */
    @Override
    public void reset() {
        childOperator.reset();
    }

    /**
     * Helper method that adds a given value onto a hashmap value.
     * If the given key does not exist yet, we simply put the key value pair into the hashmap.
     * If the given key exists, we need to get the corresponding value from the hashmap, add the given value, and put
     * the sum back into the hashmap.
     * In this case, the hashmap will always be the aggregatedChildTuples hashmap defined in computeAggregate method.
     * @param map aggregatedChildTuples defined in computeAggregate().
     * @param group The key of the hashmap of the value we want to add to.
     * @param intToAdd The value we want to add.
     */
    private void sumPut(HashMap<List<Constant>, Integer> map, List<Constant> group, Integer intToAdd) {
        Integer currentSum = map.get(group);
        if (currentSum == null) {
            currentSum = 0;
        }
        currentSum += intToAdd;
        map.put(group, currentSum);
    }
}
