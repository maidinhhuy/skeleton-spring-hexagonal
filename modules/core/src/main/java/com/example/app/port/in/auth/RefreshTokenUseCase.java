package com.example.app.port.in.auth;

import com.example.app.domain.entity.auth.AccessToken;

public interface RefreshTokenUseCase {
  AccessToken execute(String refreshToken);
}
