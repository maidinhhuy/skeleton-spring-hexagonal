package com.example.app.adapter.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
    @NotBlank String oldPassword,
    @NotBlank
        @Size(min = 8, message = "Password must be at least 8 characters")
        @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[0-9]).+$",
            message = "Password must contain at least one uppercase letter and one number")
        String newPassword) {}
