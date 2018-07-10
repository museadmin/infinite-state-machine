package com.github.museadmin.infinite_state_machine.test;

import com.github.museadmin.infinite_state_machine.core.InfiniteStateMachine;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class TestInfiniteStateMachine {

  protected InfiniteStateMachine infiniteStateMachine;
  protected File file;

  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

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
      String tmpProps = tmpFolder.newFile("test.properties").getAbsolutePath();
      String tmpDir = tmpFolder.getRoot().getAbsolutePath();
      CommonSupportMethods.createTmpPropertiesFile(tmpProps, tmpDir);

      InfiniteStateMachine ism = new InfiniteStateMachine(tmpProps);
      assertEquals(ism.getRdbms(), "sqlite3");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
