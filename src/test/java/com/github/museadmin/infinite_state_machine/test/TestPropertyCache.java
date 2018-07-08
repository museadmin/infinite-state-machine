package com.github.museadmin.infinite_state_machine.test;

import com.github.museadmin.infinite_state_machine.lib.PropertyCache;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class TestPropertyCache {

  protected PropertyCache propertyCache;

  @Before
  public void setUp(){
    propertyCache = new PropertyCache("environment.properties");
  }

  @Test
  public void testCacheGetsValueForKey() {
    assertTrue(propertyCache.getProperty("rdbms").equalsIgnoreCase("sqlite3") ||
        propertyCache.getProperty("rdbms").equalsIgnoreCase("postgres"));
  }
  @Test
  public void testCacheGetsDefaultValueForUnknownKey() {
    assertEquals("default",
        propertyCache.getProperty("xdbms", "default"));
  }
  @Test
  public void testCacheDoesNotGetDefaultValueForKnownKey() {
    assertTrue(propertyCache.getProperty("rdbms", "default").equalsIgnoreCase("sqlite3") ||
        propertyCache.getProperty("rdbms", "default").equalsIgnoreCase("postgres"));
  }
  @Test
  public void testImportPropertiesOverridesDefaults() {
    try {
      File f = File.createTempFile("test", ".properties");
      String tmpProps = f.getAbsolutePath();
      CommonSupportMethods.createTmpPropertiesFile(tmpProps);

      propertyCache.importProperties(tmpProps);
      assertEquals(propertyCache.getProperty("rdbms"), "sqlite3");

      if(f.exists() && !f.isDirectory()) {
        f.delete();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
