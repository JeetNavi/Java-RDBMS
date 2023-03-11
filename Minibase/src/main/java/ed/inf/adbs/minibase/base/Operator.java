package ed.inf.adbs.minibase.base;

/**
 * Superclass for all operator types.
 */
public abstract class Operator {

    /**
     * Abstract method for getNextTuple.
     * Allows each subclass operator to implement their own ways of getting the next tuple.
     * @return Tuple object. The next tuple of the corresponding operator.
     */
    public abstract Tuple getNextTuple();

    /**
     * Abstract method for reset.
     * Allows each subclass operator to implement their own ways of resetting.
     * This ultimately will reset the scanner of a scan operator.
     */
    public abstract void reset();

    /**
     * Dump method for operators.
     * Calls getNextTuple repeatedly until the next tuple is null, i.e., there are no more satisfying tuples.
     */
    public void dump() {

        Tuple nextTuple;

        while (true) {

            nextTuple = getNextTuple();
            if (!(nextTuple == null)) {
                System.out.println(nextTuple);
            }
            else {
                break;
            }
        }

    }

}