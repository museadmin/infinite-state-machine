package com.github.museadmin.infinite_state_machine.test.core;

import com.github.museadmin.infinite_state_machine.core.InfiniteStateMachine;
import com.github.museadmin.infinite_state_machine.core.action_pack.ISMCoreActionPack;
import com.github.museadmin.infinite_state_machine.test.support.TestSupportMethods;
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
      getInboundMsgAsJsonObject(
        new JSONObject().put("dummy", "value"),
        "ActionNormalShutdown"
      ).toString()
    );

    assertTrue(waitForRunPhase("STOPPED", 2L));
  }

  @Test
  public void testCorruptInboundMessageIsRejected() throws IOException, InterruptedException {
    thread = new Thread (infiniteStateMachine, threadName);
    thread.start ();

    assertTrue(waitForRunPhase("RUNNING", 2L));
    String malformedFile = String.format("junit_%s_localhost", msgId.toString());
    writeMsgFile(
      malformedFile,
      getInboundMsgAsJsonObject(
        new JSONObject().put("dummy", "value"),
        ""
      ).toString()
    );

    writeMsgFile(
      String.format("junit_%s_localhost", msgId.toString()),
      getInboundMsgAsJsonObject(
        new JSONObject().put("dummy", "value"),
        "ActionNormalShutdown"
      ).toString()
    );

    assertTrue(waitForRunPhase("STOPPED", 2L));

    String msgRejected = infiniteStateMachine.queryProperty("msg_rejected");
    File rejected = new File(String.format("%s%s%s.msg", msgRejected, File.separator, malformedFile));
    assertTrue(rejected.exists());
  }

  /**
   * Create a message in JSON format and return as a JSONObject
   * @param payload A JSONObject that represents an optional payload
   * @param action The name of the target action
   * @return JSONObject representing the message
   */
  private JSONObject getInboundMsgAsJsonObject(JSONObject payload, String action) {

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
