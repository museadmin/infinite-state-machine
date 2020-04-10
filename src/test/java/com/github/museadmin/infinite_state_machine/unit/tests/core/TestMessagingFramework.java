package com.github.museadmin.infinite_state_machine.unit.tests.core;

import com.github.museadmin.infinite_state_machine.ism.InfiniteStateMachine;
import com.github.museadmin.infinite_state_machine.core.action_pack.ISMCoreActionPack;
import com.github.museadmin.infinite_state_machine.unit.tests.support.TestSupportMethods;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestMessagingFramework extends TestSupportMethods {

  private Thread thread;
  private final String threadName = "UnitTestThread";

  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  @Before
  public void setup() {
    ismCoreActionPack = new ISMCoreActionPack();
    infiniteStateMachine = new InfiniteStateMachine(PROPERTIES);
    infiniteStateMachine.importActionPack(ismCoreActionPack);
  }

  @After
  public void tearDown(){
    if (infiniteStateMachine.propertyCache.getProperty("dropDb").equals("true")) {
      printDropMessage(infiniteStateMachine);
      infiniteStateMachine.dataAccessLayer.dropDatabase();
    }
  }

  @Test
  public void testInboundMsgShutsDownIsm()  throws IOException, InterruptedException {

    thread = new Thread (infiniteStateMachine, threadName);
    thread.start ();

    assertTrue(waitForRunPhase("RUNNING", 4L));

    writeMsgFile(
      String.format("junit_%s_localhost", msgId.toString()),
      createInboundMsgAsJsonObject(
        new JSONObject().put("dummy", "value"),
        "ActionNormalShutdown"
      ).toString()
    );

    assertTrue(waitForRunPhase("STOPPED", 4L));
  }

  @Test
  public void testInboundMessageWithMissingRequiredFieldIsRejected() throws IOException, InterruptedException {

    String malformedFile;

    thread = new Thread (infiniteStateMachine, threadName);
    thread.start ();

    assertTrue(waitForRunPhase("RUNNING", 4L));

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

    assertTrue(waitForRunPhase("STOPPED", 4L));

    try (Stream<Path> files = Files.list(Paths.get(infiniteStateMachine.queryProperty("msg_rejected")))) {
      assertEquals(20L, files.count());
    }
  }

}
