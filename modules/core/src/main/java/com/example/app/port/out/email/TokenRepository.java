package com.example.app.port.out.email;

import com.example.app.domain.entity.auth.RefreshToken;
import java.time.Instant;
import java.util.Optional;

public interface TokenRepository {
  void save(Long userId, String token, Instant expiresAt);

  Optional<RefreshToken> findByToken(String token);

  void revoke(String token);

  void revokeAllForUser(Long userId);
}
