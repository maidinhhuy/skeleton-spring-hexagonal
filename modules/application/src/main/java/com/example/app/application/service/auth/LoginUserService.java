package com.example.app.application.service.auth;

import com.example.app.domain.aggregation.user.User;
import com.example.app.domain.entity.auth.AccessToken;
import com.example.app.domain.exception.AuthenticationException;
import com.example.app.port.bound.command.auth.LoginUserCommand;
import com.example.app.port.in.auth.LoginUserUseCase;
import com.example.app.port.out.auth.PasswordHasher;
import com.example.app.port.out.email.TokenProvider;
import com.example.app.port.out.email.TokenRepository;
import com.example.app.port.out.user.UserRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginUserService implements LoginUserUseCase {

  private final UserRepository userRepository;
  private final PasswordHasher passwordHasher;
  private final TokenProvider tokenProvider;
  private final TokenRepository tokenRepository;

  @Override
  @Transactional
  public AccessToken execute(LoginUserCommand cmd) {
    User user =
        userRepository
            .findByEmail(cmd.email())
            .orElseThrow(
                () ->
                    new AuthenticationException(
                        AuthenticationException.Type.INVALID_CREDENTIALS, "Invalid credentials"));

    if (!passwordHasher.matches(cmd.password(), user.getAccount().passwordHash())) {
      throw new AuthenticationException(
          AuthenticationException.Type.INVALID_CREDENTIALS, "Invalid credentials");
    }

    if (!user.getAccount().isEmailVerified()) {
      throw new AuthenticationException(
          AuthenticationException.Type.EMAIL_NOT_VERIFIED,
          "Please verify your email before logging in");
    }

    if (!user.getAccount().isActive()) {
      throw new AuthenticationException(
          AuthenticationException.Type.INVALID_CREDENTIALS, "Invalid credentials");
    }

    AccessToken token =
        tokenProvider.generateAccessToken(
            user.getUserId(), user.getAccount().email(), user.getAccount().role().name());

    tokenRepository.save(
        user.getUserId(),
        token.refreshToken(),
        Instant.now().plusSeconds(tokenProvider.refreshTokenExpirySeconds()));

    return token;
  }
}
