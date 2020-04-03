package com.github.museadmin.infinite_state_machine.unit.tests.core;

import com.github.museadmin.infinite_state_machine.ism.InfiniteStateMachine;
import com.github.museadmin.infinite_state_machine.core.action_pack.ISMCoreActionPack;
import com.github.museadmin.infinite_state_machine.ism.ISMTestActionPack;
import com.github.museadmin.infinite_state_machine.unit.tests.support.TestSupportMethods;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.net.URL;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestInfiniteStateMachine extends TestSupportMethods {

  protected InfiniteStateMachine infiniteStateMachine;

  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  @Before
  public void setup() {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    URL is = loader.getResource(PROPERTIES);
    ISMCoreActionPack ismCoreActionPack = new ISMCoreActionPack();
    infiniteStateMachine = new InfiniteStateMachine(is.getPath());
    infiniteStateMachine.importActionPack(ismCoreActionPack);
  }

  @Test
  public void testInfiniteStateMachineReadsRdbms() {
    assertTrue(
        infiniteStateMachine.getRdbms().equalsIgnoreCase("sqlite3") ||
            infiniteStateMachine.getRdbms().equalsIgnoreCase("mysql")
    );
  }

  @Test
  public void testInfiniteStateMachineImportsProperties() {
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
