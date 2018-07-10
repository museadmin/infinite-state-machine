package com.github.museadmin.infinite_state_machine.test;

import com.github.museadmin.infinite_state_machine.core.InfiniteStateMachine;
import org.junit.After;
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

  @After
  public void teardown() {

  }

  @Test
  public void testInfiniteStateMachineReadsRdbms() {
    assertTrue(infiniteStateMachine.getRdbms().equalsIgnoreCase("sqlite3") ||
      infiniteStateMachine.getRdbms().equalsIgnoreCase("postgres"));
  }

  @Test
  public void testInfiniteStateMachineImportsProperties() {
      String tmpProps = CommonSupportMethods.createTmpPropertiesFile(tmpFolder);
      InfiniteStateMachine ism = new InfiniteStateMachine(tmpProps);
      assertEquals(ism.getRdbms(), "sqlite3");
  }
}
