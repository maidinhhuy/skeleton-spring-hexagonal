package com.example.app.application.service;

import com.example.app.domain.entity.User;
import com.example.app.domain.value.PasswordHash;
import com.example.app.domain.value.Role;
import com.example.app.port.bound.RegisterUserCommand;
import com.example.app.port.bound.UserResponse;
import com.example.app.port.in.RegisterUserUseCase;
import com.example.app.port.out.PasswordHasher;
import com.example.app.port.out.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegisterUserService implements RegisterUserUseCase {

  private final UserRepository userRepository;
  private final PasswordHasher passwordHasher;

  @Override
  public UserResponse execute(RegisterUserCommand cmd) {
    userRepository
        .findByEmail(cmd.email())
        .ifPresent(
            u -> {
              throw new IllegalArgumentException("Email already registered");
            });

    User user =
        User.builder()
            .email(cmd.email())
            .passwordHash(new PasswordHash(passwordHasher.hash(cmd.password().value())))
            .role(Role.USER)
            .build();

    User saved = userRepository.save(user);
    return new UserResponse(saved.getId(), saved.getEmail(), saved.getRole());
  }
}
