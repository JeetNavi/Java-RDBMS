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

    private static HashMap<Variable, Integer> varPositions;
    private static HashMap<String, HashMap<Variable, Integer>> localVarPositions;
    private static HashMap<Variable, String> varRelations;

    private DatabaseCatalog(String db) {

        HashMap<String, String> relationLocations = new HashMap<>();
        HashMap<String, String[]> relationSchemas = new HashMap<>();

        varPositions = new HashMap<>();
        localVarPositions = new HashMap<>();
        varRelations = new HashMap<>();

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

    public Integer getVarPos(Variable variable) {
        return varPositions.get(variable);
    }

    public void setVarPos(Variable variable, Integer position) {

        if (!(varPositions.containsKey(variable))) {
            varPositions.put(variable, position);
        }
    }

    public HashMap<Variable, Integer> getVarPositions() {return varPositions;}

    public HashMap<String, HashMap<Variable, Integer>> getLocalVarPositions() {
        return localVarPositions;
    }

    public void setVarRelation(Variable variable, String relation) {

        varRelations.put(variable, relation);
    }

    public String getVarRelation(Variable variable) {
        return varRelations.get(variable);
    }

    public HashMap<Variable, String> getVarRelations() {
        return varRelations;
    }

    public void setVarRelationIfNotExists(Variable variable, String relation) {
        varRelations.putIfAbsent(variable, relation);
    }

    public Integer getLocalVarPos(String relation, Variable variable) {
        return localVarPositions.get(relation).get(variable);
    }

    public void setLocalVarPos(String relation, Variable variable, Integer position) {

        if (!localVarPositions.containsKey(relation)) {
            HashMap<Variable, Integer> newMap = new HashMap<>();
            newMap.put(variable, position);
            localVarPositions.put(relation, newMap);
        }
        else {
            localVarPositions.get(relation).put(variable, position);
        }
    }
}