package com.example.app.domain.exception;

public class AuthenticationException extends RuntimeException {
  public enum Type {
    ACCOUNT_NOT_FOUND,
    INVALID_CREDENTIALS,
    TOO_MANY_REQUESTS,
    EMAIL_NOT_VERIFIED,
    TOKEN_EXPIRED,
    INVALID_TOKEN,
    WRONG_PASSWORD
  }

  private final Type type;

  public AuthenticationException(Type type, String message) {
    super(message);
    this.type = type;
  }

  public Type getType() {
    return type;
  }
}
