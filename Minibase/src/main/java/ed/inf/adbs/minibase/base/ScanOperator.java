package ed.inf.adbs.minibase.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * ScanOperator class that is inherited from Operator class.
 * This is the base operator used to read tuples from files.
 */
public class ScanOperator extends Operator{

    // The name of the relation to scan i.e., "R".
    private final String baseRelation;
    // The variables from the relational atom i.e., x,y,z in R(x,y,z).
    private final List<Variable> variables;
    // Scanner used for reading a file line by line (tuple by tuple).
    private Scanner scanner;

    /**
     * Constructor for ScanOperator.
     * Takes the relationalAtom and extracts the relation name and the variables.
     * Then assigns to baseRelation and variables.
     * Also creates a new scanner.
     * @param relationalAtom The relational atom from the body that prompted us to create a scanOperator.
     */
    public ScanOperator(RelationalAtom relationalAtom) {
        this.baseRelation = relationalAtom.getName();

        variables = new ArrayList<>();
        for (Term term : relationalAtom.getTerms()) {
            if (term instanceof Variable) { // This should always be true after rewriting.
                variables.add((Variable) term);
            }
        }

        createScanner();
    }

    /**
     * GetNextTuple method for ScanOperator.
     * Checks if the scanner has a next line/tuple.
     * If so, convert this to a tuple and return. Else return null indicating there is no more tuples.
     * @return Next tuple from file, or null in the case where there is no more tuples.
     */
    @Override
    public Tuple getNextTuple() {
        if (scanner.hasNextLine()){
            String stringTuple = scanner.nextLine();
            return toTuple(stringTuple);
        }
        else {
            return null;
        }
    }

    /**
     * Reset method for ScanOperator.
     * Closes the current scanner and creates a new scanner to point to the start of the file again.
     */
    @Override
    public void reset() {
        scanner.close();
        createScanner();
    }

    /**
     * Method used to create a new scanner to start scanning a file from the start.
     * It takes the relation name and gets the file location from the DatabaseCatalog.
     * The file location is needed when creating a new scanner.
     */
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

    /**
     * Method used to convert a line from the file to a tuple object (String -> Tuple).
     * @param tupleString The line from the file of type String.
     * @return Tuple object which is represents the tupleString.
     */
    private Tuple toTuple(String tupleString) {
        // Split the string (line) by the comma delimiter.
        String[] tupleSplit = tupleString.split(", |,");

        Constant[] tupleValues = new Constant[variables.size()];

        int counter = 0;
        // Here we exploit the fact that there are only two types of constants.
        // StringConstants begin with an apostrophe and IntegerConstants do not.
        // So we actually do not use the schemas here for simplicity.
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