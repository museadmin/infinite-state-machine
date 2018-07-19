package com.github.museadmin.infinite_state_machine.core;

import com.github.museadmin.infinite_state_machine.common.action.Action;
import com.github.museadmin.infinite_state_machine.common.action.IActionPack;
import com.github.museadmin.infinite_state_machine.common.dal.Postgres;
import com.github.museadmin.infinite_state_machine.common.dal.Sqlite3;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Bootstrapper contains the methods to get the state machine
 * running with all of its default core actions and dependencies
 */
public class Bootstrap extends RunState {

    /**
     * Each run needs its own distinct run directory structure created
     */
    public void createRuntimeDirectories() {
        runRoot = propertyCache.getProperty("runRoot") + File.separator +
            epochSeconds + File.separator;
        File root = new File(runRoot);
        if (! root.isDirectory()) { root.mkdirs(); }
    }

    /**
     * Create the default tables in the database used by the state machine core
     * Table definitions are read in from the json files and passed to the DAO
     */
    public void createTables(JSONObject jsonObject) {
        JSONArray tables = jsonObject.getJSONArray("tables");
        if (tables != null) {
            for (int i = 0; i < tables.length(); i++) {
                iDataAccessLayer.createTable(tables.getJSONObject(i));
            }
        }
    }

    /**
     * Insert the states provided by an action pack into the state table
     * @param jsonObject A JSONObject that contains the data to be inserted
     */
    public void insertStates(JSONObject jsonObject) {
        JSONArray states = jsonObject.getJSONArray("states");
        if (states != null) {
            for (int i = 0; i < states.length(); i++) {
                iDataAccessLayer.insertState(states.getJSONArray(i));
            }
        }
    }

    /**
     * Insert the actions provided by an action pack into the state_machine table
     * @param jsonObject A JSONObject that contains the data to be inserted
     */
    public void insertActions(JSONObject jsonObject) {
        JSONArray actions = jsonObject.getJSONArray("actions");
        if (actions != null) {
            for (int i = 0; i < actions.length(); i++) {
                iDataAccessLayer.insertAction(actions.getJSONArray(i));
            }
        }
    }

    /**
     * The meat and gristle of the state machine. Action packs
     * contain all of the actions necessary to express a specific
     * functionality. e.g. A messaging service.
     * @param ap
     */
    @SuppressWarnings("unchecked")
    public void importActionPack(IActionPack ap) {
        // First create the runtime tables and populate with states etc.
        createTables(ap.getJsonObjectFromResourceFile("tables.json"));
        insertStates(ap.getJsonObjectFromResourceFile("states.json"));
        insertActions(ap.getJsonObjectFromResourceFile("actions.json"));
        // Import the action classes from the action pack
        ArrayList tmpActions = ismCoreActionPack.getActionsFromActionPack();
        if (tmpActions != null){
            tmpActions.forEach(
                action -> {
                    if(actions.contains(action)) {
                        throw new RuntimeException("Class of type " + action.toString());
                    }
                    actions.add((Action) action);
                }
            );
        }
    }

    /**
     * Create a unique database instance for the run
     */
    public void createDatabase() {
        if (rdbms.equalsIgnoreCase("SQLITE3")) {

            // Create the runtime dir for the sqlite3 db
            String dbPath = runRoot + "control" + File.separator + "database";
            File dir = new File (dbPath);
            if (! dir.isDirectory()) { dir.mkdirs(); }
            dbFile = dbPath + File.separator + "ism.db";

            // Create the database itself
            iDataAccessLayer = new Sqlite3(dbFile);

        } else if (rdbms.equalsIgnoreCase("POSTGRES")) {
            iDataAccessLayer = new Postgres(propertyCache, epochSeconds);
        } else {
            throw new RuntimeException("Failed to identify RDBMS in use from property file");
        }
    }
}
