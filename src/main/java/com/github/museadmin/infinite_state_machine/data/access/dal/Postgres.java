package com.github.museadmin.infinite_state_machine.data.access.dal;

import com.github.museadmin.infinite_state_machine.data.access.utils.JsonToSqlEtl;
import com.github.museadmin.infinite_state_machine.lib.PropertyCache;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for when using Postgres
 */
public class Postgres implements IDataAccessObject {

  private Map dbData = new HashMap<String, String>();
  private List<String> comments = new ArrayList<>();
  private List<String> primaryKeys = new ArrayList<>();

  public Postgres(PropertyCache propertyCache, String epochSeconds) {

      dbData.put("dbHost", propertyCache.getProperty("dbHost"));
      dbData.put("dbName", propertyCache.getProperty("dbName") + epochSeconds);
      dbData.put("dbPassword", propertyCache.getProperty("dbPassword"));
      dbData.put("dbPort", propertyCache.getProperty("dbPort"));
      dbData.put("dbUser", propertyCache.getProperty("dbUser"));

      createDatabase(dbData.get("dbName").toString());
  }

  /**
   * Activate an action.
   * @param actionName The name of the axction to activate
   */
  public void activate(String actionName) {

  }

  /**
   * Check if all "After" actions have completed so that we can
   * change state to STOPPED.
   * @return True if not all complete
   */
  public Boolean afterActionsComplete() {
    ArrayList<String> results = executeSqlQuery(
      "SELECT * FROM actions WHERE action " +
        "LIKE '%After%' " +
        "AND active = " +
        "'" + "1" + "';"
    );
    return results.size() == 0;
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

  }

  /**
   * Create a unique database instance for the run
   * @param database Name of the DB
   */
  public void createDatabase(String database) {

      try {
          Connection connection = getConnection("postgres");
          Statement statement = connection.createStatement();
          statement.executeUpdate("CREATE DATABASE " + database);
      } catch (SQLException e) {
          e.printStackTrace();
          System.err.println(e.getClass().getName() + ": " + e.getMessage());
          System.exit(1);
      }
  }

  /**
   * Create a database table using a JSON definition
   * @param table JSONObject
   */
  public void createTable(JSONObject table){
      executeSqlStatement(createTableStatement(table));
      comments.forEach(comment -> executeSqlStatement(comment));
      comments.clear();
      executeSqlStatement(createPrimaryKeys(table));
      primaryKeys.clear();
  }

  /**
   * Deactivate an action.
   * @param actionName The name of the action to deactivate
   */
  public void deactivate(String actionName) {

  }

  /**
   * Executes a SQL statement
   * @param sql The statement to execute
   * @return True or False for success or failure
   */
  public Boolean executeSqlStatement(String sql)  {
      Boolean rc = false;
      try {
          Connection connection = getConnection(getDbData("dbName"));
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
   * Test if this action is active
   * @return True or False for not active
   */
  public Boolean active(String actionName) {
    return true;
  }

  /**
   * Check if all "Before" actions have completed so that we can
   * change state to running.
   * @return True if all complete
   */
  public Boolean beforeActionsComplete() {
    return true;
  }

  /**
   * Insert a new property into the properties table
   * @param property The name of the property
   * @param value The value of the property
   */
  public void insertProperty(String property, String value) {
    executeSqlStatement(
      "INSERT INTO properties (property, value) values " +
        "('" + property + "', '" + value + "');"
    );
  }

  /**
   * Query a property in the properties table
   * @param property Name of the property
   * @return value of the property
   */
  public String queryProperty(String property) {return "";}

  /**
   * Return the active run phase
   * @return The name of the active run phase
   */
  public String queryRunPhase() {
    return "";
  }

  /**
   * Set a state in the state table
   * @param stateName The name of the state
   */
  public void setState(String stateName) {

  }

  /**
   * Update an existing property in the properties table
   * @param property The name of the property
   * @param value The value of the property
   */
  public void updateProperty(String property, String value) {
    executeSqlStatement(
      "UPDATE properties SET " +
        property + " = '" + value + "');"
    );
  }

  /**
   * Unset a state in the state table
   * @param stateName The name of the state
   */
  public void unsetState(String stateName) {

  }

  /**
   * Execute a SQL query and return the results in an array list
   * @param sql The query
   * @return ArrayList holds the records returned
   */
  public ArrayList<String> executeSqlQuery(String sql) {
    ArrayList<String> resultList = new ArrayList<>();
    return resultList;
  }

  /**
   * Create any primary keys that were defined for the table
   */
  private String createPrimaryKeys(JSONObject table) {
    StringBuilder sql = new StringBuilder();
    sql.append("ALTER TABLE " +
      table.get("name") +
      " ADD PRIMARY KEY (");

    for (int i = 0; i < primaryKeys.size(); i++) {
      sql.append(primaryKeys.get(i) + ", ");
    }
    sql = JsonToSqlEtl.removeLastComma(sql);
    sql.append(");");
    return sql.toString();
  }

  /**
   * Return a connection to the postgres database
   * @param database Database Name
   * @return Connection
   */
  private Connection getConnection(String database) {
      Connection connection = null;
      try {
          connection = DriverManager.getConnection("jdbc:postgresql://" +
              getDbData("dbHost") + ":" + getDbData("dbPort") + "/" + database,
              getDbData("dbUser"), getDbData("dbPassword"));
      } catch (SQLException e) {
          e.printStackTrace();
          System.err.println(e.getClass().getName() + ": " + e.getMessage());
          System.exit(1);
      }
      return connection;
  }

  /**
   * Return a member of the dbData hash
   * @param key Hash ke
   * @return String Value
   */
  private String getDbData(String key) {
      return dbData.get(key).toString();
  }

  /**
   * SQLite3 context aware CREATE TABLE statement builder
   * @param table JSONObject created from JSON defintion file
   * @return The SQL as a string
   */
  private String createTableStatement(JSONObject table) {

      StringBuilder sbSql = new StringBuilder(100);
      sbSql.append("CREATE TABLE ");
      sbSql.append(table.get("name"));
      sbSql.append(" (");

      JSONArray columns = table.getJSONArray("columns");

      columns.forEach(column -> {
          JSONObject col = (JSONObject) column;
          String name = col.getString("name");

          sbSql.append(name);
          sbSql.append(" " + col.getString("type"));

          if (col.getBoolean("not_null")) {
              sbSql.append(" NOT NULL");
          }

          String def = col.getString("default");
          if (! def.isEmpty()) {
              sbSql.append(" DEFAULT '" + def + "'");
          }

          if (col.getBoolean("primary_key")) {
            primaryKeys.add(name);
          }

          sbSql.append(", ");

          String comment = col.getString("comment");
          if (! comment.isEmpty()) { comments.add("COMMENT ON COLUMN " +
                  table.get("name") + "." +
                  col.getString("name") + " is '" +
                  comment + "';"
              );
          }
      });

      sbSql.append(");");

      String sql = sbSql.toString();
      int index = sql.lastIndexOf(',');
      sbSql.deleteCharAt(index);

      return sbSql.toString();
  }

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
    return new ArrayList<JSONObject>();
  }

}