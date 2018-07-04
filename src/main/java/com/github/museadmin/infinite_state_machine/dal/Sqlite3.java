package com.github.museadmin.infinite_state_machine.dal;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Data Access Layer for when using Sqlite3
 */
public class Sqlite3 implements IDataAccessLayer {

    /**
     * Constructor attempts to create a new database
     * @param database Fully qualified path to DB
     */
    public Sqlite3(String database) {
        createDatabase(database);
    }

    public boolean createDatabase(String database) {
        try {
            DriverManager.getConnection("jdbc:sqlite:" + database);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }
}
