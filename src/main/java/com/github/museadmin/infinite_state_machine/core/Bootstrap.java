package com.github.museadmin.infinite_state_machine.core;

import com.github.museadmin.infinite_state_machine.common.action.IAction;
import com.github.museadmin.infinite_state_machine.common.action.IActionPack;
import com.github.museadmin.infinite_state_machine.common.dal.DataAccessLayer;
import com.github.museadmin.infinite_state_machine.common.utils.JsonToSqlEtl;
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
   * Create a unique database instance for the run if applicable
   */
  public void createDatabase() {
    dataAccessLayer = new DataAccessLayer(propertyCache);
  }

  /**
   * Each run needs its own distinct run directory structure created
   */
  public void createRuntimeDirectories() {

    // Create the root directory for this run
    String runRoot = propertyCache.getProperty("runRoot") + File.separator +
        epochSeconds;
    File root = new File(runRoot);
    if (! root.isDirectory()) { root.mkdirs(); }

    propertyCache.setProperty("runRoot", runRoot);
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

  public String getRdbms() {
    return propertyCache.getProperty("rdbms");
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
    ArrayList tmpActions = ap.getActionsFromActionPack(
      dataAccessLayer,
      propertyCache.getProperty("runRoot")
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
    createTables(
      ap.getJsonObjectFromResourceFile(
        ap.getClass().getSimpleName() + "_tables.json"
      )
    );
    // Parse the table data from the json definitions
    statements = JsonToSqlEtl.parseSqlFromJson(
      ap.getJsonObjectFromResourceFile(
      ap.getClass().getSimpleName() + "_pack_data.json"
      )
    );
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
