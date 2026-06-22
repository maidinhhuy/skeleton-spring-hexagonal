package com.example.app.application.service.auth;

import com.example.app.domain.aggregation.user.User;
import com.example.app.domain.exception.AuthenticationException;
import com.example.app.port.in.auth.VerifyEmailUseCase;
import com.example.app.port.out.email.EmailVerificationRepository;
import com.example.app.port.out.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class VerifyEmailService implements VerifyEmailUseCase {
  private final UserRepository userRepository;
  private final EmailVerificationRepository emailVerificationRepository;

  @Override
  public void execute(String token) {
    Long userId =
        emailVerificationRepository
            .findUserIdByToken(token)
            .orElseThrow(
                () ->
                    new AuthenticationException(
                        AuthenticationException.Type.INVALID_TOKEN,
                        "Invalid or expired verification token"));
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () ->
                    new AuthenticationException(
                        AuthenticationException.Type.ACCOUNT_NOT_FOUND,
                        "User not found for verification token"));
    user.activate();
    userRepository.save(user);
    emailVerificationRepository.deleteByUserId(userId);
  }
}
