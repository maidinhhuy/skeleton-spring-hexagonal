package com.example.app.port.out.auth;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository {
  void save(Long userId, String token, Instant expiresAt);

  Optional<Long> findUserIdByToken(String token);

  void revokeByToken(String token);

  void revokeAllByUserId(Long userId);

  boolean isTokenValid(String token);
}
