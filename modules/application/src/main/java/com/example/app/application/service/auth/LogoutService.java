package com.example.app.application.service.auth;

import com.example.app.port.in.auth.LogoutUseCase;
import com.example.app.port.out.email.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LogoutService implements LogoutUseCase {

  private final TokenRepository tokenRepository;

  @Override
  public void execute(String refreshToken) {
    tokenRepository.revoke(refreshToken);
  }
}
