package ed.inf.adbs.minibase.base;

import java.util.List;

public class SelectOperator extends Operator{

    private List<ComparisonAtom> conditions;

    private Operator childOperator;

    public SelectOperator(List<ComparisonAtom> conditions, Operator childOperator) {
        this.conditions = conditions;
        this.childOperator = childOperator;
    }

    /**
     * @return
     */
    @Override
    public Tuple getNextTuple() {

        SelectionCondition selectionCondition = new SelectionCondition(conditions);
        Tuple childTuple;

        do {
            childTuple = childOperator.getNextTuple();

            if (selectionCondition.evaluateOnTuple(childTuple)) {
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
