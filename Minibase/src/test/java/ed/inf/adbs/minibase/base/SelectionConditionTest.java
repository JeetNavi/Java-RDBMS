package ed.inf.adbs.minibase.base;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class SelectionConditionTest {

    // 1 Comparison Atom.

    @Test
    public void oneEQZeroShouldReturnFalse() {
        Term lhs = new IntegerConstant(1);
        Term rhs = new IntegerConstant(0);
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.EQ);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();

        SelectionCondition selectionCondition = new SelectionCondition(conditions, varPositions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(0);
        values[2] = new IntegerConstant(0);

        Tuple testTuple = new Tuple(values);

        assertFalse(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void oneLessThanZeroShouldReturnFalse() {
        Term lhs = new IntegerConstant(1);
        Term rhs = new IntegerConstant(0);
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.LT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();

        SelectionCondition selectionCondition = new SelectionCondition(conditions, varPositions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(0);
        values[2] = new IntegerConstant(0);

        Tuple testTuple = new Tuple(values);

        assertFalse(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void oneLessThanTwoShouldReturnTrue() {
        Term lhs = new IntegerConstant(1);
        Term rhs = new IntegerConstant(2);
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.LT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();

        SelectionCondition selectionCondition = new SelectionCondition(conditions, varPositions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(0);
        values[2] = new IntegerConstant(0);

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void oneLessThanOneShouldReturnFalse() {
        Term lhs = new IntegerConstant(1);
        Term rhs = new IntegerConstant(1);
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.LT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();

        SelectionCondition selectionCondition = new SelectionCondition(conditions, varPositions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(0);
        values[2] = new IntegerConstant(0);

        Tuple testTuple = new Tuple(values);

        assertFalse(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void oneLTEZeroShouldReturnFalse() {
        Term lhs = new IntegerConstant(1);
        Term rhs = new IntegerConstant(0);
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.LT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();

        SelectionCondition selectionCondition = new SelectionCondition(conditions, varPositions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(0);
        values[2] = new IntegerConstant(0);

        Tuple testTuple = new Tuple(values);

        assertFalse(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void lessThanWithVarOnLHS() {
        Variable lhs = new Variable("x");
        Term rhs = new IntegerConstant(6);
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.LT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(lhs, 1);

        SelectionCondition selectionCondition = new SelectionCondition(conditions, varPositions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(8);
        values[2] = new IntegerConstant(5);

        Tuple testTuple = new Tuple(values);

        assertFalse(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void lessThanWithVarOnRHS() {
        Variable rhs = new Variable("x");
        Term lhs = new IntegerConstant(6);
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.LT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(rhs, 1);

        SelectionCondition selectionCondition = new SelectionCondition(conditions, varPositions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(8);
        values[2] = new IntegerConstant(5);

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void LEQWithVarOnRHS() {
        Variable rhs = new Variable("x");
        Term lhs = new IntegerConstant(6);
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.LEQ);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(rhs, 1);

        SelectionCondition selectionCondition = new SelectionCondition(conditions, varPositions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(6);
        values[2] = new IntegerConstant(5);

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void LEQWithVarOnLHS() {
        Variable lhs = new Variable("x");
        Term rhs = new IntegerConstant(6);
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.LEQ);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(lhs, 1);

        SelectionCondition selectionCondition = new SelectionCondition(conditions, varPositions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(6);
        values[2] = new IntegerConstant(5);

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void StringEQWithVarOnLHS() {
        Variable lhs = new Variable("x");
        Term rhs = new StringConstant("test");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.EQ);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(lhs, 1);

        SelectionCondition selectionCondition = new SelectionCondition(conditions, varPositions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new StringConstant("test");
        values[2] = new IntegerConstant(5);

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void StringEQWithVarOnRHS() {
        Variable rhs = new Variable("x");
        Term lhs = new StringConstant("test");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.EQ);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(rhs, 1);

        SelectionCondition selectionCondition = new SelectionCondition(conditions, varPositions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new StringConstant("test");
        values[2] = new IntegerConstant(5);

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void StringCompEQ() {
        Term lhs = new StringConstant("hi");
        Term rhs = new StringConstant("hii");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.NEQ);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();

        SelectionCondition selectionCondition = new SelectionCondition(conditions, varPositions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(0);
        values[2] = new IntegerConstant(0);

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void StringCompGT() {
        Term lhs = new StringConstant("hii");
        Term rhs = new StringConstant("hii");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.GT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();

        SelectionCondition selectionCondition = new SelectionCondition(conditions, varPositions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(0);
        values[2] = new IntegerConstant(0);

        Tuple testTuple = new Tuple(values);

        assertFalse(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void GEQVarOnLHSString() {
        Variable lhs = new Variable("x");
        Term rhs = new StringConstant("test");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.GEQ);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(lhs, 1);

        SelectionCondition selectionCondition = new SelectionCondition(conditions, varPositions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new StringConstant("tesu");
        values[2] = new IntegerConstant(5);

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void LEQVarOnLHSString() {
        Variable lhs = new Variable("x");
        Term rhs = new StringConstant("tesu");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.LEQ);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(lhs, 1);

        SelectionCondition selectionCondition = new SelectionCondition(conditions, varPositions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new StringConstant("test");
        values[2] = new IntegerConstant(5);

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void StringCompLT() {
        Term lhs = new StringConstant("hii");
        Term rhs = new StringConstant("hii");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.LT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();

        SelectionCondition selectionCondition = new SelectionCondition(conditions, varPositions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(0);
        values[2] = new IntegerConstant(0);

        Tuple testTuple = new Tuple(values);

        assertFalse(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void VarCompLTInt() {
        Variable lhs = new Variable("x");
        Variable rhs = new Variable("y");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.LT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(lhs, 1);
        varPositions.put(rhs, 2);

        SelectionCondition selectionCondition = new SelectionCondition(conditions, varPositions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(7);
        values[2] = new IntegerConstant(6);

        Tuple testTuple = new Tuple(values);

        assertFalse(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void VarCompLTString() {
        Variable lhs = new Variable("x");
        Variable rhs = new Variable("y");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.LT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(lhs, 1);
        varPositions.put(rhs, 2);

        SelectionCondition selectionCondition = new SelectionCondition(conditions, varPositions);

        Constant[] values = new Constant[3];
        values[0] = new StringConstant("0");
        values[1] = new StringConstant("less");
        values[2] = new StringConstant("lest");

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void GTints() {
        Term lhs = new IntegerConstant(3);
        Term rhs = new IntegerConstant(2);
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.GT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();

        SelectionCondition selectionCondition = new SelectionCondition(conditions, varPositions);

        Constant[] values = new Constant[3];
        values[0] = new StringConstant("0");
        values[1] = new StringConstant("less");
        values[2] = new StringConstant("lest");

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void VarCompGTInt() {
        Variable lhs = new Variable("x");
        Variable rhs = new Variable("y");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.GT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(lhs, 1);
        varPositions.put(rhs, 2);

        SelectionCondition selectionCondition = new SelectionCondition(conditions, varPositions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(7);
        values[2] = new IntegerConstant(6);

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void VarCompGTString() {
        Variable lhs = new Variable("x");
        Variable rhs = new Variable("y");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.GT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(lhs, 1);
        varPositions.put(rhs, 2);

        SelectionCondition selectionCondition = new SelectionCondition(conditions, varPositions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new StringConstant("7");
        values[2] = new StringConstant("6");

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void VarOnRHSIntOnLHSGT() {
        Variable rhs = new Variable("x");
        Term lhs = new IntegerConstant(2);
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.GT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();

        varPositions.put(rhs, 2);

        SelectionCondition selectionCondition = new SelectionCondition(conditions, varPositions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new StringConstant("7");
        values[2] = new IntegerConstant(3);

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void VarsEQINT() {
        Variable lhs = new Variable("x");
        Variable rhs = new Variable("y");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.EQ);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(lhs, 1);
        varPositions.put(rhs, 2);

        SelectionCondition selectionCondition = new SelectionCondition(conditions, varPositions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(0);
        values[2] = new IntegerConstant(0);

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void VarsEQString() {
        Variable lhs = new Variable("x");
        Variable rhs = new Variable("y");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.EQ);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(lhs, 1);
        varPositions.put(rhs, 2);

        SelectionCondition selectionCondition = new SelectionCondition(conditions, varPositions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new StringConstant("0");
        values[2] = new StringConstant("0");

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    // Adding more conditions.

    @Test
    public void twoConditionT1() {
        Variable lhs = new Variable("x");
        Variable rhs = new Variable("y");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.GEQ);
        List<ComparisonAtom> conditions = new ArrayList<>();
        conditions.add(comparisonAtom);

        Variable lhs1 = new Variable("z");
        Term rhs1 = new IntegerConstant(4);
        ComparisonAtom comparisonAtom1 = new ComparisonAtom(lhs1, rhs1, ComparisonOperator.LEQ);
        conditions.add(comparisonAtom1);

        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(lhs, 0);
        varPositions.put(rhs, 1);
        varPositions.put(lhs1, 2);

        SelectionCondition selectionCondition = new SelectionCondition(conditions, varPositions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(3);
        values[1] = new IntegerConstant(2);
        values[2] = new IntegerConstant(4);

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

}