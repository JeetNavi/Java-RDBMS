package ed.inf.adbs.minibase.base;

import java.util.List;

public class SelectionCondition {

    private List<ComparisonAtom> conditions;

    public SelectionCondition(List<ComparisonAtom> conditions) {
        this.conditions = conditions;
    }

    public boolean evaluateOnTuple(Tuple tuple) {

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
                    if (conditionHolds) {
                        conditionHolds = evaluateConditionGT(tuple, comparisonAtom);
                    }
                    break;
                case LEQ:
                    conditionHolds = evaluateConditionEQ(tuple, comparisonAtom);
                    if (conditionHolds) {
                        conditionHolds = evaluateConditionLT(tuple, comparisonAtom);
                    }
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
        return true;
    }

    private boolean evaluateConditionGT(Tuple tuple, ComparisonAtom comparisonAtom) {
        return true;
    }

    private boolean evaluateConditionEQ(Tuple tuple, ComparisonAtom comparisonAtom) {
        return true;
    }

}
