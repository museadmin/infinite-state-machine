package com.github.museadmin.infinite_state_machine.data.access.utils;

public class InvalidRunPhase extends RuntimeException {
  public InvalidRunPhase(String message) {
    super(message);
  }
}
