package com.example.app.application.service.auth;

import com.example.app.domain.entity.auth.AccessToken;
import com.example.app.domain.entity.auth.RefreshToken;
import com.example.app.domain.exception.AuthenticationException;
import com.example.app.port.in.auth.RefreshTokenUseCase;
import com.example.app.port.out.email.TokenProvider;
import com.example.app.port.out.email.TokenRepository;
import com.example.app.port.out.user.UserRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService implements RefreshTokenUseCase {

  private final TokenRepository tokenRepository;
  private final TokenProvider tokenProvider;
  private final UserRepository userRepository;

  @Override
  public AccessToken execute(String oldToken) {
    RefreshToken existing =
        tokenRepository
            .findByToken(oldToken)
            .orElseThrow(
                () ->
                    new AuthenticationException(
                        AuthenticationException.Type.INVALID_TOKEN, "Invalid refresh token"));

    if (existing.revoked()) {
      throw new AuthenticationException(
          AuthenticationException.Type.INVALID_TOKEN, "Refresh token has been revoked");
    }

    if (existing.expiresAt().isBefore(Instant.now())) {
      throw new AuthenticationException(
          AuthenticationException.Type.TOKEN_EXPIRED, "Refresh token has expired");
    }

    var user =
        userRepository
            .findById(existing.userId())
            .orElseThrow(
                () ->
                    new AuthenticationException(
                        AuthenticationException.Type.ACCOUNT_NOT_FOUND, "User not found"));

    tokenRepository.revoke(oldToken);

    AccessToken newToken =
        tokenProvider.generateAccessToken(
            user.getUserId(), user.getAccount().email(), user.getAccount().role().name());

    tokenRepository.save(
        user.getUserId(),
        newToken.refreshToken(),
        Instant.now().plusSeconds(tokenProvider.refreshTokenExpirySeconds()));

    return newToken;
  }
}
