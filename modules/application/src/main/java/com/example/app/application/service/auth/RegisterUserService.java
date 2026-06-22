package com.example.app.application.service.auth;

import com.example.app.domain.aggregation.user.User;
import com.example.app.domain.entity.user.Role;
import com.example.app.domain.exception.EmailConflictException;
import com.example.app.port.bound.command.auth.RegisterUserCommand;
import com.example.app.port.in.auth.RegisterUserUseCase;
import com.example.app.port.out.auth.PasswordHasher;
import com.example.app.port.out.email.EmailClient;
import com.example.app.port.out.email.EmailVerificationRepository;
import com.example.app.port.out.user.UserRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RegisterUserService implements RegisterUserUseCase {
  private final UserRepository userRepository;
  private final PasswordHasher passwordHasher;
  private final EmailVerificationRepository emailVerificationRepository;
  private final EmailClient emailClient;

  @Override
  public User execute(RegisterUserCommand cmd) {
    if (userRepository.existsByEmail(cmd.email())) {
      throw new EmailConflictException(cmd.email());
    }
    String hash = passwordHasher.hash(cmd.password());
    User user = User.create(cmd.email(), hash, Role.CUSTOMER, cmd.displayName());
    User saved = userRepository.save(user);

    String token = UUID.randomUUID().toString();
    Instant expiresAt = Instant.now().plusSeconds(86400);
    emailVerificationRepository.save(saved.getUserId(), token, expiresAt);
    emailClient.sendVerificationEmail(cmd.email(), token);
    return saved;
  }
}
