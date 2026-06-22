package com.example.app.port.out.email;

import java.time.Instant;
import java.util.Optional;

public interface EmailVerificationRepository {
  void save(Long userId, String token, Instant expiresAt);

  Optional<Long> findUserIdByToken(String token);

  void deleteByUserId(Long userId);

  int countRecentByUserId(Long userId, Instant since);
}
