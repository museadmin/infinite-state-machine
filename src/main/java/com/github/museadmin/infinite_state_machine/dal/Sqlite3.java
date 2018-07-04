package com.github.museadmin.infinite_state_machine.dal;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Sqlite3 implements IDataAccessLayer {

    /**
     * Constructor attempts to create a new database
     * @param database Fully qualified path to DB
     */
    public Sqlite3(String database) {

        try {
            DriverManager.getConnection("jdbc:sqlite:" + database);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean createDatabase() {
        return true;
    }
}
