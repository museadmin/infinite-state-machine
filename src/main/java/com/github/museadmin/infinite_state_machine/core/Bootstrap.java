package com.github.museadmin.infinite_state_machine.core;

import com.github.museadmin.infinite_state_machine.common.action.Action;
import com.github.museadmin.infinite_state_machine.common.action.IActionPack;
import com.github.museadmin.infinite_state_machine.common.dal.Postgres;
import com.github.museadmin.infinite_state_machine.common.dal.Sqlite3;
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
   * The meat and gristle of the state machine. Action packs
   * contain all of the actions necessary to express a specific
   * functionality. e.g. A messaging service.
   * @param ap
   */
  @SuppressWarnings("unchecked")
  public void importActionPack(IActionPack ap) {

    createDatabase();
    populateDatabase(ap);

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
  private void createDatabase() {
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

  /**
   * Extract Transform and Load the SQL statements from an ActionPack pack_data.json file
   * @param ap The action pack
   */
  private void populateDatabase(IActionPack ap) {
    // First create the runtime tables
    createTables(ap.getJsonObjectFromResourceFile("tables.json"));
    // Parse the table data from the json definitions
    statements = JsonToSqlEtl.parseSqlFromFile(ap.getJsonObjectFromResourceFile("pack_data.json"));
    // Use the statements from the ActionPack to populate the tables
    statements.forEach(statement -> iDataAccessLayer.executeSqlStatement(statement));
  }

}
