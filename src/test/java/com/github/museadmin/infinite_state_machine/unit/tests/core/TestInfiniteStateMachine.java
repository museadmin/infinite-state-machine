package com.github.museadmin.infinite_state_machine.unit.tests.core;

import com.github.museadmin.infinite_state_machine.ism.ISMTestHelpers;
import com.github.museadmin.infinite_state_machine.ism.InfiniteStateMachine;
import com.github.museadmin.infinite_state_machine.core.action_pack.ISMCoreActionPack;
import com.github.museadmin.infinite_state_machine.unit.tests.support.TestSupportMethods;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestInfiniteStateMachine extends TestSupportMethods {

  protected InfiniteStateMachine infiniteStateMachine;
  final static Boolean DELETE_DB_IN_CLEANUP = true;

  @Rule
  public final TemporaryFolder tmpFolder = new TemporaryFolder();

  @Before
  public void setUp() {
    ISMCoreActionPack ismCoreActionPack = new ISMCoreActionPack();
    infiniteStateMachine = new InfiniteStateMachine(PROPERTIES);
    infiniteStateMachine.importActionPack(ismCoreActionPack);
  }

  @After
  public void tearDown(){
    String dbName = infiniteStateMachine.propertyCache.getProperty("dbName");
    System.out.println(dbName);
  }

  @Test
  public void testInfiniteStateMachineReadsRdbms() {
    assertTrue(
        infiniteStateMachine.getRdbms().equalsIgnoreCase("sqlite3") ||
            infiniteStateMachine.getRdbms().equalsIgnoreCase("mysql")
    );
  }

  @Test
  public void testInfiniteStateMachineImportsQualifiedProperties() {
    // Helper method expects properties as JSONObject
    JSONObject properties = new JSONObject();
    properties.put("rdbms", "sqlite3");
    properties.put("dbName", "ism.db");
    properties.put("dbPath", "/control/database");
    properties.put("runRoot", tmpFolder.getRoot().getAbsolutePath());
    String tmpProps = TestSupportMethods.createTmpPropertiesFile(properties);

    InfiniteStateMachine ism = new InfiniteStateMachine(tmpProps);
    assertEquals(ism.getRdbms(), "sqlite3");
  }

  @Test
  public void testInfiniteStateMachineImportsUnqualifiedProperties() {
    InfiniteStateMachine ism = new InfiniteStateMachine("infinite_state_machine.properties");
    assertTrue(ISMTestHelpers.dbTypes.contains(ism.getRdbms()));
  }

  @Test
  public void testInfiniteStateMachineImportsDefaultProperties1() {
    InfiniteStateMachine ism = new InfiniteStateMachine("");
    assertTrue(ISMTestHelpers.dbTypes.contains(ism.getRdbms()));
  }

}
