package ed.inf.adbs.minibase.base;

import java.util.List;

/**
 * SelectionCondition class.
 * A SelectionCondition object is made up of a list of comparison atoms, i.e., a list of selection conditions.
 * This class provides a method to evaluate a tuple on a selection condition.
 */
public class SelectionCondition {

    // The comparison atoms / conditions of the selection condition.
    private final List<ComparisonAtom> conditions;

    /**
     * Constructor of SelectionCondition.
     * Assigns conditions.
     * @param conditions The comparison atoms that are the selection conditions (of a single relation).
     */
    public SelectionCondition(List<ComparisonAtom> conditions) {
        this.conditions = conditions;
    }

    /**
     * Method that can be publicly used to evaluate a SelectionCondition on a tuple.
     * I.e., for the selection condition containing one condition: x<y, with the tuple (1, 2) (with variables (x, y)),
     * this method will return true because 1<2 (x<y).
     * If there are multiple conditions, all conditions will be considered on the tuple.
     * @param tuple The tuple to evaluate the conditions on.
     * @return true if all the conditions hold on the tuple, false otherwise.
     */
    public boolean evaluateOnTuple(Tuple tuple) {

        if (tuple == null) {
            return false;
        }

        boolean conditionHolds = false;

        for (ComparisonAtom comparisonAtom : conditions) {
            // We evaluate each condition one-by-one rather than one big conjunction.

            switch (comparisonAtom.getOp()) {
                case EQ:
                    conditionHolds = evaluateConditionEQ(tuple, comparisonAtom);
                    break;
                case GT:
                    conditionHolds = evaluateConditionGT(tuple, comparisonAtom);
                    break;
                case LT:
                    conditionHolds = evaluateConditionLT(tuple, comparisonAtom);
                    break;
                case GEQ:
                    conditionHolds = evaluateConditionEQ(tuple, comparisonAtom);
                    if (conditionHolds) { break; }
                    conditionHolds = evaluateConditionGT(tuple, comparisonAtom);
                    break;
                case LEQ:
                    conditionHolds = evaluateConditionEQ(tuple, comparisonAtom);
                    if (conditionHolds) { break; }
                    conditionHolds = evaluateConditionLT(tuple, comparisonAtom);
                    break;
                case NEQ:
                    conditionHolds = !evaluateConditionEQ(tuple, comparisonAtom);
                    break;
            }

            if (!conditionHolds) {
                return false;
            }

        }

        return true; // All conditions hold on the tuple.
    }

    /**
     * Helper method for evaluateOnTuple.
     * This method is used if one of the conditions contains the "less than" operator.
     * Or if the condition contains an operator that can be simplified to use a combination of operators including "less than".
     * This method evaluates the tuple based on the one comparison atom.
     * @param tuple The tuple to evaluate the "less than" condition on.
     * @param comparisonAtom The "less than" condition.
     * @return true if the "less than" condition holds on the tuple, false otherwise.
     */
    private boolean evaluateConditionLT(Tuple tuple, ComparisonAtom comparisonAtom) {
        Term lhs = comparisonAtom.getTerm1();
        Term rhs = comparisonAtom.getTerm2();

        // For comparisons of constants, i.e. 1 < 2, or "abc" < "abd".

        if (lhs instanceof Constant && rhs instanceof Constant) {
            if (lhs instanceof IntegerConstant) {
                // We have a comparison of integers, i.e. 1 < 2.
                return ((IntegerConstant) lhs).getValue() < ((IntegerConstant) rhs).getValue();
            }

            else {
                // We have a comparison of strings, i.e. "abc" < "abd".
                return ((StringConstant) lhs).getValue().compareTo(((StringConstant) rhs).getValue()) < 0;
            }

        }

        // For comparisons of variables, i.e. x < y.
        else if (lhs instanceof Variable && rhs instanceof Variable) {
            int lhsPos = tuple.getPosOfVar((Variable) lhs);
            int rhsPos = tuple.getPosOfVar((Variable) rhs);

            Constant[] tupleValues = tuple.getValues();

            // Integer comparison (We assume atoms like 5 < "abc" won't arise).
            if (tupleValues[lhsPos] instanceof IntegerConstant){
                return (((IntegerConstant) tupleValues[lhsPos]).getValue()) < (((IntegerConstant) tupleValues[rhsPos]).getValue());
            }
            // String comparison
            else {
                return ((StringConstant) tupleValues[lhsPos]).getValue().compareTo(((StringConstant) tupleValues[rhsPos]).getValue()) < 0;
            }
        }

        // If we reach here, we are in the case where one of the terms is a variable and one of the terms is a constant.
        else {
            // LHS is the variable, i.e. x < 2.
            if (lhs instanceof Variable) {
                int varPos = tuple.getPosOfVar((Variable) lhs);

                Constant[] tupleValues = tuple.getValues();

                if (rhs instanceof IntegerConstant) {
                    // Variable as LHS. Integer as RHS.
                    return ((IntegerConstant) tupleValues[varPos]).getValue() < ((IntegerConstant) rhs).getValue();
                }
                else {
                    // Variable as LHS. String as RHS.
                    return (((StringConstant) tupleValues[varPos]).getValue().compareTo(((StringConstant) rhs).getValue())) < 0;
                }
            }
            else {
                // RHS is the constant, i.e. 2 < a. This is equivalent to a > 2.
                return evaluateConditionGT(tuple, new ComparisonAtom(rhs, lhs, ComparisonOperator.GT));
            }
        }

    }

    /**
     * Helper method for evaluateOnTuple.
     * This method is used if one of the conditions contains the "greater than" operator.
     * Or if the condition contains an operator that can be simplified to use a combination of operators including "greater than".
     * This method evaluates the tuple based on the one comparison atom.
     * @param tuple The tuple to evaluate the "greater than" condition on.
     * @param comparisonAtom The "greater than" condition.
     * @return true if the "greater than" condition holds on the tuple, false otherwise.
     */
    private boolean evaluateConditionGT(Tuple tuple, ComparisonAtom comparisonAtom) {
        Term lhs = comparisonAtom.getTerm1();
        Term rhs = comparisonAtom.getTerm2();

        // For comparisons of constants, i.e. 1 > 2, or "abc" > "abd".
        if (lhs instanceof Constant && rhs instanceof Constant) {
            // We have a comparison of integers, i.e. 1 > 2.
            if (lhs instanceof IntegerConstant) {
                return ((IntegerConstant) lhs).getValue() > ((IntegerConstant) rhs).getValue();
            }

            else {
                // We have a comparison of strings, i.e. "abc" > "abd".
                return ((StringConstant) lhs).getValue().compareTo(((StringConstant) rhs).getValue()) > 0;
            }

        }

        // For comparisons of variables, i.e. x > y.
        else if (lhs instanceof Variable && rhs instanceof Variable) {
            int lhsPos = tuple.getPosOfVar((Variable) lhs);
            int rhsPos = tuple.getPosOfVar((Variable) rhs);

            Constant[] tupleValues = tuple.getValues();

            // Integer comparison (We assume atoms like 5 > "abc" won't arise).
            if (tupleValues[lhsPos] instanceof IntegerConstant){
                return (((IntegerConstant) tupleValues[lhsPos]).getValue()) > (((IntegerConstant) tupleValues[rhsPos]).getValue());
            }
            // String comparison
            else {
                return ((StringConstant) tupleValues[lhsPos]).getValue().compareTo(((StringConstant) tupleValues[rhsPos]).getValue()) > 0;
            }
        }

        // If we reach here, we are in the case where one of the terms is a variable and one of the terms is a constant.
        else {
            // LHS is the variable, i.e. x > 2.
            if (lhs instanceof Variable) {
                int varPos = tuple.getPosOfVar((Variable) lhs);

                Constant[] tupleValues = tuple.getValues();

                if (rhs instanceof IntegerConstant) {
                    // Variable as LHS. Integer as RHS.
                    return ((IntegerConstant) tupleValues[varPos]).getValue() > ((IntegerConstant) rhs).getValue();
                }
                else {
                    // Variable as LHS. String as RHS.
                    return (((StringConstant) tupleValues[varPos]).getValue().compareTo(((StringConstant) rhs).getValue())) > 0;
                }
            }
            else {
                // RHS is the constant, i.e. 2 > a. This is equivalent to a < 2.
                return evaluateConditionGT(tuple, new ComparisonAtom(rhs, lhs, ComparisonOperator.LT));
            }
        }

    }

    /**
     * Helper method for evaluateOnTuple.
     * This method is used if one of the conditions contains the "equals" operator.
     * Or if the condition contains an operator that can be simplified to use a combination of operators including "equals".
     * This method evaluates the tuple based on the one comparison atom.
     * @param tuple The tuple to evaluate the "equals" condition on.
     * @param comparisonAtom The "equals" condition.
     * @return true if the "equals" condition holds on the tuple, false otherwise.
     */
    private boolean evaluateConditionEQ(Tuple tuple, ComparisonAtom comparisonAtom) {
        Term lhs = comparisonAtom.getTerm1();
        Term rhs = comparisonAtom.getTerm2();

        // For comparisons of constants, i.e. 1 = 2, or "abc" = "abd".
        if (lhs instanceof Constant && rhs instanceof Constant) {
            // We have a comparison of integers, i.e. 1 = 2.
            if (lhs instanceof IntegerConstant) {
                return ((IntegerConstant) lhs).getValue().equals(((IntegerConstant) rhs).getValue());
            }

            else {
                // We have a comparison of strings, i.e. "abc" = "abd".
                return ((StringConstant) lhs).getValue().compareTo(((StringConstant) rhs).getValue()) == 0;
            }

        }

        // For comparisons of variables, i.e. x = y.
        else if (lhs instanceof Variable && rhs instanceof Variable) {
            int lhsPos = tuple.getPosOfVar((Variable) lhs);
            int rhsPos = tuple.getPosOfVar((Variable) rhs);

            Constant[] tupleValues = tuple.getValues();

            // Integer comparison (We assume atoms like 5 = "abc" won't arise).
            if (tupleValues[lhsPos] instanceof IntegerConstant){
                return (((IntegerConstant) tupleValues[lhsPos]).getValue()).equals((((IntegerConstant) tupleValues[rhsPos]).getValue()));
            }
            // String comparison
            else {
                return ((StringConstant) tupleValues[lhsPos]).getValue().compareTo(((StringConstant) tupleValues[rhsPos]).getValue()) == 0;
            }
        }

        // If we reach here, we are in the case where one of the terms is a variable and one of the terms is a constant.
        else {

            Constant[] tupleValues = tuple.getValues();

            if (lhs instanceof Variable) {
                // LHS is the variable, i.e. x = 2 or x = "abc".
                int varPos = tuple.getPosOfVar((Variable) lhs);
                if (rhs instanceof IntegerConstant) {
                    // RHS is an integerConstant, i.e. x = 2.
                    return (((IntegerConstant) tupleValues[varPos]).getValue()).equals(((IntegerConstant) rhs).getValue());
                } else {
                    // RHS is a StringConstant, i.e. x = "abc".
                    return (((StringConstant) tupleValues[varPos]).getValue()).equals(((StringConstant) rhs).getValue());
                }
            }

            else {
                //RHS is the variable, i.e. 2 = x or "abc" = x.
                int varPos = tuple.getPosOfVar((Variable) rhs);
                if (lhs instanceof IntegerConstant) {
                    // LHS is an integerConstant, i.e. 2 = x.
                    return (((IntegerConstant) lhs).getValue()).equals(((IntegerConstant) (tupleValues[varPos])).getValue());
                } else {
                    // LHS is a StringConstant, i.e. "abc" = x.
                    return (((StringConstant) lhs).getValue()).equals(((StringConstant) (tupleValues[varPos])).getValue());
                }
            }
        }

    }

}

