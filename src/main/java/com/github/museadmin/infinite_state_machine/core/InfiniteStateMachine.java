package com.github.museadmin.infinite_state_machine.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The primary parent object that contains all of the components
 * of the infinite state machine
 */
public class InfiniteStateMachine extends Bootstrap implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(InfiniteStateMachine.class.getName());

  /**
   * Constructor accepts a fully qualified path to an alternative
   * properties file. This enables applications to pass in their own
   * properties.
   * @param propertiesFile Fully qualified name of properties file
   */
  public InfiniteStateMachine(String propertiesFile) {
    propertyCache.importProperties(propertiesFile);
    rdbms = propertyCache.getProperty("rdbms");
    // Control directories and DB
    createRuntimeDirectories();
    createDatabase();
  }

  /**
   * Run the state machine main control loop in a
   * background thread.
   */
  public void run() {
    int i = 0;
    while (! queryRunPhase().equals("STOPPED")) {
      actions.get(i++).execute();
      if (i == actions.size()) {
        i = 0;
      }
    }
  }

  /**
   * Query a property in the properties table
   * @param property Name of the property
   * @return value of the property
   */
  public String queryProperty(String property) {
    return dataAccessLayer.queryProperty(property);
  }

  /**
   * Make the run phase available to caller
   * @return String run phase
   */
  public String queryRunPhase() {
    return dataAccessLayer.queryRunPhase();
  }
}
