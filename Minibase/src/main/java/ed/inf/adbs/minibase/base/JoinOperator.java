package ed.inf.adbs.minibase.base;

import java.util.List;

/**
 * JoinOperator class that is inherited from Operator class.
 * The algorithm used in this class is the simple tuple nested loop join algorithm.
 */
public class JoinOperator extends Operator {

    private final Operator leftChildOperator;
    private final Operator rightChildOperator;
    // The join condition is a SelectionCondition object, allowing us to specify >1 join condition here.
    private final SelectionCondition joinCondition;

    // These are used to keep track of the tuples in the children operators.
    private Tuple leftChildTuple;
    private Tuple rightChildTuple;

    // These are constants that rely on children operators, hence they are not marked as static.
    private final int leftTupleLen;
    private final int rightTupleLen;
    // Each tuple specifies the variables that correspond to each position. This is needed for creation of new tuples.
    private final List<Variable> joinedTupleVars;

    /**
     * Constructor of a JoinOperator.
     * Assigns children operators and join conditions.
     * Makes leftChildTuple and rightChildTuple point to first tuple of the respective child operator.
     * Stores length of child tuples using the first tuple as the lengths will not change.
     * The lengths are needed for creation of new tuples (we need to specify the length of the tuple due to array usage).
     * The length of the joined tuple will be the sum of the length of the two tuples.
     * The respective variables are also stored for new tuple creation.
     * @param leftChildOperator The left (outer) operator in the join.
     * @param rightChildOperator The right (inner) operator in the join.
     * @param joinCondition A selectionCondition object containing the join conditions. This is null if there are no join conditions (cartesian product)
     */
    public JoinOperator(Operator leftChildOperator, Operator rightChildOperator, SelectionCondition joinCondition) {
        this.leftChildOperator = leftChildOperator;
        this.rightChildOperator = rightChildOperator;
        this.joinCondition = joinCondition;

        leftChildTuple = leftChildOperator.getNextTuple();
        rightChildTuple = rightChildOperator.getNextTuple();

        joinedTupleVars = leftChildTuple.getVariables();
        joinedTupleVars.addAll(rightChildTuple.getVariables());
        leftTupleLen = leftChildTuple.getValues().length;
        rightTupleLen = rightChildTuple.getValues().length;
    }

    /**
     * GetNextTuple method for JoinOperator.
     * Implements the simple tuple nested loop join algorithm (expensive).
     * @return The next joined tuple that satisfies the join condition (if it exists).
     */
    @Override
    public Tuple getNextTuple() {

        // For every tuple in the left outer child, scan the right inner child until there is a match, or if there is null reset and move onto next outer tuple.
        while (leftChildTuple != null) {
            Constant[] leftTupleValues = leftChildTuple.getValues();
            while (rightChildTuple != null) {
                Constant[] rightTupleValues = rightChildTuple.getValues();
                Constant[] joinedTupleValues = new Constant[leftTupleLen + rightTupleLen];
                System.arraycopy(leftTupleValues, 0, joinedTupleValues, 0, leftTupleLen);
                System.arraycopy(rightTupleValues, 0, joinedTupleValues, leftTupleLen, rightTupleLen);
                Tuple joinedTuple = new Tuple(joinedTupleValues, joinedTupleVars);
                if (joinCondition == null) {
                    // The join condition is null. Update rightChildTuple and return the joinedTuple without checking of selections.
                    rightChildTuple = rightChildOperator.getNextTuple();
                    return joinedTuple;
                }
                else {
                    // There exists a join condition, we need to check if this condition holds on the current joined tuple.
                    if (joinCondition.evaluateOnTuple(joinedTuple)) {
                        rightChildTuple = rightChildOperator.getNextTuple();
                        return joinedTuple;
                    }
                }

                rightChildTuple = rightChildOperator.getNextTuple();
            }

            // Right child tuple is null.
            rightChildOperator.reset();
            rightChildTuple = rightChildOperator.getNextTuple();
            leftChildTuple = leftChildOperator.getNextTuple();
        }

        // Left child tuple is null. There is no more matches.
        return null;
    }

    /**
     * Reset method for JoinOperator.
     * Resets the children operators until ultimately the scanners of the scan operators have reset.
     */
    @Override
    public void reset() {
        leftChildOperator.reset();
        rightChildOperator.reset();
    }
}