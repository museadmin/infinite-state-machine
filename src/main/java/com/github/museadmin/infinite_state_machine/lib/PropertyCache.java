package com.github.museadmin.infinite_state_machine.lib;

import java.io.IOException;
import java.util.Properties;

/**
 * Reads a properties file on construction and caches them for later use.
 */
public class PropertyCache {

  private Properties prop = new Properties();

  /**
   * Reads the values in a properties file and caches them for later retrieval.
   *
   * @param file The path to the properties file.
   */
  public PropertyCache(String file) {
    try
    {
      prop.load(ClassLoader.getSystemResourceAsStream(file));
    } catch (IOException e)
    {
      e.printStackTrace();
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
