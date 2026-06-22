package com.example.app.port.out.auth;

import java.time.Instant;

public interface LoginAttemptRepository {
  void save(String email, String ipAddress, boolean success, String failureReason);

  int countFailures(String email, Instant since);
}
