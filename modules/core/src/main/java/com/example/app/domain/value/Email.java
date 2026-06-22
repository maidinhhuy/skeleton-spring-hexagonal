package com.example.app.domain.value;

public record Email(String value) {
  public Email {
    if (value == null || !value.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
      throw new IllegalArgumentException("Invalid email: " + value);
    value = value.toLowerCase();
  }
}
