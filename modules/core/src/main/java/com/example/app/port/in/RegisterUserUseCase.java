package com.example.app.port.in;

import com.example.app.port.bound.RegisterUserCommand;
import com.example.app.port.bound.UserResponse;

public interface RegisterUserUseCase {
  UserResponse execute(RegisterUserCommand cmd);
}
