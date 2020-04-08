package com.github.museadmin.infinite_state_machine.unit.tests.common;

import com.github.museadmin.infinite_state_machine.core.action_pack.ISMCoreActionPack;
import com.github.museadmin.infinite_state_machine.ism.InfiniteStateMachine;
import com.github.museadmin.infinite_state_machine.unit.tests.support.TestSupportMethods;
import org.json.JSONObject;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import java.util.ArrayList;

/**
 * Tests for the Sqlite3 RDBMS
 */
public class TestDAL extends TestSupportMethods {

  protected InfiniteStateMachine infiniteStateMachine;

  @Rule
  public final TemporaryFolder tmpFolder = new TemporaryFolder();

  @Before
  public void setup() {
    ISMCoreActionPack ismCoreActionPack = new ISMCoreActionPack();
    infiniteStateMachine = new InfiniteStateMachine("db_test.properties");
    infiniteStateMachine.importActionPack(ismCoreActionPack);
  }

  @Test
  public void testCoreTablesCreated() {
    boolean tableFound = false;
    ArrayList <JSONObject> results = infiniteStateMachine.getDbTables();
    for (JSONObject result : results) {
      if (result.getString("name").equalsIgnoreCase("messages")) {
        tableFound = true;
      }
    }
    assertTrue(tableFound);
  }

}
