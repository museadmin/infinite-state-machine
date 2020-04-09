package com.github.museadmin.infinite_state_machine.unit.tests.common;

import com.github.museadmin.infinite_state_machine.core.action_pack.ISMCoreActionPack;
import com.github.museadmin.infinite_state_machine.ism.InfiniteStateMachine;
import com.github.museadmin.infinite_state_machine.unit.tests.support.TestSupportMethods;
import org.json.JSONObject;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
    infiniteStateMachine = new InfiniteStateMachine("infinite_state_machine.properties");
    infiniteStateMachine.importActionPack(ismCoreActionPack);
  }

  @Test
  public void testCoreTablesCreated() {
    boolean tableFound = false;
    ArrayList <JSONObject> results = infiniteStateMachine.getDbTables();
    for (JSONObject result : results) {
      switch (infiniteStateMachine.getRdbms().toUpperCase()) {
        case "SQLITE3":
          if (result.getString("name").equalsIgnoreCase("messages")) {
            tableFound = true;
          }
          break;
        case "MYSQL":
          if (result.getString("TABLE_NAME").equalsIgnoreCase("messages")) {
            tableFound = true;
          }
          break;
        default:
          fail("Failed to read RDBMS");
      }

    }
    assertTrue(tableFound);
  }

}
