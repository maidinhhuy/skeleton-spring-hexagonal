package com.example.app.domain.exception;

public class EmailConflictException extends RuntimeException {
  public EmailConflictException(String email) {
    super("Email already registered: " + email);
  }
}
