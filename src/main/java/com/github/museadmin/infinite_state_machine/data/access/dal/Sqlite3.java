package com.github.museadmin.infinite_state_machine.data.access.dal;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Data Access Object for when using Sqlite3
 */
public class Sqlite3 implements IDataAccessObject {

  private String database;
  public String SQLITE_TRUE = "1";
  public String SQLITE_FALSE = "0";

  // ================= Setup Methods =================

  /**
   * Constructor attempts to create a new database
   * @param database Fully qualified path to DB
   */
  public Sqlite3(String database) {
      this.database = database;
      createDatabase(database);
  }

  /**
   * Create a unique database instance for the run
   * @param database Fully qualified name of the DB
   */
  public void createDatabase(String database) {
    getConnection(database);
  }

  /**
   * Create a database table using a JSON definition
   * @param table JSONObject
   */
  public void createTable(JSONObject table) {
    executeSqlStatement(createTableStatement(table));
  }

  /**
   * SQLite3 context aware CREATE TABLE statement builder
   * @param table JSONObject created from JSON defintion file
   * @return String Create table statement
   */
  private String createTableStatement(JSONObject table) {

    StringBuilder sbSql = new StringBuilder(200);
    sbSql.append("CREATE TABLE ");
    sbSql.append(table.get("name"));
    sbSql.append(" (\n");

    JSONArray columns = table.getJSONArray("columns");

    columns.forEach(column -> {
      JSONObject col = (JSONObject) column;

      sbSql.append(col.getString("name"));
      sbSql.append(" " + col.getString("type"));

      if (col.getBoolean("not_null")) {
        sbSql.append(" NOT NULL");
      }

      String def = col.getString("default");
      if (! def.isEmpty()) {
        sbSql.append(" DEFAULT '" + def + "'");
      }

      if (col.getBoolean("primary_key")) {
        sbSql.append(" PRIMARY KEY");
      }

      sbSql.append(", ");

      String comment = col.getString("comment");
      if (! comment.isEmpty()) {
        sbSql.append("-- " + comment);
      }

      sbSql.append(" \n");
    });

    sbSql.append(");");

    String sql = sbSql.toString();
    int index = sql.lastIndexOf(',');
    sbSql.deleteCharAt(index);

    return sbSql.toString();
  }

  // ================= DB Query Methods =================

  /**
   * Execute a SQL query and return the results in an array list
   * @param sql The query
   * @return ArrayList holds the records returned
   */
  public ArrayList<String> executeSqlQuery(String sql) {

    ArrayList<String> resultList = new ArrayList<>();

    try {
      Connection connection = getConnection(database);
      Statement statement = connection.createStatement();
      ResultSet results = statement.executeQuery(sql);

      int columnCount = results.getMetaData().getColumnCount();

      while (results.next()) {
        int i = 1;
        while(i <= columnCount) {
          resultList.add(results.getString(i++));
        }
      }
      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(1);
    }
    return resultList;
  }

  /**
   * Executes a SQL statement
   * @param sql The statement to execute
   * @return True or False for success or failure
   */
  public Boolean executeSqlStatement(String sql)  {
    Boolean rc = false;
    try {
      Connection connection = getConnection(database);
      Statement statement = connection.createStatement();
      rc = statement.execute(sql);
      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(1);
    }
    return rc;
  }

  /**
   * Return a connection to the sqlite3 database
   * @param database The fully qualified path to the database
   * @return Connection
   */
  private Connection getConnection(String database){
    Connection connection = null;
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:" + database);
    } catch (SQLException e) {
      e.printStackTrace();
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(1);
    }
    return connection;
  }

  // ================= Action Helper Methods =================

  /**
   * Test if this action is active
   * @return True or False for not active
   */
  public Boolean active(String actionName) {

    String runPhase = queryRunPhase();

    ArrayList<String> results = executeSqlQuery(
      "SELECT active FROM actions WHERE action = " +
        "'" + actionName + "' " +
        "AND (run_phase = " +
        "'" + runPhase + "' " +
        "OR run_phase = 'ALL') " +
        "AND active = " +
        "'" + SQLITE_TRUE + "';"
    );

    return results.size() > 0;
  }

  /**
   * Activate an action.
   * @param actionName The name of the axction to activate
   */
  public void activate(String actionName) {
    executeSqlStatement(
      "UPDATE actions SET active = " +
        "'" + SQLITE_TRUE + "'" +
        "WHERE action = " +
        "'" + actionName + "';"
    );
  }

  /**
   * Check if all "After" actions have completed so that we can
   * change state to STOPPED.
   * @return True if not all complete
   */
  public Boolean afterActionsComplete() {

    ArrayList<String> states = executeSqlQuery(
      "SELECT state FROM states WHERE state_name IN " +
        "('EMERGENCY_SHUTDOWN', 'NORMAL_SHUTDOWN');"
    );

    if (states.size() > 0 && states.get(0).equals(SQLITE_TRUE)) {
      ArrayList<String>  results = executeSqlQuery(
        "SELECT * FROM actions WHERE action " +
          "LIKE '%After%' " +
          "AND active = " +
          "'" + SQLITE_TRUE + "';"
      );
      return results.size() == 0;
    }
    return false;
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
    // TODO Add check that passed state is valid
    executeSqlStatement(
      "UPDATE states SET state = " +
        "'" + SQLITE_FALSE + "'" +
        "WHERE state_name " +
        "IN ('EMERGENCY_SHUTDOWN', 'NORMAL_SHUTDOWN', 'RUNNING', 'STARTING', 'STOPPED');"
    );

    executeSqlStatement(
      "UPDATE states SET state = " +
        "'" + SQLITE_TRUE + "'" +
        "WHERE state_name = " +
        "'" + runPhase + "';"
    );
  }

  /**
   * Deactivate an action.
   * @param actionName The name of the action to deactivate
   */
  public void deactivate(String actionName) {
    executeSqlStatement(
      "UPDATE actions SET active = " +
        "'" + SQLITE_FALSE + "' " +
        "WHERE action = " +
        "'" + actionName + "';"
    );
  }

  /**
   * Check if all "Before" actions have completed so that we can
   * change state to running.
   * @return True if not all complete
   */
  public Boolean beforeActionsComplete() {

    ArrayList<String> states = executeSqlQuery(
      "SELECT state FROM states WHERE state_name = 'STARTING';"
    );

    if (states.size() > 0 && states.get(0).equals(SQLITE_TRUE)) {
      ArrayList<String> results = executeSqlQuery(
        "SELECT * FROM actions WHERE action " +
          "LIKE '%Before%' " +
          "AND active = " +
          "'" + SQLITE_TRUE + "';"
      );
      return results.size() == 0;
    }
    return false;
  }

  /**
   * Query a property in the properties table
   * @param property Name of the property
   * @return value of the property
   */
  public String queryProperty(String property) {

    ArrayList<String> results = executeSqlQuery(
      "SELECT value FROM properties WHERE property = " +
        "'" + property + "';"
    );
    return results.size() > 0 ? results.get(0) : "";
  }

  /**
   * Return the active run phase
   * @return The name of the active run phase
   */
  public String queryRunPhase() {

    ArrayList<String> results = executeSqlQuery(
      "SELECT state_name FROM states WHERE state_name IN (" +
        "'RUNNING'," +
        "'STARTING'," +
        "'STOPPED'," +
        "'NORMAL_SHUTDOWN'," +
        "'EMERGENCY_SHUTDOWN')" +
        "AND state = '" + SQLITE_TRUE +"';"
    );

    return results.size() > 0 ? results.get(0) : "";
  }

  /**
   * Set a state in the state table
   * @param stateName The name of the state
   */
  public void setState(String stateName) {
    executeSqlStatement(
      "UPDATE states SET " +
        "state = " +
        "'" + SQLITE_TRUE + "'" +
        "WHERE state_name = " +
        "'" + stateName + "';"
    );
  }

  /**
   * Unset a state in the state table
   * @param stateName The name of the state
   */
  public void unsetState(String stateName) {
    executeSqlStatement(
      "UPDATE states SET " +
        "state = " +
        "'" + SQLITE_FALSE + "'" +
        "WHERE state_name = " +
        "'" + stateName + "';"
    );
  }
}