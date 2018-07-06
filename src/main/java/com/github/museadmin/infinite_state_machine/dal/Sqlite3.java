package com.github.museadmin.infinite_state_machine.dal;

import org.json.JSONObject;
import org.json.JSONArray;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Data Access Object for when using Sqlite3
 */
public class Sqlite3 implements IDataAccessLayer {

    private String database;

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

    /**
     * SQLite3 context aware CREATE TABLE statement builder
     * @param table JSONObject created from JSON defintion file
     * @return The SQL as a string
     */
    private String createTableStatement(JSONObject table) {

        StringBuilder sbSql = new StringBuilder(100);
        sbSql.append("CREATE TABLE ");
        sbSql.append(table.get("table_name"));
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
}