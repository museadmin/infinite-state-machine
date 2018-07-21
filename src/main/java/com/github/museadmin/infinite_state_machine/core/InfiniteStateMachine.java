package com.github.museadmin.infinite_state_machine.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The primary parent object that contains all of the components
 * of the infinite state machine
 */
public class InfiniteStateMachine extends Bootstrap {

  private static final Logger LOGGER = LoggerFactory.getLogger(InfiniteStateMachine.class.getName());

  /**
   * Default constructor
   */
  public InfiniteStateMachine() {
    rdbms = propertyCache.getProperty("rdbms");
    createRuntimeDirectories();

    // One-off import for core action pack. Other action
    // packs would be imported at request of applications
    // via the public importActionPack method
    importActionPack(ismCoreActionPack);
  }

  /**
   * Constructor accepts a fully qualified path to an alternative
   * properties file. This enables applications to pass in their own
   * properties.
   * @param propertiesFile
   */
  public InfiniteStateMachine(String propertiesFile) {
    propertyCache.importProperties(propertiesFile);
    rdbms = propertyCache.getProperty("rdbms");
    createRuntimeDirectories();

    // One-off import for core action pack. Other action
    // packs would be imported at request of applications
    // via the public importActionPack method
    importActionPack(ismCoreActionPack);
  }

  /**
   * TODO main loop
   * File based messaging actions should be part of core
   * action pack. They can then be used for unit testing.
   * Need to be able to disable from properties.
   * msg action should not write msg to db if target action
   * is already active.
   */
}
