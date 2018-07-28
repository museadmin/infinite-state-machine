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

  /**
   * Execute a SQL query and return the results in an array list
   * @param sql The query
   * @return ArrayList<String> the records returned
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
