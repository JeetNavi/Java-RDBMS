package ed.inf.adbs.minibase.base;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class JoinOperatorTest {

    @Test
    public void testJoinOneCondition() {

        String databaseDir = "." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db";
        DatabaseCatalog.init(databaseDir);
        DatabaseCatalog catalog = DatabaseCatalog.getCatalogInstance();
        catalog.setVarPos(new Variable("u"), 0);
        catalog.setVarPos(new Variable("v"), 1);
        catalog.setVarPos(new Variable("w"), 2);
        catalog.setVarPos(new Variable("x"), 3);
        catalog.setVarPos(new Variable("y"), 4);
        catalog.setVarPos(new Variable("z"), 5);

        List<Term> leftAtomTerms = new ArrayList<>();
        leftAtomTerms.add(new Variable("u"));
        leftAtomTerms.add(new Variable("v"));
        leftAtomTerms.add(new Variable("w"));
        Operator leftChild = new ScanOperator(new RelationalAtom("R", leftAtomTerms));

        List<Term> rightAtomTerms = new ArrayList<>();
        leftAtomTerms.add(new Variable("x"));
        leftAtomTerms.add(new Variable("y"));
        leftAtomTerms.add(new Variable("z"));
        Operator rightChild = new ScanOperator(new RelationalAtom("S", rightAtomTerms));

        List<ComparisonAtom> conditions = new ArrayList<>();
        conditions.add(new ComparisonAtom(new Variable("u"), new Variable("x"), ComparisonOperator.EQ));
        SelectionCondition joinCond = new SelectionCondition(conditions);

        Operator operator = new JoinOperator(leftChild, rightChild, joinCond);
        operator.dump();

        // Expects 7 output tuples.
    }


    @Test
    public void testJoinTwoCondition() {

        String databaseDir = "." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db";
        DatabaseCatalog.init(databaseDir);
        DatabaseCatalog catalog = DatabaseCatalog.getCatalogInstance();
        catalog.setVarPos(new Variable("u"), 0);
        catalog.setVarPos(new Variable("v"), 1);
        catalog.setVarPos(new Variable("w"), 2);
        catalog.setVarPos(new Variable("x"), 3);
        catalog.setVarPos(new Variable("y"), 4);
        catalog.setVarPos(new Variable("z"), 5);

        List<Term> leftAtomTerms = new ArrayList<>();
        leftAtomTerms.add(new Variable("u"));
        leftAtomTerms.add(new Variable("v"));
        leftAtomTerms.add(new Variable("w"));
        Operator leftChild = new ScanOperator(new RelationalAtom("R", leftAtomTerms));

        List<Term> rightAtomTerms = new ArrayList<>();
        leftAtomTerms.add(new Variable("x"));
        leftAtomTerms.add(new Variable("y"));
        leftAtomTerms.add(new Variable("z"));
        Operator rightChild = new ScanOperator(new RelationalAtom("S", rightAtomTerms));

        List<ComparisonAtom> conditions = new ArrayList<>();
        conditions.add(new ComparisonAtom(new Variable("u"), new Variable("x"), ComparisonOperator.EQ));
        conditions.add(new ComparisonAtom(new Variable("u"), new Variable("z"), ComparisonOperator.EQ));
        SelectionCondition joinCond = new SelectionCondition(conditions);

        Operator operator = new JoinOperator(leftChild, rightChild, joinCond);
        operator.dump();

        // Expects one output tuple Tuple{values=[2, 7, 'anlp', 2, 'anka', 2]}
    }

    @Test
    public void testJoinOneConditionWithProjection() {

        String databaseDir = "." + File.separator + "data" + File.separator + "evaluation" + File.separator + "db";
        DatabaseCatalog.init(databaseDir);
        DatabaseCatalog catalog = DatabaseCatalog.getCatalogInstance();
        catalog.setVarPos(new Variable("u"), 0);
        catalog.setVarPos(new Variable("v"), 1);
        catalog.setVarPos(new Variable("w"), 2);
        catalog.setVarPos(new Variable("x"), 3);
        catalog.setVarPos(new Variable("y"), 4);
        catalog.setVarPos(new Variable("z"), 5);

        List<Term> leftAtomTerms = new ArrayList<>();
        leftAtomTerms.add(new Variable("u"));
        leftAtomTerms.add(new Variable("v"));
        leftAtomTerms.add(new Variable("w"));
        Operator leftChild = new ScanOperator(new RelationalAtom("R", leftAtomTerms));

        List<Term> rightAtomTerms = new ArrayList<>();
        leftAtomTerms.add(new Variable("x"));
        leftAtomTerms.add(new Variable("y"));
        leftAtomTerms.add(new Variable("z"));
        Operator rightChild = new ScanOperator(new RelationalAtom("S", rightAtomTerms));

        List<ComparisonAtom> conditions = new ArrayList<>();
        conditions.add(new ComparisonAtom(new Variable("u"), new Variable("x"), ComparisonOperator.EQ));
        SelectionCondition joinCond = new SelectionCondition(conditions);

        Operator operator = new JoinOperator(leftChild, rightChild, joinCond);

        List<Variable> headVars = new ArrayList<>();
        headVars.add(new Variable("y"));
        headVars.add(new Variable("w"));
        operator = new ProjectOperator(operator, new Head("Q", headVars, null));
        operator.dump();

        // Expects 7 output tuples. 2 cols.
    }

}