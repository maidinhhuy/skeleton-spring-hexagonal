package com.example.app.port.out.email;

import com.example.app.domain.entity.email.EmailMessage;

public interface EmailClient {
  void send(EmailMessage message);

  void sendVerificationEmail(String email, String token);
}
