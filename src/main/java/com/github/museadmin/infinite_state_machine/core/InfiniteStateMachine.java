package com.github.museadmin.infinite_state_machine.core;

import com.github.museadmin.infinite_state_machine.dal.IDataAccessLayer;
import com.github.museadmin.infinite_state_machine.dal.Postgres;
import com.github.museadmin.infinite_state_machine.dal.Sqlite3;
import com.github.museadmin.infinite_state_machine.lib.PropertyCache;

import java.io.File;

/**
 * The primary parent object that contains all of the components
 * of the infinite state machine
 */
public class InfiniteStateMachine {

    private String dbFile;
    private String epochSeconds = Long.toString(System.currentTimeMillis()/1000);
    private PropertyCache propertyCache = new PropertyCache("environment.properties");
    private String rdbms;
    private String runRoot;
    private IDataAccessLayer iDataAccessLayer;

    public String getRdbms() {
        return rdbms;
    }

    public InfiniteStateMachine() {
        rdbms = propertyCache.getProperty("rdbms");
        createRuntimeDirectories();
        createDatabase();
    }

    private void createDatabase() {
        if (rdbms.equalsIgnoreCase("SQLITE3")) {
            // Create the runtime dir for the sqlite3 db
            String dbPath = runRoot + "control" + File.separator + "database";
            File dir = new File (dbPath);
            if (! dir.isDirectory()) { dir.mkdirs(); }
            dbFile = dbPath + File.separator + "ism.db";
            // Create the database itself
            iDataAccessLayer = new Sqlite3(dbFile);
        } else if (rdbms.equalsIgnoreCase("POSTGRES")) {
            iDataAccessLayer = new Postgres(propertyCache, epochSeconds);
        } else {
            throw new RuntimeException("Failed to identify RDBMS in use from property file");
        }

    }

    private void createRuntimeDirectories() {
        // The root directory for the run
        runRoot = propertyCache.getProperty("runRoot") + File.separator +
            epochSeconds + File.separator;
        File root = new File(runRoot);
        if (! root.isDirectory()) { root.mkdirs(); }
    }
}
