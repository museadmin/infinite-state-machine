package com.github.museadmin.infinite_state_machine.test.core;

import com.github.museadmin.infinite_state_machine.core.InfiniteStateMachine;
import com.github.museadmin.infinite_state_machine.test.support.CommonSupportMethods;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class TestInfiniteStateMachine {

  protected InfiniteStateMachine infiniteStateMachine;

  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  @Before
  public void setup() {
    infiniteStateMachine = new InfiniteStateMachine();
  }

  @Test
  public void testInfiniteStateMachineReadsRdbms() {
    assertTrue(infiniteStateMachine.getRdbms().equalsIgnoreCase("sqlite3") ||
      infiniteStateMachine.getRdbms().equalsIgnoreCase("postgres"));
  }

  @Test
  public void testInfiniteStateMachineImportsThirdPartyProperties() {
      String tmpProps = CommonSupportMethods.createTmpPropertiesFile(tmpFolder);
      InfiniteStateMachine ism = new InfiniteStateMachine(tmpProps);
      assertEquals(ism.getRdbms(), "sqlite3");
  }

  @Test
  public void setInfiniteStateMachineExecutesActions() {
    infiniteStateMachine.execute();
  }

}
