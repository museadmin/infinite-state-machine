package com.github.museadmin.infinite_state_machine.unit.tests.support;

import com.github.museadmin.infinite_state_machine.ism.InfiniteStateMachine;
import com.github.museadmin.infinite_state_machine.core.action_pack.ISMCoreActionPack;
import org.json.JSONObject;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.util.Properties;

/**
 * Any common support methods applicable to the unit tests
 */
public class TestSupportMethods {

  public static InfiniteStateMachine infiniteStateMachine;
  public static ISMCoreActionPack ismCoreActionPack;
  public Integer msgId = 1;
  public final String PROPERTIES = "infinite_state_machine.properties";

  // Common Test Methods

  /**
   * Create a one off test properties file
   * @param tmpFolder The tmp folder for the test
   * @return String Path to tmp property file
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
   * @throws InterruptedException Thrown if thread is interrupted
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

  // Messaging Test Methods

  /**
   * Create a message in JSON format and empty one of the fields then return as a JSONObject
   * @param payload A JSONObject that represents an optional payload
   * @param action The name of the target action
   * @param field The name of the field we want empty for the test
   * @return JSONObject representing the message
   */
  public JSONObject createInboundMsgAsJsonObjectWithEmptyField(JSONObject payload, String action, String field) {

    JSONObject msg = new JSONObject()
      .put("sender", "junit")
      .put("sender_id", (msgId++).toString())
      .put("recipient", "localhost")
      .put("action", action)
      .put("payload", payload)
      .put("sent", epochSecondsString())
      .put("received", "")
      .put("direction", "in")
      .put("processed", "0");
    msg.put(field, "");
    return msg;
  }

  /**
   * Create a message in JSON format and null one of the fields then return as a JSONObject
   * @param payload A JSONObject that represents an optional payload
   * @param action The name of the target action
   * @param field The name of the field we want null for the test
   * @return JSONObject representing the message
   */
  public JSONObject createInboundMsgAsJsonObjectWithNullField(JSONObject payload, String action, String field) {

    JSONObject msg = new JSONObject()
      .put("sender", "junit")
      .put("sender_id", (msgId++).toString())
      .put("recipient", "localhost")
      .put("action", action)
      .put("payload", payload)
      .put("sent", epochSecondsString())
      .put("received", "")
      .put("direction", "in")
      .put("processed", "0");
    msg.remove(field);
    return msg;
  }

  /**
   * Create a message in JSON format and return as a JSONObject
   * @param payload A JSONObject that represents an optional payload
   * @param action The name of the target action
   * @return JSONObject representing the message
   */
  public JSONObject createInboundMsgAsJsonObject(JSONObject payload, String action) {

    return new JSONObject()
      .put("sender", "junit")
      .put("sender_id", (msgId++).toString())
      .put("recipient", "localhost")
      .put("action", action)
      .put("payload", payload)
      .put("sent", epochSecondsString())
      .put("received", "")
      .put("direction", "in")
      .put("processed", "0");
  }

  /**
   * Write a message to file
   * @param message JSON String contains message
   * @param fileName Name of the test file
   * @throws IOException If we fail to write
   */
  public void writeMsgFile(String fileName, String message) throws IOException {

    String inbound = infiniteStateMachine.queryProperty("msg_in");

    String msg_file = inbound + File.separator + fileName + ".msg";
    String semaphore = inbound + File.separator + fileName + ".smp";

    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
      new FileOutputStream(msg_file), "utf-8"))) {
      writer.write(message);
    }

    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
      new FileOutputStream(semaphore), "utf-8"))) {
      writer.write("");
    }
  }

}
