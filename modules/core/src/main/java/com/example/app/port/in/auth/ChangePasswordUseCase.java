package com.example.app.port.in.auth;

public interface ChangePasswordUseCase {
  void execute(Long userId, String oldPassword, String newPassword);
}
