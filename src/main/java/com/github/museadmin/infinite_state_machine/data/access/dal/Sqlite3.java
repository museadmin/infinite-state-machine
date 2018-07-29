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
   * Test if this action is active
   * @return True or False for not active
   */
  public Boolean notActive(String actionName) {

    String runPhase = queryRunPhase();

    ArrayList<String> results = executeSqlQuery(
      "SELECT active FROM actions WHERE action = " +
        "'" + actionName + "' " +
        "AND run_phase = " +
        "'" + runPhase + "'" +
        "AND active = " +
        "'" + SQLITE_TRUE + "';"
    );

    return results.size() == 0;
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
  private String queryRunPhase() {

    ArrayList<String> results = executeSqlQuery(
      "SELECT state_name FROM states WHERE state_name IN ('RUNNING','STARTING','STOPPED')" +
        "AND state = '" + SQLITE_TRUE +"';"
    );

    return results.size() > 0 ? results.get(0) : "";
  }
}