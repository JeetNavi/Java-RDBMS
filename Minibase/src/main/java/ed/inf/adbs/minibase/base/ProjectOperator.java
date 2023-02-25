package ed.inf.adbs.minibase.base;

import java.util.ArrayList;
import java.util.List;

public class ProjectOperator extends Operator {

    private List<Tuple> reportedTuples = new ArrayList<>();

    private Operator childOperator;

    private List<Integer> headVarsPos = new ArrayList<>();

    public ProjectOperator(Operator childOperator, Head queryHead) {

        DatabaseCatalog catalog = DatabaseCatalog.getCatalogInstance();

        this.childOperator = childOperator;
        List<Variable> headVars = queryHead.getVariables();

        for (Variable variable : headVars) {
            headVarsPos.add(catalog.getVarPos(variable));
        }

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

        Constant[] childTupleVals = childTuple.getValues();
        Tuple projectedTuple;
        Constant[] projectedTupleVals = new Constant[headVarsPos.size()];

        int posCounter = 0;

        for (Integer varPos : headVarsPos) {
            projectedTupleVals[posCounter] = childTupleVals[varPos];
            posCounter += 1;
        }

        projectedTuple = new Tuple(projectedTupleVals);

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
