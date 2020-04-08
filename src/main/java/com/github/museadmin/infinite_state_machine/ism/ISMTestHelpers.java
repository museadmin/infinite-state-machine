package com.github.museadmin.infinite_state_machine.ism;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Public helper methods for the unit tests
 */
public class ISMTestHelpers extends RunState {

  /**
   * Return the RDBMS defined in the loaded property cache
   *
   * @return String RDBMS type
   */
  public String getRdbms() {
    return propertyCache.getProperty("rdbms");
  }

  /**
   * Return a ist of the tables in the database
   *
   * @return ArraList <JSONObject>
   */
  public ArrayList<JSONObject> getDbTables() {
    ArrayList <JSONObject> result;

    switch (propertyCache.getProperty("rdbms").toUpperCase()) {
      case "SQLITE3":
        result = dataAccessLayer.executeSqlQuery(
            "SELECT name FROM sqlite_master WHERE type ='table' AND name NOT LIKE 'sqlite_%';"
        );
        break;
      default:
        throw new RuntimeException("No case for RDBMS");
    }
    return result;
  }
}
