package ed.inf.adbs.minibase.base;

import java.util.*;

public class SumOperator extends Operator{

    private Operator childOperator;
    private SumAggregate sumAggregate;
    private List<Variable> groups;

    private int tupleLength;

    private static final Variable SUM_VARIABLE  = new Variable("sumvariable");

    private List<Tuple> tuplesToReturn;


    public SumOperator(SumAggregate sumAggregate, Operator childOperator, List<Variable> groups) {
            this.childOperator = childOperator;
            this.sumAggregate = sumAggregate;
            this.groups = groups;
            tupleLength = groups.size() + 1;
            tuplesToReturn = computeAggregate();
        }

    private List<Tuple> computeAggregate() {
        //List<Tuple> childTuples = new ArrayList<>();
        Tuple childTuple = childOperator.getNextTuple();
        HashMap<List<Constant>, Integer> groupedChildTuples = new HashMap<>();
        List<Term> sumAggregateTerms = sumAggregate.getProductTerms();
        List<Variable> sumAggregateVariables = new ArrayList<>();
        List<Variable> variables = new ArrayList<>();

        for (Term term : sumAggregateTerms) {
            if (term instanceof Variable) {
                sumAggregateVariables.add((Variable) term);
            }
        }

        variables.addAll(groups);
        variables.add(SUM_VARIABLE);
        //variablesToKeep.addAll(groups);
        //variablesToKeep.addAll(sumAggregateVariables);

        while (childTuple != null) {
            List<Constant> group = new ArrayList<>();
            int sumToAdd = 1;
            for (Variable variable : groups) {
                group.add(childTuple.getConstantFromVariable(variable));
            }
            for (Term term : sumAggregateTerms) {
                IntegerConstant intToAdd;
                if (term instanceof Variable) {
                    intToAdd = (IntegerConstant) childTuple.getConstantFromVariable((Variable) term);
                } else {
                    intToAdd = ((IntegerConstant) term);
                }
                sumToAdd = sumToAdd * intToAdd.getValue();

            }
            sumPut(groupedChildTuples, group, sumToAdd);
            //.add(childTuple);
            childTuple = childOperator.getNextTuple();
        }

        List<Tuple> returnTuples = new ArrayList<>();

        for (List<Constant> group : groupedChildTuples.keySet()) {

            int counter = 0;
            Constant[] values = new Constant[tupleLength];

            for (Constant constant : group) {
                values[counter] = constant;
                counter += 1;
            }

            values[counter] = new IntegerConstant(groupedChildTuples.get(group));


            returnTuples.add(new Tuple(values, variables));
        }

        return returnTuples;
    }

    /**
     * @return
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
     *
     */
    @Override
    public void reset() {
        childOperator.reset();
    }

    private void sumPut(HashMap<List<Constant>, Integer> map, List<Constant> group, Integer intToAdd) {
        Integer currentSum = map.get(group);
        if (currentSum == null) {
            currentSum = 0;
        }
        currentSum += intToAdd;
        map.put(group, currentSum);
    }
}
