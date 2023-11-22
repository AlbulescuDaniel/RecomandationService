package com.test.crypto.common.exceptions;

public class BadRequestException extends RuntimeException {
  public BadRequestException(String message) {
    super(message);
  }
}
