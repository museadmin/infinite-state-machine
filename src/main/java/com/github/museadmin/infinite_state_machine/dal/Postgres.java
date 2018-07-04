package com.github.museadmin.infinite_state_machine.dal;

import com.github.museadmin.infinite_state_machine.lib.PropertyCache;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Access Layer for when using Postgres
 */
public class Postgres implements IDataAccessLayer {

    private Map dbData = new HashMap<String, String>();

    public Postgres(PropertyCache propertyCache, String epochSeconds) {

        dbData.put("dbHost", propertyCache.getProperty("dbHost"));
        dbData.put("dbName", propertyCache.getProperty("dbName") + epochSeconds);
        dbData.put("dbPassword", propertyCache.getProperty("dbPassword"));
        dbData.put("dbPort", propertyCache.getProperty("dbPort"));
        dbData.put("dbUser", propertyCache.getProperty("dbUser"));

        createDatabase(dbData.get("dbName").toString());
        createTables();
    }

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

    public void createTables(){

        executeSqlStatement(
            "CREATE TABLE state (" +
                "       state_id INTEGER PRIMARY KEY,\n" +
                "        status INTEGER DEFAULT 1,\n" +
                "         state_flag CHAR NOT NULL,\n" +
                "          note CHAR \n" +
                "      );"
        );
        executeSqlStatement("COMMENT ON COLUMN state.status is 'True (1) or False (0)';");
        executeSqlStatement("COMMENT ON COLUMN state.state_flag is 'Textual flag';");
        executeSqlStatement("COMMENT ON COLUMN state.note is 'Comment explaining what this state is for';");

        executeSqlStatement(
            "CREATE TABLE state_machine (\n" +
                "        state_machine_id INTEGER PRIMARY KEY,\n" +
                "        action CHAR,\n" +
                "        phase CHAR DEFAULT 'STARTUP',\n" +
                "        payload CHAR,\n" +
                "        activation INTEGER DEFAULT 0\n" +
                "      );"
        );
        executeSqlStatement("COMMENT ON COLUMN state_machine.action is 'The textual name. e.g. PROCESS_NORMAL_SHUTDOWN';");
        executeSqlStatement("COMMENT ON COLUMN state_machine.phase is 'The run phase';");
        executeSqlStatement("COMMENT ON COLUMN state_machine.payload is 'Any payload for the action';");
        executeSqlStatement("COMMENT ON COLUMN state_machine.activation is 'The activation. ACT = 1 or SKIP = 0';");

        executeSqlStatement(
            "CREATE TABLE properties (\n" +
                "        property CHAR PRIMARY KEY,\n" +
                "        value CHAR\n" +
                "      );"
        );
        executeSqlStatement("COMMENT ON COLUMN properties.property is 'A property';");
        executeSqlStatement("COMMENT ON COLUMN properties.value is 'The value of the property';");

        executeSqlStatement(
            "CREATE TABLE dependencies (\n" +
                "        dependency_id INTEGER PRIMARY KEY,\n" +
                "        dependency CHAR,\n" +
                "        version CHAR\n" +
                "      );"
        );
        executeSqlStatement("COMMENT ON COLUMN dependencies.dependency is 'Name of the dependency';");
        executeSqlStatement("COMMENT ON COLUMN dependencies.version is 'Version number of the dependency';");

    }

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

    private String getDbData(String key) {
        return dbData.get(key).toString();
    }
}
