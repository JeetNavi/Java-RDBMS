package ed.inf.adbs.minibase.base;

import java.util.ArrayList;
import java.util.List;

public class ProjectOperator extends Operator {

    private List<Tuple> reportedTuples = new ArrayList<>();

    private Operator childOperator;

    private List<Variable> headVariables;

    public ProjectOperator(Operator childOperator, Head queryHead) {

        this.childOperator = childOperator;
        this.headVariables = queryHead.getVariables();

    }

    /**
     * @return
     */
    @Override
    public Tuple getNextTuple() {

        Tuple childTuple = childOperator.getNextTuple();

        if (childTuple == null) {
            return null;
        }

        Tuple projectedTuple;
        Constant[] projectedTupleVals = new Constant[headVariables.size()];

        int posCounter = 0;

        for (Variable variable : headVariables) {
            projectedTupleVals[posCounter] = childTuple.getConstantFromVariable(variable);
            posCounter += 1;
        }

        projectedTuple = new Tuple(projectedTupleVals, headVariables);

        if (!(reportedTuples.contains(projectedTuple))) {
            reportedTuples.add(projectedTuple);
            return projectedTuple;
        }
        else {
            return getNextTuple();
        }

    }

    /**
     *
     */
    @Override
    public void reset() {
        childOperator.reset();
    }
}