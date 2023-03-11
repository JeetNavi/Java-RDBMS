package ed.inf.adbs.minibase.base;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * ProjectOperator class that is inherited from Operator class.
 * Supporting projection.
 */
public class ProjectOperator extends Operator {

    // Used to keep track of previously returned tuples to avoid returning duplicate tuples.
    private final HashSet<Tuple> reportedTuples = new HashSet<>();

    private final Operator childOperator;

    // Head variables used to identify what to project on.
    private final List<Variable> headVariables;

    /**
     * Constructor for ProjectOperator class.
     * Assigns child operator and head variables.
     * @param childOperator Operator to project over.
     * @param queryHead Head object. Head of the query containing variables to project.
     */
    public ProjectOperator(Operator childOperator, Head queryHead) {
        this.childOperator = childOperator;
        this.headVariables = queryHead.getVariables();
    }

    /**
     * GetNextTuple method for ProjectOperator.
     * Simply takes the next tuple from the child operator and project on the variables in headVariables.
     * Then we check that this tuple has not been previously returned and return.
     * If it has been returned previously, then repeat the process.
     * @return The child operators next tuple with variables projected away.
     */
    @Override
    public Tuple getNextTuple() {

        Tuple childTuple = childOperator.getNextTuple();

        // No more tuples to project on.
        if (childTuple == null) {
            return null;
        }

        Tuple projectedTuple;
        Constant[] projectedTupleVales = new Constant[headVariables.size()];

        int posCounter = 0;

        for (Variable variable : headVariables) {
            projectedTupleVales[posCounter] = childTuple.getConstantFromVariable(variable);
            posCounter += 1;
        }

        projectedTuple = new Tuple(projectedTupleVales, headVariables);

        // Check that the projected tuple is distinct.
        if (!(reportedTuples.contains(projectedTuple))) {
            reportedTuples.add(projectedTuple);
            return projectedTuple;
        }
        else {
            return getNextTuple();
        }

    }

    /**
     * Reset method for ProjectOperator.
     * Resets the child operator until ultimately the scanner of the scan operator has reset.
     */
    @Override
    public void reset() {
        childOperator.reset();
    }
}