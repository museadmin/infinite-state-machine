package com.github.museadmin.infinite_state_machine.core;

import com.github.museadmin.infinite_state_machine.lib.PropertyCache;

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

    // Default constructor
    public InfiniteStateMachine() {
        // Set from properties file
        rdbms = propertyCache.getProperty("rdbms");

        // Create the runtime database
        create_database();
    }

    private void create_database(){

    }
}
