package com.automation.exceptions;

// Thrown when environment/config files are missing or broken
public class FrameworkConfigException extends RuntimeException {
  public FrameworkConfigException(String message) {
    super(message);
  }
}
