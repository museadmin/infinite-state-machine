package com.github.museadmin.infinite_state_machine.core;

import com.github.museadmin.infinite_state_machine.common.action.IActionPack;
import com.github.museadmin.infinite_state_machine.common.dal.IDataAccessLayer;
import com.github.museadmin.infinite_state_machine.common.dal.Postgres;
import com.github.museadmin.infinite_state_machine.common.dal.Sqlite3;
import com.github.museadmin.infinite_state_machine.core.action_pack.ISMCoreActionPack;
import com.github.museadmin.infinite_state_machine.lib.PropertyCache;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;

/**
 * The primary parent object that contains all of the components
 * of the infinite state machine
 */
public class InfiniteStateMachine {

  private String dbFile;
  private ISMCoreActionPack ismCoreActionPack = new ISMCoreActionPack();
  private String epochSeconds = Long.toString(System.currentTimeMillis());
  private PropertyCache propertyCache = new PropertyCache("config.properties");
  private static final Logger LOGGER = LoggerFactory.getLogger(InfiniteStateMachine.class.getName());

  public String getRdbms() {
    return rdbms;
  }
  private String rdbms;
  private String runRoot;
  private IDataAccessLayer iDataAccessLayer = null;

  /**
   * Default constructor
   */
  public InfiniteStateMachine() {
    rdbms = propertyCache.getProperty("rdbms");
    createRuntimeDirectories();
    createDatabase();

    // One off import for core action pack
    importActionPack(ismCoreActionPack);
  }

  /**
   * Constructor accepts a fully qualified path to an alternative
   * properties file. This enables applications to pass in their own
   * properties.
   * @param propertiesFile
   */
  public InfiniteStateMachine(String propertiesFile){
    propertyCache.importProperties(propertiesFile);
    rdbms = propertyCache.getProperty("rdbms");
    createRuntimeDirectories();
    createDatabase();

    // One off import for core action pack. Other action
    // packs would be imported by applications
    importActionPack(ismCoreActionPack);
  }

  /**
   * The meat and gristle of the state machine. Action packs
   * contain all of the actions necessary to express a specific
   * functionality. e.g. A messaging service.
   * @param ap
   */
  public void importActionPack(IActionPack ap) {
    createTables(ap.getJsonObjectFromResourceFile("tables.json"));
    insertStates(ap.getJsonObjectFromResourceFile("states.json"));
    insertActions(ap.getJsonObjectFromResourceFile("actions.json"));
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
   * Create the default tables in the database used by the state machine core
   * Table definitions are read in from the json files and passed to the DAO
   */
  private void createTables(JSONObject jsonObject) {
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
  private void insertStates(JSONObject jsonObject) {
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
  private void insertActions(JSONObject jsonObject) {
    JSONArray actions = jsonObject.getJSONArray("actions");
    if (actions != null) {
      for (int i = 0; i < actions.length(); i++) {
        iDataAccessLayer.insertAction(actions.getJSONArray(i));
      }
    }
  }

  /**
   * Each run needs its own distinct run directory structure created
   */
  private void createRuntimeDirectories() {
    runRoot = propertyCache.getProperty("runRoot") + File.separator +
      epochSeconds + File.separator;
    File root = new File(runRoot);
    if (! root.isDirectory()) { root.mkdirs(); }
  }

}
