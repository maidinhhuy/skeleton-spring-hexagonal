package com.example.app.domain.value;

public record Password(String value) {
  public Password {
    if (value == null || value.isBlank())
      throw new IllegalArgumentException("Password must not be blank");
  }
}
