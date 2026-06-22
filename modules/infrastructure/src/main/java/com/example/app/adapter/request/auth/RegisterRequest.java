package com.example.app.adapter.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank String email,
    @NotBlank @Size(min = 8) String password,
    @NotBlank String displayName) {
  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public String getDisplayName() {
    return displayName;
  }
}
