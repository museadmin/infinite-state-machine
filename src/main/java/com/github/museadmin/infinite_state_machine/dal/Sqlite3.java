package com.github.museadmin.infinite_state_machine.dal;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Sqlite3 implements IDataAccessLayer {

    /**
     * Constructor attempts to create a new database
     * @param database Fully qualified path to DB
     */
    public Sqlite3(String database) {

        // Ensure that the directory for the database exists
        File theDb = new File(database);
        File parent = new File(theDb.getParent());
        if (! parent.isDirectory()) { parent.mkdirs(); }

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
