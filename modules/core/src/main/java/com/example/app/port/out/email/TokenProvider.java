package com.example.app.port.out.email;

import com.example.app.domain.entity.auth.AccessToken;
import com.example.app.domain.entity.auth.RefreshToken;

public interface TokenProvider {
  AccessToken generateAccessToken(Long userId, String email, String role);

  RefreshToken generateRefreshToken(Long userId);

  long refreshTokenExpirySeconds();
}
