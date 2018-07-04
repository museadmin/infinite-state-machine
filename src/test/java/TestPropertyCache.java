import com.github.museadmin.infinite_state_machine.lib.PropertyCache;
import org.junit.Before;
import org.junit.Test;

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
}
