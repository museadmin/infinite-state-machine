package com.github.museadmin.infinite_state_machine.unit.tests.core;

import com.github.museadmin.infinite_state_machine.core.InfiniteStateMachine;
import com.github.museadmin.infinite_state_machine.core.action_pack.ISMCoreActionPack;
import com.github.museadmin.infinite_state_machine.unit.tests.support.TestSupportMethods;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

public class TestMessagingFramework extends TestSupportMethods {

  private Thread thread;
  private String threadName = "UnitTestThread";
  private Integer msgId = 1;

  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  @Before
  public void setup() {
    ismCoreActionPack = new ISMCoreActionPack();
    infiniteStateMachine = new InfiniteStateMachine();
    infiniteStateMachine.importActionPack(ismCoreActionPack);
  }

  @Test
  public void testInboundMsgShutsDownIsm()  throws IOException, InterruptedException {

    thread = new Thread (infiniteStateMachine, threadName);
    thread.start ();

    assertTrue(waitForRunPhase("RUNNING", 2L));

    writeMsgFile(
      String.format("junit_%s_localhost", msgId.toString()),
      createInboundMsgAsJsonObject(
        new JSONObject().put("dummy", "value"),
        "ActionNormalShutdown"
      ).toString()
    );

    assertTrue(waitForRunPhase("STOPPED", 2L));
  }

  @Test
  public void testInboundMessageWithMissingRequiredFieldIsRejected() throws IOException, InterruptedException {

    String malformedFile;

    thread = new Thread (infiniteStateMachine, threadName);
    thread.start ();

    assertTrue(waitForRunPhase("RUNNING", 2L));

    for (String field : Arrays.asList(
      "action",
      "sender",
      "sender_id",
      "recipient",
      "sent"
    )) {

      // Empty action string
      malformedFile = String.format("junit_%s_localhost", msgId.toString());
      JSONObject msgObject = createInboundMsgAsJsonObjectWithEmptyField(
        new JSONObject().put("dummy", "value"),
        "ActionAbnormalShutdown",
        field
      );
      writeMsgFile(malformedFile, msgObject.toString());
      
      // No action item in json
      malformedFile = String.format("junit_%s_localhost", msgId.toString());
      msgObject = createInboundMsgAsJsonObjectWithNullField(
        new JSONObject().put("dummy", "value"),
        "ActionAbnormalShutdown",
        field
      );
      writeMsgFile(malformedFile, msgObject.toString());

    }

    writeMsgFile(
      String.format("junit_%s_localhost", msgId.toString()),
      createInboundMsgAsJsonObject(
        new JSONObject().put("dummy", "value"),
        "ActionNormalShutdown"
      ).toString()
    );

    assertTrue(waitForRunPhase("STOPPED", 2L));

    try (Stream<Path> files = Files.list(Paths.get(infiniteStateMachine.queryProperty("msg_rejected")))) {
      assertTrue(files.count() == 20L);
    }
  }

  /**
   * Create a message in JSON format and empty one of the fields then return as a JSONObject
   * @param payload A JSONObject that represents an optional payload
   * @param action The name of the target action
   * @return JSONObject representing the message
   */
  private JSONObject createInboundMsgAsJsonObjectWithEmptyField(JSONObject payload, String action, String field) {

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
   * @return JSONObject representing the message
   */
  private JSONObject createInboundMsgAsJsonObjectWithNullField(JSONObject payload, String action, String field) {

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
  private JSONObject createInboundMsgAsJsonObject(JSONObject payload, String action) {

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
   * @throws IOException
   */
  private void writeMsgFile(String fileName, String message) throws IOException {

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
