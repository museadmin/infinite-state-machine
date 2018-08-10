package com.github.museadmin.infinite_state_machine.data.access.utils;

public class InvalidRunPhaseException extends RuntimeException {
  public InvalidRunPhaseException(String message) {
    super(message);
  }
}
