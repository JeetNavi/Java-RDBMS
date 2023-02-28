package ed.inf.adbs.minibase.base;

import java.util.List;

public class SelectOperator extends Operator{

    private SelectionCondition conditions;

    private Operator childOperator;

    public SelectOperator(SelectionCondition conditions, Operator childOperator) {
        this.conditions = conditions;
        this.childOperator = childOperator;
    }

    /**
     * @return
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
     *
     */
    @Override
    public void reset() {
        childOperator.reset();
    }
}
