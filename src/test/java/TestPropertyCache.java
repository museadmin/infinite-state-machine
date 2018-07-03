import com.github.museadmin.infinite_state_machine.lib.PropertyCache;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class TestPropertyCache {

  protected PropertyCache propertyCache;

  @Before
  public void setUp(){
    propertyCache = new PropertyCache("environment.properties");
  }

  @Test
  public void testCacheGetsValueForKey() {
    assertEquals("sqlite3", propertyCache.getProperty("rdbms"));
  }
  @Test
  public void testCacheGetsDefaultValueForUnknownKey() {
    assertEquals("default",
        propertyCache.getProperty("xdbms", "default"));
  }
  @Test
  public void testCacheDoesNotGetDefaultValueForKnownKey() {
    assertEquals("sqlite3",
        propertyCache.getProperty("rdbms", "default"));
  }
}
