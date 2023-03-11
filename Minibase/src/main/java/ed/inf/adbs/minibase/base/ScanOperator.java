package ed.inf.adbs.minibase.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ScanOperator extends Operator{

    private String baseRelation;
    private List<Variable> variables;
    private Scanner scanner;

    public ScanOperator(RelationalAtom relationalAtom) {
        this.baseRelation = relationalAtom.getName();
        variables = new ArrayList<>();
        createScanner();
        for (Term term : relationalAtom.getTerms()) {
            if (term instanceof Variable) {
                variables.add((Variable) term);
            }
        }
//        // Add variable positions to map.
//        for (int i = 0; i < relationalAtom.getTerms().size(); i++) {
//            DatabaseCatalog.getCatalogInstance().setVarPos((Variable) relationalAtom.getTerms().get(i), i);
//        }
    }

    /**
     * @return
     */
    @Override
    public Tuple getNextTuple() {
        if (scanner.hasNextLine()){
            String stringTuple = scanner.nextLine();
            return toTuple(stringTuple, variables);
        }
        else {
            return null;
        }
    }

    /**
     *
     */
    @Override
    public void reset() {
        scanner.close();
        createScanner();
    }

    private void createScanner() {
        try {
            String fileLocation = DatabaseCatalog.getCatalogInstance().getLocation(baseRelation);
            File relationFile = new File(fileLocation);
            scanner = new Scanner(relationFile);
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }

    }

    private Tuple toTuple(String tupleString, List<Variable> variables) {
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

        return new Tuple(tupleValues, variables);
    }
}