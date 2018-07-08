package com.github.museadmin.infinite_state_machine.test;

import com.github.museadmin.infinite_state_machine.core.InfiniteStateMachine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class TestInfiniteStateMachine {

  protected InfiniteStateMachine infiniteStateMachine;
  protected File file;

  @Before
  public void setup() {
    infiniteStateMachine = new InfiniteStateMachine();
  }

  @After
  public void teardown() {

    if(file != null && file.exists() && !file.isDirectory()) {
      file.delete();
    }
  }

  @Test
  public void testInfiniteStateMachineReadsRdbms() {
    assertTrue(infiniteStateMachine.getRdbms().equalsIgnoreCase("sqlite3") ||
      infiniteStateMachine.getRdbms().equalsIgnoreCase("postgres"));
  }

  @Test
  public void testInfiniteStateMachineImportsProperties() {
    try {
      file = File.createTempFile("test", ".properties");
      String tmpProps = file.getAbsolutePath();
      CommonSupportMethods.createTmpPropertiesFile(tmpProps);

      InfiniteStateMachine ism = new InfiniteStateMachine(tmpProps);
      assertEquals(ism.getRdbms(), "sqlite3");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
