package com.example.app.port.bound;

import com.example.app.domain.value.Email;
import com.example.app.domain.value.Password;

public record RegisterUserCommand(Email email, Password password) {}
