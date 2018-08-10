package com.github.museadmin.infinite_state_machine.data.access.dal;

import com.github.museadmin.infinite_state_machine.data.access.utils.InvalidRunPhase;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Data Access Object for when using Sqlite3
 */
public class Sqlite3 implements IDataAccessObject {

  private String database;
  public String SQLITE_TRUE = "1";
  public String SQLITE_FALSE = "0";

  // ================= Setup =================

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

  // ================= DB =================

  /**
   * Execute a SQL query and return the results in an array list
   * @param sql The query
   * @return ArrayList holds the records returned
   */
  public ArrayList<JSONObject> executeSqlQuery(String sql) {

    ArrayList<JSONObject> rows = new ArrayList<>();

    try {
      Connection connection = getConnection(database);
      Statement statement = connection.createStatement();
      ResultSet rs = statement.executeQuery(sql);
      ResultSetMetaData rsm = rs.getMetaData();
      int columnCount = rsm.getColumnCount();

      while (rs.next()) {
        int i = 1;
        JSONObject row = new JSONObject();
        while(i <= columnCount) {
          row.put(rsm.getColumnName(i), rs.getString(i++));
        }
        rows.add(row);
      }
      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(1);
    }
    return rows;
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

  // ================= Action =================

  /**
   * Test if this action is active
   * @return True or False for not active
   */
  public Boolean active(String actionName) {

    String runPhase = queryRunPhase();
    ArrayList<JSONObject> results = executeSqlQuery(
      String.format("SELECT active FROM actions WHERE action = '%s' AND (run_phase = '%s' OR run_phase = 'ALL') " +
        "AND active = '%s';", actionName, runPhase, SQLITE_TRUE)
    );
    return results.size() > 0;
  }

  /**
   * Activate an action.
   * @param actionName The name of the axction to activate
   */
  public void activate(String actionName) {
    executeSqlStatement(
      String.format("UPDATE actions SET active = '%s'WHERE action = '%s';", SQLITE_TRUE, actionName)
    );
  }

  /**
   * Deactivate an action.
   * @param actionName The name of the action to deactivate
   */
  public void deactivate(String actionName) {
    executeSqlStatement(
      String.format("UPDATE actions SET active = '%s' WHERE action = '%s';", SQLITE_FALSE, actionName)
    );
  }

  /**
   * Clear the payload for an action prior to deactivation
   * @param actionName The name of the action
   */
  public void clearPayload(String actionName) {
    executeSqlStatement(
      String.format("UPDATE actions SET payload = '' WHERE action = '%s';", actionName)
    );
  }

  /**
   * Update the payload for an action
   * @param actionName The name of the action
   */
  public void updatePayload(String actionName, String payload) {
    executeSqlStatement(
      String.format("UPDATE actions SET payload = '%s'WHERE action = '%s';", payload, actionName)
    );
  }

  // ================= Hooks =================

  /**
   * Check if all "After" actions have completed so that we can
   * change state to STOPPED.
   * @return True if not all complete
   */
  public Boolean afterActionsComplete() {

    ArrayList<JSONObject> results = executeSqlQuery(
      String.format("SELECT * FROM actions WHERE action LIKE '%%After%%' AND active = '%s';", SQLITE_TRUE)
    );
    return results.size() == 0;
  }

  /**
   * Check if all "Before" actions have completed so that we can
   * change state to running.
   * @return True if not all complete
   */
  public Boolean beforeActionsComplete() {

    ArrayList <JSONObject> results = executeSqlQuery(
      String.format("SELECT * FROM actions WHERE action LIKE '%%Before%%' AND active = '%s';", SQLITE_TRUE)
    );
    return results.size() == 0;
  }

  // ================= Property  =================

  /**
   * Insert a new property into the properties table
   * @param property The name of the property
   * @param value The value of the property
   */
  public void insertProperty(String property, String value) {
    executeSqlStatement(
      String.format("INSERT INTO properties (property, value) values ('%s', '%s');", property, value)
    );
  }

  /**
   * Update an existing property in the properties table
   * @param property The name of the property
   * @param value The value of the property
   */
  public void updateProperty(String property, String value) {
    executeSqlStatement(
      String.format("UPDATE properties SET value = '%s' WHERE property = '%s';", value, property)
    );
  }

  /**
   * Query a property in the properties table
   * @param property Name of the property
   * @return value of the property
   */
  public String queryProperty(String property) {

    ArrayList<JSONObject> results = executeSqlQuery(
      String.format("SELECT value FROM properties WHERE property = '%s';", property)
    );
    return results.size() > 0 ? results.get(0).get("value").toString() : "";
  }

  // ================= Run phase  =================

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
  public void updateRunPhase(String runPhase) {

    if (Arrays.asList("EMERGENCY_SHUTDOWN", "NORMAL_SHUTDOWN", "RUNNING", "STARTING", "STOPPED").contains(runPhase)) {
      executeSqlStatement(
        "UPDATE phases SET state = " +
          "'" + SQLITE_FALSE + "'" +
          "WHERE phase_name " +
          "IN ('EMERGENCY_SHUTDOWN', 'NORMAL_SHUTDOWN', 'RUNNING', 'STARTING', 'STOPPED');"
      );

      executeSqlStatement(
        "UPDATE phases SET state = " +
          "'" + SQLITE_TRUE + "'" +
          "WHERE phase_name = " +
          "'" + runPhase + "';"
      );
    } else {
      throw new InvalidRunPhase(String.format("Invalid Run Phase passed (%s)", runPhase));
    }
  }

  /**
   * Return the active run phase
   * @return The name of the active run phase
   */
  public String queryRunPhase() {
    ArrayList<JSONObject> results = executeSqlQuery(
      String.format("SELECT phase_name FROM phases WHERE state = '%s';", SQLITE_TRUE)
    );
    return results.size() > 0 ? results.get(0).get("phase_name").toString() : "";
  }

  // ================= State  =================

  /**
   * Set a state in the state table
   * @param stateName The name of the state
   */
  public void setState(String stateName) {
    executeSqlStatement(
      String.format("UPDATE states SET state = '%s'WHERE state_name = '%s';", SQLITE_TRUE, stateName)
    );
  }

  /**
   * Unset a state in the state table
   * @param stateName The name of the state
   */
  public void unsetState(String stateName) {
    executeSqlStatement(
      String.format("UPDATE states SET state = '%s'WHERE state_name = '%s';", SQLITE_FALSE, stateName)
    );
  }

  // ================= Message  =================

  /**
   * Insert a message into the database. Assumes valid json object.
   * @param message JSONObject the message
   */
  public void insertMessage(JSONObject message) {

    executeSqlStatement(
      "INSERT INTO messages " +
        "(sender, " +
        "sender_id, " +
        "recipient, " +
        "action, " +
        "sent, " +
        "received, " +
        "direction, " +
        "processed, " +
        "payload) " +
        "VALUES " +
        "(" +
        "'" + message.get("sender") + "', " +
        "'" + message.get("sender_id") + "', " +
        "'" + message.get("recipient") + "', " +
        "'" + message.get("action") + "', " +
        "'" + message.get("sent") + "', " +
        "'" + message.get("received") + "', " +
        "'" + message.get("direction") + "', " +
        "'" + message.get("processed") + "', " +
        "'" + message.get("payload") + "'" +
        ");"
    );
  }

  /**
   * Retrieve an array of unprocessed messages form the database messages table
   * @return ArrayList of messages as JSONObjects
   */
  public ArrayList<JSONObject> getUnprocessedMessages() {
    return executeSqlQuery(
      String.format("SELECT * from messages WHERE processed = '%s';", SQLITE_FALSE)
    );
  }

  /**
   * Set the processed field true of a message record
   * @param id The ID (PK) of the record
   */
  public void markMessageProcessed(Integer id) {
    executeSqlStatement(
      String.format("UPDATE messages SET processed = '%s' WHERE id = %d;", SQLITE_TRUE, id)
    );
  }

}