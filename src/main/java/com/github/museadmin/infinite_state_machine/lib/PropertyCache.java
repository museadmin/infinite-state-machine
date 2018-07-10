package com.github.museadmin.infinite_state_machine.lib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Reads a properties file on construction and caches them for later use.
 */
public class PropertyCache {

  private Properties prop = new Properties();
  private static final Logger LOGGER = LoggerFactory.getLogger(PropertyCache.class.getName());

  /**
   * Reads the values in a properties file and caches them for later retrieval.
   *
   * @param file The name of the properties file.
   */
  public PropertyCache(String file) {
    try
    {
      prop.load(ClassLoader.getSystemResourceAsStream(file));
    } catch (IOException e) {
      LOGGER.error(e.getClass().getName() + ": " + e.getMessage());
      System.exit(1);
    }
  }

  /**
   * Import properties from an external file
   * @param file Fully qualified path to file
   */
  public void importProperties(String file) {
    try {
      FileInputStream input = new FileInputStream(file);
      prop.load(input);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      LOGGER.error(e.getClass().getName() + ": " + e.getMessage());
      System.exit(1);
    }
  }

  /**
   * Searches for the given key in the property cache.
   * @param key the hashtable key.
   * @return The property or null if not found.
   */
  public String getProperty(String key) {
    return prop.getProperty(key);
  }

  /**
   * Searches for the given key in the property cache.
   * @param key the hashtable key.
   * @return The property or defaultValue if not found.
   */
  public String getProperty(String key, String defaultValue) {
    return prop.getProperty(key, defaultValue);
  }

}
