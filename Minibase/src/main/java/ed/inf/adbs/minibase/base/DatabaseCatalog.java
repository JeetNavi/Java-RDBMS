package ed.inf.adbs.minibase.base;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

/**
 * A DatabaseCatalog object is used to store information about the database, such as schema
 * information or file locations.
 * This is a singleton class as there will only be one set of information about the database.
 */
public class DatabaseCatalog {

    // Volatile ensures multiple thread will be able to handle the DatabaseCatalog instance correctly.
    private static volatile DatabaseCatalog catalog;

    // Hashmap mapping relation names (i.e. R) to a file path location.
    private final HashMap<String, String> relationLocations;
    // Hashmap mapping relation names to its schema.
    private final HashMap<String, String[]> relationSchemas;

    /**
     * Constructor.
     * Since this is a singleton class, this will get called at most once during execution.
     * The constructor assigns relationLocations and relationSchemas their values.
     * @param db The path to the database containing relevant information.
     */
    private DatabaseCatalog(String db) {

        HashMap<String, String> relationLocations = new HashMap<>();
        HashMap<String, String[]> relationSchemas = new HashMap<>();

        try {
            List<String> allSchemas = Files.readAllLines(Paths.get(db + File.separator + "schema.txt"));

            for (String schema : allSchemas) {
                // Separates relation name and the schema.
                String[] splittedSchema = schema.split(" ", 2);

                String relationName = splittedSchema[0];
                String[] relationSchema = splittedSchema[1].split(" ");

                relationLocations.put(relationName, db + File.separator + "files" + File.separator + relationName + ".csv");
                relationSchemas.put(relationName, relationSchema);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        this.relationLocations = relationLocations;
        this.relationSchemas = relationSchemas;
    }

    /**
     * GetInstance method of singleton class.
     * If a DatabaseCatalog instance has not been initialized yet, this will throw an assertion error.
     * @return The DatabaseCatalog instance (assuming it has been initialized).
     */
    public static DatabaseCatalog getCatalogInstance() {

        if (catalog == null) {
            throw new AssertionError("A DatabaseCatalog instance has not been initialized.");
        }

        return catalog;
    }

    /**
     * Initializer for the DatabaseCatalog instance.
     * If a DatabaseCatalog instance is already initialized then this will throw an assertion error.
     * Calls the constructor to initialize.
     * @param db The path to the database containing relevant information.
     * @return newly created DatabaseCatalog instance (assuming it has not already been initialized).
     */
    public synchronized static DatabaseCatalog init(String db) {
        if (catalog != null) {
            throw new AssertionError("A DatabaseCatalog instance has already been initialized.");
        }

        catalog = new DatabaseCatalog(db);
        return catalog;
    }

    /**
     * This method assumes init has been called previously.
     * Used to get the file path location of a relation.
     * @param relation the relation of which the file path is requested, i.e. R.
     * @return File path location of relation, as a string.
     */
    public String getLocation(String relation) {
        return relationLocations.get(relation);
    }

}