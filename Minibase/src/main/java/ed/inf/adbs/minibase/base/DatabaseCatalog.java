package ed.inf.adbs.minibase.base;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class DatabaseCatalog {

    // Volatile ensures multiple thread will be able to handle the DatabaseCatalog instance correctly.
    private static volatile DatabaseCatalog catalog;

    private final HashMap<String, String> relationLocations;
    private final HashMap<String, String[]> relationSchemas;

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

    public static DatabaseCatalog getCatalogInstance() {

        if (catalog == null) {
            throw new AssertionError("A DatabaseCatalog instance has not been initialized.");
        }

        return catalog;
    }

    public synchronized static DatabaseCatalog init(String db) {
        if (catalog != null) {
            throw new AssertionError("A DatabaseCatalog instance has already been initialized.");
        }

        catalog = new DatabaseCatalog(db);
        return catalog;
    }

    // The below methods assumes init has been called.

    public HashMap<String, String> getRelationLocations() {
        return relationLocations;
    }

    public HashMap<String, String[]> getRelationSchemas() {
        return relationSchemas;
    }

    public String getLocation(String relation) {
        return relationLocations.get(relation);
    }

    public String[] getSchema(String relation) {
        return relationSchemas.get(relation);
    }
}