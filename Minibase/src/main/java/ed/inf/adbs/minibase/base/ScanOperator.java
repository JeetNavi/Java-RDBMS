package ed.inf.adbs.minibase.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ScanOperator extends Operator{

    private String baseRelation;
    private Scanner scanner;

    public ScanOperator(String baseRelation) {
        this.baseRelation = baseRelation;
        createScanner();
    }

    /**
     * @return
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
}
