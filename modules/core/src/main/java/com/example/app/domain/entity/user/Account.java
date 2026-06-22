package com.example.app.domain.entity.user;

public record Account(
    String email, String passwordHash, Role role, boolean isActive, boolean isEmailVerified) {}
