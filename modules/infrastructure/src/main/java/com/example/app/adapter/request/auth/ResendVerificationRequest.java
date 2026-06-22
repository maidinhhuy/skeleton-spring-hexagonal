package com.example.app.adapter.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendVerificationRequest(@NotBlank @Email String email) {}
