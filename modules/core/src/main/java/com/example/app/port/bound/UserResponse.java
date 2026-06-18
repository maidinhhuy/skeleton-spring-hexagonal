package com.example.app.port.bound;

import com.example.app.domain.value.Email;
import com.example.app.domain.value.Role;
import com.example.app.domain.value.UserId;

public record UserResponse(UserId id, Email email, Role role) {}
