package com.github.museadmin.infinite_state_machine.unit.tests.core;

import com.github.museadmin.infinite_state_machine.lib.PropertyCache;
import com.github.museadmin.infinite_state_machine.unit.tests.support.TestSupportMethods;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class TestPropertyCache {

  protected PropertyCache propertyCache;

  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  @Before
  public void setUp(){
    propertyCache = new PropertyCache("config.properties");
  }

  @Test
  public void testCacheGetsValueForKnownKey() {
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
      String tmpProps = TestSupportMethods.createTmpPropertiesFile(tmpFolder);
      propertyCache.importProperties(tmpProps);
      assertEquals(propertyCache.getProperty("rdbms"), "sqlite3");
  }
}