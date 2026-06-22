package com.example.app.infrastructure.email;

import com.example.app.domain.entity.email.EmailMessage;
import com.example.app.port.out.email.EmailClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DevEmailClient implements EmailClient {

  @Override
  public void send(EmailMessage message) {
    log.info("[EMAIL-DEV] to={} subject={}", message.to(), message.subject());
  }

  @Override
  public void sendVerificationEmail(String email, String token) {
    log.info("[EMAIL-DEV] sendVerificationEmail to={} token={}", email, token);
  }
}
