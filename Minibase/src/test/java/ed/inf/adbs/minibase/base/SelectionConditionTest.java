package ed.inf.adbs.minibase.base;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class SelectionConditionTest {


    // 1 Comparison Atom.

    //DatabaseCatalog catalog = DatabaseCatalog.init(".\data\evaluation\db");
//    DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");

    @Test
    public void oneEQZeroShouldReturnFalse() {
        DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");
        Term lhs = new IntegerConstant(1);
        Term rhs = new IntegerConstant(0);
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.EQ);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();

        SelectionCondition selectionCondition = new SelectionCondition(conditions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(0);
        values[2] = new IntegerConstant(0);

        Tuple testTuple = new Tuple(values);

        assertFalse(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void oneLessThanZeroShouldReturnFalse() {
        DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");
        Term lhs = new IntegerConstant(1);
        Term rhs = new IntegerConstant(0);
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.LT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();

        SelectionCondition selectionCondition = new SelectionCondition(conditions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(0);
        values[2] = new IntegerConstant(0);

        Tuple testTuple = new Tuple(values);

        assertFalse(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void oneLessThanTwoShouldReturnTrue() {
        DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");
        Term lhs = new IntegerConstant(1);
        Term rhs = new IntegerConstant(2);
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.LT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();

        SelectionCondition selectionCondition = new SelectionCondition(conditions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(0);
        values[2] = new IntegerConstant(0);

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void oneLessThanOneShouldReturnFalse() {
        DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");
        Term lhs = new IntegerConstant(1);
        Term rhs = new IntegerConstant(1);
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.LT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();

        SelectionCondition selectionCondition = new SelectionCondition(conditions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(0);
        values[2] = new IntegerConstant(0);

        Tuple testTuple = new Tuple(values);

        assertFalse(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void oneLTEZeroShouldReturnFalse() {
        DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");
        Term lhs = new IntegerConstant(1);
        Term rhs = new IntegerConstant(0);
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.LT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();

        SelectionCondition selectionCondition = new SelectionCondition(conditions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(0);
        values[2] = new IntegerConstant(0);

        Tuple testTuple = new Tuple(values);

        assertFalse(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void lessThanWithVarOnLHS() {
        DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");
        Variable lhs = new Variable("x");
        Term rhs = new IntegerConstant(6);
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.LT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(lhs, 1);

        SelectionCondition selectionCondition = new SelectionCondition(conditions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(8);
        values[2] = new IntegerConstant(5);

        Tuple testTuple = new Tuple(values);

        assertFalse(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void lessThanWithVarOnRHS() {
        DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");
        Variable rhs = new Variable("x");
        Term lhs = new IntegerConstant(6);
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.LT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(rhs, 1);

        SelectionCondition selectionCondition = new SelectionCondition(conditions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(8);
        values[2] = new IntegerConstant(5);

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void LEQWithVarOnRHS() {
        DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");
        Variable rhs = new Variable("x");
        Term lhs = new IntegerConstant(6);
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.LEQ);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(rhs, 1);

        SelectionCondition selectionCondition = new SelectionCondition(conditions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(6);
        values[2] = new IntegerConstant(5);

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void LEQWithVarOnLHS() {
        DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");
        Variable lhs = new Variable("x");
        Term rhs = new IntegerConstant(6);
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.LEQ);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(lhs, 1);

        SelectionCondition selectionCondition = new SelectionCondition(conditions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(6);
        values[2] = new IntegerConstant(5);

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void StringEQWithVarOnLHS() {
        DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");
        Variable lhs = new Variable("x");
        Term rhs = new StringConstant("test");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.EQ);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(lhs, 1);

        SelectionCondition selectionCondition = new SelectionCondition(conditions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new StringConstant("test");
        values[2] = new IntegerConstant(5);

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void StringEQWithVarOnRHS() {
        DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");
        Variable rhs = new Variable("x");
        Term lhs = new StringConstant("test");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.EQ);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(rhs, 1);

        SelectionCondition selectionCondition = new SelectionCondition(conditions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new StringConstant("test");
        values[2] = new IntegerConstant(5);

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void StringCompEQ() {
        DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");
        Term lhs = new StringConstant("hi");
        Term rhs = new StringConstant("hii");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.NEQ);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();

        SelectionCondition selectionCondition = new SelectionCondition(conditions);

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
        DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.GT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();

        SelectionCondition selectionCondition = new SelectionCondition(conditions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(0);
        values[2] = new IntegerConstant(0);

        Tuple testTuple = new Tuple(values);

        assertFalse(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void GEQVarOnLHSString() {
        DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");
        Variable lhs = new Variable("x");
        Term rhs = new StringConstant("test");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.GEQ);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(lhs, 1);

        SelectionCondition selectionCondition = new SelectionCondition(conditions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new StringConstant("tesu");
        values[2] = new IntegerConstant(5);

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void LEQVarOnLHSString() {
        DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");
        Variable lhs = new Variable("x");
        Term rhs = new StringConstant("tesu");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.LEQ);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(lhs, 1);

        SelectionCondition selectionCondition = new SelectionCondition(conditions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new StringConstant("test");
        values[2] = new IntegerConstant(5);

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void StringCompLT() {
        DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");
        Term lhs = new StringConstant("hii");
        Term rhs = new StringConstant("hii");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.LT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();

        SelectionCondition selectionCondition = new SelectionCondition(conditions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(0);
        values[2] = new IntegerConstant(0);

        Tuple testTuple = new Tuple(values);

        assertFalse(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void VarCompLTInt() {
        DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");
        Variable lhs = new Variable("x");
        Variable rhs = new Variable("y");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.LT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(lhs, 1);
        varPositions.put(rhs, 2);

        SelectionCondition selectionCondition = new SelectionCondition(conditions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(7);
        values[2] = new IntegerConstant(6);

        Tuple testTuple = new Tuple(values);

        assertFalse(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void VarCompLTString() {
        DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");
        Variable lhs = new Variable("x");
        Variable rhs = new Variable("y");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.LT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(lhs, 1);
        varPositions.put(rhs, 2);

        SelectionCondition selectionCondition = new SelectionCondition(conditions);

        Constant[] values = new Constant[3];
        values[0] = new StringConstant("0");
        values[1] = new StringConstant("less");
        values[2] = new StringConstant("lest");

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void GTints() {
        DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");
        Term lhs = new IntegerConstant(3);
        Term rhs = new IntegerConstant(2);
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.GT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();

        SelectionCondition selectionCondition = new SelectionCondition(conditions);

        Constant[] values = new Constant[3];
        values[0] = new StringConstant("0");
        values[1] = new StringConstant("less");
        values[2] = new StringConstant("lest");

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void VarCompGTInt() {
        DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");
        Variable lhs = new Variable("x");
        Variable rhs = new Variable("y");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.GT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(lhs, 1);
        varPositions.put(rhs, 2);

        SelectionCondition selectionCondition = new SelectionCondition(conditions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(7);
        values[2] = new IntegerConstant(6);

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void VarCompGTString() {
        DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");
        Variable lhs = new Variable("x");
        Variable rhs = new Variable("y");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.GT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(lhs, 1);
        varPositions.put(rhs, 2);

        SelectionCondition selectionCondition = new SelectionCondition(conditions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new StringConstant("7");
        values[2] = new StringConstant("6");

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void VarOnRHSIntOnLHSGT() {
        DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");
        Variable rhs = new Variable("x");
        Term lhs = new IntegerConstant(2);
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.GT);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();

        varPositions.put(rhs, 2);

        SelectionCondition selectionCondition = new SelectionCondition(conditions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new StringConstant("7");
        values[2] = new IntegerConstant(3);

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void VarsEQINT() {
        DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");
        Variable lhs = new Variable("x");
        Variable rhs = new Variable("y");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.EQ);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(lhs, 1);
        varPositions.put(rhs, 2);

        SelectionCondition selectionCondition = new SelectionCondition(conditions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(0);
        values[1] = new IntegerConstant(0);
        values[2] = new IntegerConstant(0);

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

    @Test
    public void VarsEQString() {
        DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");
        Variable lhs = new Variable("x");
        Variable rhs = new Variable("y");
        ComparisonAtom comparisonAtom = new ComparisonAtom(lhs, rhs, ComparisonOperator.EQ);
        List<ComparisonAtom> conditions = Collections.singletonList(comparisonAtom);
        HashMap<Variable, Integer> varPositions = new HashMap<>();
        varPositions.put(lhs, 1);
        varPositions.put(rhs, 2);

        SelectionCondition selectionCondition = new SelectionCondition(conditions);

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
        DatabaseCatalog catalog = DatabaseCatalog.init("." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db");
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

        SelectionCondition selectionCondition = new SelectionCondition(conditions);

        Constant[] values = new Constant[3];
        values[0] = new IntegerConstant(3);
        values[1] = new IntegerConstant(2);
        values[2] = new IntegerConstant(4);

        Tuple testTuple = new Tuple(values);

        assertTrue(selectionCondition.evaluateOnTuple(testTuple));
    }

}