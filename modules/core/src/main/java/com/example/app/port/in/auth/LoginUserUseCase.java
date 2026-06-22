package com.example.app.port.in.auth;

import com.example.app.domain.entity.auth.AccessToken;
import com.example.app.port.bound.command.auth.LoginUserCommand;

public interface LoginUserUseCase {
  AccessToken execute(LoginUserCommand cmd);
}
