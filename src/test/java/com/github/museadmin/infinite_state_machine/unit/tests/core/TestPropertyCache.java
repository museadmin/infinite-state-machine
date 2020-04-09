package com.github.museadmin.infinite_state_machine.unit.tests.core;

import com.github.museadmin.infinite_state_machine.common.lib.PropertyCache;
import com.github.museadmin.infinite_state_machine.ism.ISMTestHelpers;
import com.github.museadmin.infinite_state_machine.unit.tests.support.TestSupportMethods;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.net.URL;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class TestPropertyCache extends TestSupportMethods {

  protected PropertyCache propertyCache;

  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  @Before
  public void setUp(){
    propertyCache = new PropertyCache();
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    URL is = loader.getResource(PROPERTIES);
    try {
      propertyCache.importProperties(is.getPath());
    } catch (NullPointerException e) {
      System.out.printf("ERROR: Did not find properties file (%s)%n", PROPERTIES);
    }
  }

  @Test
  public void testCacheGetsValueForKnownKey() {
    assertTrue(
        ISMTestHelpers.dbTypes.contains(propertyCache.getProperty("rdbms"))
    );

  }

  @Test
  public void testCacheGetsDefaultValueForUnknownKey() {
    assertEquals("default",
        propertyCache.getProperty("xdbms", "default"));
  }

  @Test
  public void testCacheDoesNotGetDefaultValueForKnownKey() {
    assertTrue(
        ISMTestHelpers.dbTypes.contains(propertyCache.getProperty("rdbms"))
    );
  }
}