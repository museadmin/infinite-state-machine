package com.github.museadmin.infinite_state_machine.unit.tests.support;

import com.github.museadmin.infinite_state_machine.core.InfiniteStateMachine;
import com.github.museadmin.infinite_state_machine.core.action_pack.ISMCoreActionPack;
import org.junit.rules.TemporaryFolder;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Any common support methods applicable to the unit tests
 */
public class TestSupportMethods {

  public static InfiniteStateMachine infiniteStateMachine;
  public static ISMCoreActionPack ismCoreActionPack;

  /**
   * Create a one off test properties file
   * @param tmpFolder
   */
  public static String createTmpPropertiesFile(TemporaryFolder tmpFolder) {

    FileOutputStream output = null;
    String tmpProps = null;
    try {
      tmpProps = tmpFolder.newFile("test.properties").getAbsolutePath();
      String tmpDir = tmpFolder.getRoot().getAbsolutePath();

      output = new FileOutputStream(tmpProps);
      Properties prop = new Properties();
      prop.setProperty("rdbms", "sqlite3");
      prop.setProperty("runRoot", tmpDir);
      prop.store(output,null);

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (output != null) {
        try {
          output.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return tmpProps;
  }

  /**
   * Get the epoch milliseconds as a string
   * @return The seconds in string form
   */
  public static String epochMilliSeconds() {
    return Long.toString(System.currentTimeMillis());
  }

  /**
   * Get the epoch seconds as a string
   * @return The seconds in string form
   */
  public static String epochSecondsString() {
    return Long.toString(java.time.Instant.now().getEpochSecond());
  }

  /**
   * Get the epoch seconds as a Long
   * @return The seconds in Long form
   */
  public static Long epochSecondsLong() {
    return java.time.Instant.now().getEpochSecond();
  }

  /**
   * Wait for a particular run phase to be entered or return false if not entered within time out
   * @param phase The phase to wait for
   * @param time The time out period
   * @return Boolean. True if entered in time
   * @throws InterruptedException
   */
  public static Boolean waitForRunPhase(String phase, Long time) throws InterruptedException {

    Long startTime = java.time.Instant.now().getEpochSecond();
    Long timeOut = startTime + time;

    while(! infiniteStateMachine.queryRunPhase().equals(phase)) {
      Thread.sleep(100);
      if (java.time.Instant.now().getEpochSecond() >= timeOut) {
        return false;
      }
    }
    return true;
  }
}
