package com.github.museadmin.infinite_state_machine.test.core;

import com.github.museadmin.infinite_state_machine.core.InfiniteStateMachine;
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

import static com.github.museadmin.infinite_state_machine.test.support.CommonSupportMethods.epochSeconds;

public class TestMessagingFramework {

  protected InfiniteStateMachine infiniteStateMachine;

  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  @Before
  public void setup() {
    infiniteStateMachine = new InfiniteStateMachine();
  }

  @Test
  public void testInboundMsgShutsDownIsm()  throws IOException {

    infiniteStateMachine.execute();

    JSONObject payload = new JSONObject()
      .put("dummy", "value");

    String message = new JSONObject()
      .put("sender", "junit")
      .put("sender_id", "1")
      .put("recipient", "localhost")
      .put("action", "ActionNormalShutdown")
      .put("payload", payload)
      .put("sent", epochSeconds())
      .put("received", "")
      .put("direction", "in")
      .put("processed", "0").toString();

    writeMsgFile(message, "junit_1_localhost");

  }

  /**
   * Write a message to file
   * @param message JSON String contains message
   * @param fileName Name of the test file
   * @throws IOException
   */
  private void writeMsgFile(String message, String fileName) throws IOException {

    String inbound = infiniteStateMachine.queryProperty("msg_in");

    String msg_file = inbound + File.separator + fileName + ".msg";
    String semaphore = inbound + File.separator + fileName + ".smp";

    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
      new FileOutputStream(msg_file), "utf-8"))) {
      writer.write(message.toString());
    }

    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
      new FileOutputStream(semaphore), "utf-8"))) {
      writer.write("");
    }
  }
}
