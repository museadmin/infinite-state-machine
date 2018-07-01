import junit.framework.*;

import com.github.museadmin.infinite_state_machine.lib.PropertyCache;

public class TestPropertyCache extends TestCase {

  protected PropertyCache propertyCache;

  protected void setUp(){
    propertyCache = new PropertyCache("target/classes/environment.properties");
  }

  public void testCacheGetsValueForKey() {
    assertEquals("sqlite3", propertyCache.getProperty("rdbms"));
  }

  public void testCacheGetsDefaultValueForUnknownKey() {
    assertEquals("default",
        propertyCache.getProperty("xdbms", "default"));
  }

  public void testCacheDoesNotGetDefaultValueForKnownKey() {
    assertEquals("sqlite3",
        propertyCache.getProperty("rdbms", "default"));
  }
}
