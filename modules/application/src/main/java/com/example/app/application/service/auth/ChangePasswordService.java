package com.example.app.application.service.auth;

import com.example.app.domain.aggregation.user.User;
import com.example.app.domain.exception.AuthenticationException;
import com.example.app.port.in.auth.ChangePasswordUseCase;
import com.example.app.port.out.auth.PasswordHasher;
import com.example.app.port.out.email.TokenRepository;
import com.example.app.port.out.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChangePasswordService implements ChangePasswordUseCase {
  private final UserRepository userRepository;
  private final PasswordHasher passwordHasher;
  private final TokenRepository tokenRepository;

  @Override
  public void execute(Long userId, String oldPassword, String newPassword) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () ->
                    new AuthenticationException(
                        AuthenticationException.Type.ACCOUNT_NOT_FOUND, "User not found"));
    if (!passwordHasher.matches(oldPassword, user.getAccount().passwordHash())) {
      throw new AuthenticationException(
          AuthenticationException.Type.WRONG_PASSWORD, "Current password is incorrect");
    }
    user.changePassword(passwordHasher.hash(newPassword));
    userRepository.save(user);
    tokenRepository.revokeAllForUser(userId);
  }
}
