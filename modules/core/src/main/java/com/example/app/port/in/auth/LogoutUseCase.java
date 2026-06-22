package com.example.app.port.in.auth;

public interface LogoutUseCase {
  void execute(String refreshToken);
}
