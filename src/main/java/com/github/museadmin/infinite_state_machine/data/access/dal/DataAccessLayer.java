package com.github.museadmin.infinite_state_machine.data.access.dal;

import com.github.museadmin.infinite_state_machine.lib.PropertyCache;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * The DAL mirrors the methods in each DAO and acts
 * as a pass through to the DAO in use
 */
public class DataAccessLayer implements IDataAccessLayer {

  IDataAccessObject iDataAccessObject;
  PropertyCache propertyCache;

  /**
   * Create the runtime control database
   * @param props The propertyCache from the ISM
   * @param runRoot The top level directory created for the run
   * @param epochSeconds Used to create a unique DB identifier. Ignored by SQLITE3
   */
  public DataAccessLayer(PropertyCache props, String runRoot, String epochSeconds) {

    propertyCache = props;
    String rdbms = propertyCache.getProperty("rdbms");

    if (rdbms.equalsIgnoreCase("SQLITE3")) {

      // Create the runtime dir for the sqlite3 db
      String dbPath = runRoot + "control" + File.separator + "database";
      File dir = new File (dbPath);
      if (! dir.isDirectory()) { dir.mkdirs(); }
      String dbFile = dbPath + File.separator + "ism.db";

      // Create the database itself
      iDataAccessObject = new Sqlite3(dbFile);

    } else if (rdbms.equalsIgnoreCase("POSTGRES")) {
      iDataAccessObject = new Postgres(propertyCache, epochSeconds);
    } else {
      throw new RuntimeException("Failed to identify RDBMS in use from property file");
    }
  }

  // ================= Action Helper Methods =================

  /**
   * Activate an action.
   * @param actionName The name of the action to activate
   */
  public void activate(String actionName) {
    iDataAccessObject.activate(actionName);
  }

  /**
   * Check if all "After" actions have completed so that we can
   * change state to STOPPED.
   * @return True if not all complete
   */
  public Boolean afterActionsComplete() {
    return iDataAccessObject.afterActionsComplete();
  }

  /**
   * Check if all "Before" actions have completed so that we can
   * change state to running.
   * @return True if all complete
   */
  public Boolean beforeActionsComplete() {
    return iDataAccessObject.beforeActionsComplete();
  }

  /**
   * Deactivate an action.
   * @param actionName The name of the action to deactivate
   */
  public void deactivate(String actionName) {
    iDataAccessObject.deactivate(actionName);
  }

  /**
   * Test if this action is active
   * @return True or False for not active
   */
  public Boolean active(String actionName) {
    return iDataAccessObject.active(actionName);
  }

  /**
   * Query a property in the properties table
   * @param property Name of the property
   * @return value of the property
   */
  public String queryProperty(String property) {
    return iDataAccessObject.queryProperty(property);
  }


  public String queryRunPhase() {
    return iDataAccessObject.queryRunPhase();
  }

  /**
   * Set the run state. The run states are an option group
   * Hence the special method for setting these.
   * EMERGENCY_SHUTDOWN
   * NORMAL_SHUTDOWN
   * RUNNING
   * STARTING
   * STOPPED
   * @param runPhase Name of state to change to
   */
  public void changeRunPhase(String runPhase) {
    iDataAccessObject.changeRunPhase(runPhase);
  }

  /**
   * Set a state in the state table
   * @param stateName The name of the state
   */
  public void setState(String stateName) {
    iDataAccessObject.setState(stateName);
  }

  /**
   * Unset a state in the state table
   * @param stateName The name of the state
   */
  public void unsetState(String stateName) {
    iDataAccessObject.unsetState(stateName);
  }

  // ================= Direct Database Manipulation =================

  /**
   * Execute a SQL query and return the results in an array list
   * @param sql The query
   * @return ArrayList holds the records returned
   */
  public ArrayList<String> executeSqlQuery(String sql) {
    return iDataAccessObject.executeSqlQuery(sql);
  }

  /**
   * Executes a SQL statement
   * @param sql The statement to execute
   * @return True or False for success or failure
   */
  public Boolean executeSqlStatement(String sql) {
    return iDataAccessObject.executeSqlStatement(sql);
  }

  /**
   * Create a database table using a JSON definition
   * @param table JSONObject
   */
  public void createTable(JSONObject table) {
    iDataAccessObject.createTable(table);
  }

}
