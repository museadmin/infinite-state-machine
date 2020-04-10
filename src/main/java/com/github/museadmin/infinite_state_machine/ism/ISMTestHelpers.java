package com.github.museadmin.infinite_state_machine.ism;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Public helper methods for the unit tests
 */
public class ISMTestHelpers extends RunState {

  public final static List<String> dbTypes = new ArrayList<>(Arrays.asList(
      "sqlite3", "SQLITE3", "mysql", "MYSQL"
  ));

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
   * @return ArrayListxx of DB tables
   */
  public ArrayList<JSONObject> getDbTables() {
    ArrayList <JSONObject> result;

    switch (propertyCache.getProperty("rdbms").toUpperCase()) {
      case "SQLITE3":
        result = dataAccessLayer.executeSqlQuery(
            "SELECT name FROM sqlite_master WHERE type ='table' AND name NOT LIKE 'sqlite_%';"
        );
        break;
      case "MYSQL":
        String sql = String.format(
            "SELECT table_name FROM information_schema.tables WHERE table_schema = '%s';",
            propertyCache.getProperty("dbName")
        );
        result = dataAccessLayer.executeSqlQuery(
            sql
        );
        break;
      default:
        throw new RuntimeException("No case for RDBMS");
    }
    return result;
  }
}
