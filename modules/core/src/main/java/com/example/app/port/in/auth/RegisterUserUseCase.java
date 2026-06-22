package com.example.app.port.in.auth;

import com.example.app.domain.aggregation.user.User;
import com.example.app.port.bound.command.auth.RegisterUserCommand;

public interface RegisterUserUseCase {
  User execute(RegisterUserCommand cmd);
}
