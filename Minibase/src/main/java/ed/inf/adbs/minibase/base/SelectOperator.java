package ed.inf.adbs.minibase.base;

/**
 * SelectOperator class that is inherited from Operator class.
 * This operator is used when there are selection conditions (not including join conditions) on a relation.
 */
public class SelectOperator extends Operator{

    // SelectionCondition object is used to represent the conditions we have (can hold >1 conditions).
    private final SelectionCondition conditions;

    // Child operator on which we apply the selection on.
    private final Operator childOperator;

    /**
     * Constructor for SelectOperator.
     * Assigns the conditions and the child operator.
     * @param conditions SelectionCondition object containing all the selection conditions.
     * @param childOperator The child operator on which we apply the selection on.
     */
    public SelectOperator(SelectionCondition conditions, Operator childOperator) {
        this.conditions = conditions;
        this.childOperator = childOperator;
    }

    /**
     * GetNextTuple method for SelectOperator.
     * Checks if the next child tuple satisfies the selection conditions and returns accordingly.
     * @return Next child tuple that satisfies the selection conditions. Returns null if there are no more satisfying child tuples.
     */
    @Override
    public Tuple getNextTuple() {

        Tuple childTuple;

        do {
            childTuple = childOperator.getNextTuple();

            if (conditions.evaluateOnTuple(childTuple)) {
                return childTuple;
            }

        } while (childTuple != null);

        return null;
    }

    /**
     * Reset method for SelectOperator.
     * Resets the child operator until ultimately the scanner of the scan operator has reset.
     */
    @Override
    public void reset() {
        childOperator.reset();
    }
}