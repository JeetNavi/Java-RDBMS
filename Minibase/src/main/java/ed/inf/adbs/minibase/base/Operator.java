package ed.inf.adbs.minibase.base;

import java.util.HashMap;

abstract class Operator {

    public abstract Tuple getNextTuple();

    public abstract void reset();

    //private HashMap<Term, Integer> positionOfTerms

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

    public Tuple toTuple(String tupleString) {
        String[] tupleSplit = tupleString.split(", |,");
        Constant[] tupleValues = new Constant[tupleSplit.length];

        int counter = 0;

        for (String constString : tupleSplit) {
            if (constString.charAt(0) == '\'') {
                constString = constString.substring(1, constString.length() - 1);
                tupleValues[counter] = new StringConstant(constString);
            }
            else {
                tupleValues[counter] = new IntegerConstant(Integer.parseInt(constString));
            }
            counter += 1;
        }

        return new Tuple(tupleValues);
    }

    public void addPositionOfTerm(Term term, int pos) {

    }
}
