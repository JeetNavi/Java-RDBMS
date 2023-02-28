package ed.inf.adbs.minibase.base;

import java.util.HashMap;
import java.util.List;

public class SelectionCondition {

    private List<ComparisonAtom> conditions;

    private DatabaseCatalog catalog = DatabaseCatalog.getCatalogInstance();

    public SelectionCondition(List<ComparisonAtom> conditions) {
        this.conditions = conditions;
    }

    public boolean evaluateOnTuple(Tuple tuple) {

        if (tuple == null) {
            return false;
        }

        boolean conditionHolds = false;

        for (ComparisonAtom comparisonAtom : conditions) {

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
            int lhsPos = catalog.getVarPos((Variable) lhs);
            int rhsPos = catalog.getVarPos((Variable) rhs);

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
                int varPos = catalog.getVarPos((Variable) lhs);

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
            int lhsPos = catalog.getVarPos((Variable) lhs);
            int rhsPos = catalog.getVarPos((Variable) rhs);

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
                int varPos = catalog.getVarPos((Variable) lhs);

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
            int lhsPos = catalog.getVarPos((Variable) lhs);
            int rhsPos = catalog.getVarPos((Variable) rhs);

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
                int varPos = catalog.getLocalVarPos(catalog.getVarRelation((Variable) lhs), (Variable) lhs);
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
                int varPos = catalog.getLocalVarPos(catalog.getVarRelation((Variable) rhs), (Variable) rhs);
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

        public void addCondition(ComparisonAtom condition) {
            conditions.add(condition);
        }



    }


