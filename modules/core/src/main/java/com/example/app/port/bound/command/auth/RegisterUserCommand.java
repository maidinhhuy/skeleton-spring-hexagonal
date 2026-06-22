package com.example.app.port.bound.command.auth;

public record RegisterUserCommand(String email, String password, String displayName) {}
