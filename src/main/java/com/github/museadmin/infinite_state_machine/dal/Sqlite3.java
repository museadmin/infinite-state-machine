package com.github.museadmin.infinite_state_machine.dal;

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
        createTables();
    }

    public void createTables() {

        executeSqlStatement(
            "CREATE TABLE state (" +
                "       state_id INTEGER PRIMARY KEY, \n" +
                "        status INTEGER DEFAULT 1, -- True (1) or False (0) \n" +
                "         state_flag CHAR NOT NULL, -- Textual flag \n" +
                "          note CHAR -- Comment explaining what this state is for \n" +
                "      );"
        );

        executeSqlStatement(
            "CREATE TABLE state_machine (\n" +
                "        state_machine_id INTEGER PRIMARY KEY,\n" +
                "        action CHAR, -- The textual name. e.g. PROCESS_NORMAL_SHUTDOWN\n" +
                "        phase CHAR DEFAULT 'STARTUP', -- The run phase\n" +
                "        payload CHAR, -- Any payload sent via msg for action\n" +
                "        activation INTEGER DEFAULT 0 -- The activation. ACT = 1 or SKIP = 0\n" +
                "      );"
        );

        executeSqlStatement(
            "CREATE TABLE properties (\n" +
                "        property CHAR PRIMARY KEY, -- A property\n" +
                "        value CHAR -- The value of the property\n" +
                "      );"
        );

        executeSqlStatement(
            "CREATE TABLE dependencies (\n" +
                "        dependency_id INTEGER PRIMARY KEY,\n" +
                "        dependency CHAR, -- Name of the Gem\n" +
                "        version CHAR -- Gem version number\n" +
                "      );"
        );
    }

    public void createDatabase(String database) {
        getConnection(database);
    }

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


}
