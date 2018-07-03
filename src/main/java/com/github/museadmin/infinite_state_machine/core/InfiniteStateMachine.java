package com.github.museadmin.infinite_state_machine.core;

import com.github.museadmin.infinite_state_machine.dal.IDataAccessLayer;
import com.github.museadmin.infinite_state_machine.dal.Sqlite3;
import com.github.museadmin.infinite_state_machine.lib.PropertyCache;

import java.io.File;

/**
 * The primary parent object that contains all of the components
 * of the infinite state machine
 */
public class InfiniteStateMachine {

    private PropertyCache propertyCache =
        new PropertyCache("environment.properties");

    public String getRdbms() {
        return rdbms;
    }

    private String rdbms;
    private String dbFile;
    private IDataAccessLayer iDataAccessLayer;

    // Default constructor
    public InfiniteStateMachine() {
        // Set from properties file
        rdbms = propertyCache.getProperty("rdbms");
        String runRoot = propertyCache.getProperty("runRoot");
        dbFile = runRoot + Long.toString(System.currentTimeMillis()/1000) + File.separator + "ism.db";

        // Create the runtime database
        create_database();
    }

    private void create_database() {
        if (rdbms.equalsIgnoreCase("SQLITE3")) {
            iDataAccessLayer = new Sqlite3(dbFile);
        } else if (rdbms.equalsIgnoreCase("POSTGRES")) {

        } else {
            throw new RuntimeException("Failed to identify RDBMS in use from property file");
        }

    }
}
