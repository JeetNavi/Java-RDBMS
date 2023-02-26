package ed.inf.adbs.minibase.base;

import java.util.ArrayList;
import java.util.List;

public class JoinOperator extends Operator {

    private Operator leftChildOperator;

    private Operator rightChildOperator;

    private SelectionCondition joinCondition;

    private Tuple leftChildTuple;
    private Tuple rightChildTuple;
    private final int leftTupleLen;
    private final int rightTupleLen;

    public JoinOperator(Operator leftChildOperator, Operator rightChildOperator, SelectionCondition joinCondition) {
        this.leftChildOperator = leftChildOperator;
        this.rightChildOperator = rightChildOperator;
        this.joinCondition = joinCondition;
        leftChildTuple = leftChildOperator.getNextTuple();
        rightChildTuple = rightChildOperator.getNextTuple();
        leftTupleLen = leftChildTuple.getValues().length;
        rightTupleLen = rightChildTuple.getValues().length;
    }

    /**
     * @return
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
                Tuple joinedTuple = new Tuple(joinedTupleValues);
                if (joinCondition == null) {
                    rightChildTuple = rightChildOperator.getNextTuple();
                    return joinedTuple;
                }
                else {
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
     *
     */
    @Override
    public void reset() {
        leftChildOperator.reset();
        rightChildOperator.reset();
    }
}
