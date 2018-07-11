package com.github.museadmin.infinite_state_machine.core;

import com.github.museadmin.infinite_state_machine.common.dal.IDataAccessLayer;
import com.github.museadmin.infinite_state_machine.common.dal.Postgres;
import com.github.museadmin.infinite_state_machine.common.dal.Sqlite3;
import com.github.museadmin.infinite_state_machine.lib.PropertyCache;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * The primary parent object that contains all of the components
 * of the infinite state machine
 */
public class InfiniteStateMachine {

  private String dbFile;
  private String epochSeconds = Long.toString(System.currentTimeMillis());
  private PropertyCache propertyCache = new PropertyCache("environment.properties");
  private static final Logger LOGGER = LoggerFactory.getLogger(InfiniteStateMachine.class.getName());

  public String getRdbms() {
    return rdbms;
  }

  private String rdbms;
  private String runRoot;
  private IDataAccessLayer iDataAccessLayer = null;

  public InfiniteStateMachine() {
    rdbms = propertyCache.getProperty("rdbms");
    createRuntimeDirectories();
    createDatabase();
    createDefaultTables();
  }

  public InfiniteStateMachine(String propertiesFile){
    propertyCache.importProperties(propertiesFile);
    rdbms = propertyCache.getProperty("rdbms");
    createRuntimeDirectories();
    createDatabase();
    createDefaultTables();
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
  private void createDefaultTables() {

    String[] files = { "tblState.json", "tblStateMachine.json", "tblProperties.json", "tblDependencies.json" };
    for (String file : files) {
      iDataAccessLayer.createTable(getTableDefinitionFromResource(file));
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

  /**
   * Returns a JSONObject populated from a JSON file in the resources directory
   * @param jsonFileName String representing the unqualified file name
   * @return JSONObject
   */
  private JSONObject getTableDefinitionFromResource(String jsonFileName) {

    InputStream is = ClassLoader.getSystemResourceAsStream(jsonFileName);
    InputStreamReader isr;
    BufferedReader br;
    StringBuilder sb = new StringBuilder();
    String content;
    try {
      isr = new InputStreamReader(is);
      br = new BufferedReader(isr);
      while ((content = br.readLine()) != null) {
          sb.append(content);
      }
      isr.close();
      br.close();
    } catch (IOException e) {
      LOGGER.error(e.getClass().getName() + ": " + e.getMessage());
      System.exit(1);
    }
    return new JSONObject(sb.toString());
  }

}
