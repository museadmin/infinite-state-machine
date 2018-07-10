package com.github.museadmin.infinite_state_machine.test.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SLF4JConfigTest {

  static Logger LOGGER = LoggerFactory.getLogger(SLF4JConfigTest.class);

  public static void main(String args[])
  {
    LOGGER.debug("hello world");
  }

}
