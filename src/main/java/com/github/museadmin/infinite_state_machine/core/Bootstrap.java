package com.github.museadmin.infinite_state_machine.core;

import com.github.museadmin.infinite_state_machine.data.access.action.IAction;
import com.github.museadmin.infinite_state_machine.data.access.action.IActionPack;
import com.github.museadmin.infinite_state_machine.data.access.dal.DataAccessLayer;
import com.github.museadmin.infinite_state_machine.data.access.utils.JsonToSqlEtl;
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
   * Create a unique database instance for the run
   */
  public void createDatabase() {
    dataAccessLayer = new DataAccessLayer(propertyCache, runRoot,epochSeconds);
  }

  /**
   * Each run needs its own distinct run directory structure created
   */
  public void createRuntimeDirectories() {

    // Create the root directory for this run
    runRoot = propertyCache.getProperty("runRoot") + File.separator +
        epochSeconds + File.separator;
    File root = new File(runRoot);
    if (! root.isDirectory()) { root.mkdirs(); }
  }

  /**
   * Create the default tables in the database used by the state machine core
   * Table definitions are read in from the json files and passed to the DAO
   * @param jsonObject Object holds table metadata
   */
  public void createTables(JSONObject jsonObject) {
      JSONArray tables = jsonObject.getJSONArray("tables");
      if (tables != null) {
          for (int i = 0; i < tables.length(); i++) {
              dataAccessLayer.createTable(tables.getJSONObject(i));
          }
      }
  }

  /**
   * The meat and gristle of the state machine. Action packs
   * contain all of the actions necessary to express a specific
   * functionality. e.g. A messaging service.
   * @param ap The action pack being imported
   */
  @SuppressWarnings("unchecked")
  public void importActionPack(IActionPack ap) {

    // Pick up any data / metadata that the action pack wants
    // in the database
    populateDatabase(ap);

    // Import the action classes from the action pack
    ArrayList tmpActions = ismCoreActionPack.getActionsFromActionPack(
      dataAccessLayer,
      runRoot
    );

    if (tmpActions != null && tmpActions.size() > 0){
      tmpActions.forEach(
        action -> {
          if(actions.contains(action)) {
            throw new RuntimeException("Class of type " +
              action.toString() +
            "is already loaded");
          }
          actions.add((IAction) action);
        }
      );

    } else {
      System.out.println("NO ACTIONS IMPORTED");
    }
  }

  /**
   * Extract Transform and Load the SQL statements from an ActionPack pack_data.json file
   * @param ap The action pack
   */
  private void populateDatabase(IActionPack ap) {
    // First create the runtime tables
    createTables(ap.getJsonObjectFromResourceFile("tables.json"));
    // Parse the table data from the json definitions
    statements = JsonToSqlEtl.parseSqlFromJson(ap.getJsonObjectFromResourceFile("pack_data.json"));
    // Use the statements from the ActionPack to populate the tables
    statements.forEach(statement -> dataAccessLayer.executeSqlStatement(statement));
  }

  /**
   * Return the active run phase
   * @return The name of the active run phase
   */
  public String queryRunPhase() {
    return dataAccessLayer.queryRunPhase();
  }

}
