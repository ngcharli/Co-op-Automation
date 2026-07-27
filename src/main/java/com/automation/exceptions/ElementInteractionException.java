package com.automation.exceptions;

// Thrown when an element cannot be interacted with or found after explicit wait
public class ElementInteractionException extends RuntimeException {
  public ElementInteractionException(String message, Throwable cause) {
    super(message, cause);
  }
}