package com.github.museadmin.infinite_state_machine.ism;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.net.URL;

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
   *
   * Alternatively no path defaults to "infinite_state_machine.properties"
   * and an unqualified name prompts a search via the classloader for the file.
   *
   * @param propertiesFile Fully qualified name of properties file or
   *                       the unqualified name or empty string.
   */
  public InfiniteStateMachine(String propertiesFile) {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    URL is;

    File props = new File(propertiesFile);

    if (props.exists()) {
      propertyCache.importProperties(propertiesFile);
    } else {
      if (propertiesFile.isEmpty()) {
        is = loader.getResource("infinite_state_machine.properties");
      } else {
        is = loader.getResource(propertiesFile);
      }
      try {
        propertyCache.importProperties(is.getPath());
      } catch (NullPointerException e) {
        System.out.printf("ERROR: Did not find properties file (%s)%n", propertiesFile);
      }

    }

    // Control directories
    createRuntimeDirectories();
    // Control DB
    createDatabase();
  }

  /**
   * Run the state machine main control loop in a
   * background thread.
   */
  @Override
  public void run() {
    LOGGER.info("Starting run");
    int i = 0;
    while (! queryRunPhase().equals("STOPPED")) {
      actions.get(i++).execute();
      if (i == actions.size()) {
        i = 0;
      }
    }
    LOGGER.info("Run completed");
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
