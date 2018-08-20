package com.github.museadmin.infinite_state_machine.unit.tests.core;

import com.github.museadmin.infinite_state_machine.classes.ISMTestActionPack;
import com.github.museadmin.infinite_state_machine.core.InfiniteStateMachine;
import com.github.museadmin.infinite_state_machine.core.action_pack.ISMCoreActionPack;
import com.github.museadmin.infinite_state_machine.unit.tests.support.TestSupportMethods;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestInfiniteStateMachine {

  protected InfiniteStateMachine infiniteStateMachine;

  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  @Before
  public void setup() {
    ISMCoreActionPack ismCoreActionPack = new ISMCoreActionPack();
    infiniteStateMachine = new InfiniteStateMachine();
    infiniteStateMachine.importActionPack(ismCoreActionPack);
  }

  @Test
  public void testInfiniteStateMachineReadsRdbms() {
    assertTrue(infiniteStateMachine.getRdbms().equalsIgnoreCase("sqlite3") ||
      infiniteStateMachine.getRdbms().equalsIgnoreCase("postgres"));
  }

  @Test
  public void testInfiniteStateMachineImportsThirdPartyProperties() {
      String tmpProps = TestSupportMethods.createTmpPropertiesFile(tmpFolder);
      InfiniteStateMachine ism = new InfiniteStateMachine(tmpProps);
      assertEquals(ism.getRdbms(), "sqlite3");
  }

  @Test
  public void testIsmImportsTestActionPack() {
    ISMTestActionPack ismTestActionPack = new ISMTestActionPack();
    infiniteStateMachine.importActionPack(ismTestActionPack);
  }

}
