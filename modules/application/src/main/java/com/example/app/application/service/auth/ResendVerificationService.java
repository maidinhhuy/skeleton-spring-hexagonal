package com.example.app.application.service.auth;

import com.example.app.domain.aggregation.user.User;
import com.example.app.domain.exception.AuthenticationException;
import com.example.app.port.in.auth.ResendVerificationUseCase;
import com.example.app.port.out.email.EmailClient;
import com.example.app.port.out.email.EmailVerificationRepository;
import com.example.app.port.out.user.UserRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ResendVerificationService implements ResendVerificationUseCase {
  private final UserRepository userRepository;
  private final EmailVerificationRepository emailVerificationRepository;
  private final EmailClient emailClient;

  @Override
  public void execute(String email) {
    Optional<User> userOpt = userRepository.findByEmail(email);
    if (userOpt.isEmpty() || userOpt.get().getAccount().isEmailVerified()) {
      return;
    }
    User user = userOpt.get();
    Long userId = user.getUserId();

    int recentCount =
        emailVerificationRepository.countRecentByUserId(userId, Instant.now().minusSeconds(3600));
    if (recentCount >= 3) {
      throw new AuthenticationException(
          AuthenticationException.Type.TOO_MANY_REQUESTS,
          "Too many verification emails. Please wait before requesting again.");
    }

    emailVerificationRepository.deleteByUserId(userId);
    String token = UUID.randomUUID().toString();
    emailVerificationRepository.save(userId, token, Instant.now().plusSeconds(86400));
    emailClient.sendVerificationEmail(email, token);
  }
}
