package com.example.app.config;

import com.example.app.application.service.RegisterUserService;
import com.example.app.port.in.RegisterUserUseCase;
import com.example.app.port.out.PasswordHasher;
import com.example.app.port.out.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

  @Bean
  public RegisterUserUseCase registerUserUseCase(
      UserRepository userRepository, PasswordHasher passwordHasher) {
    return new RegisterUserService(userRepository, passwordHasher);
  }
}
