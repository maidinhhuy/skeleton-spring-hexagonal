package com.example.app.config;

import com.example.app.infrastructure.email.DevEmailClient;
import com.example.app.infrastructure.email.ResendEmailClient;
import com.example.app.port.out.email.EmailClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class EmailConfig {

  @Bean
  @Profile("dev")
  public EmailClient devEmailClient() {
    return new DevEmailClient();
  }

  @Bean
  @Profile("!dev")
  public EmailClient resendEmailClient(
      @Value("${APP_RESEND_API_KEY:}") String apiKey,
      @Value("${app.email.from:}") String emailFrom,
      @Value("${app.frontend.base-url}") String frontendBaseUrl,
      ObjectMapper objectMapper) {
    return new ResendEmailClient(apiKey, emailFrom, frontendBaseUrl, objectMapper);
  }
}
