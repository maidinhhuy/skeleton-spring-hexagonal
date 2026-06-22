package com.example.app.infrastructure.email;

import com.example.app.domain.entity.email.EmailMessage;
import com.example.app.port.out.email.EmailClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

@Slf4j
public class ResendEmailClient implements EmailClient {

  private final String apiKey;
  private final String emailFrom;
  private final String frontendBaseUrl;
  private final ObjectMapper objectMapper;
  private final HttpClient httpClient;

  public ResendEmailClient(
      String apiKey, String emailFrom, String frontendBaseUrl, ObjectMapper objectMapper) {
    this.apiKey = apiKey;
    this.emailFrom = emailFrom;
    this.frontendBaseUrl = frontendBaseUrl;
    this.objectMapper = objectMapper;
    this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
  }

  @Async
  @Override
  public void send(EmailMessage message) {
    if (apiKey == null || apiKey.isBlank()) {
      log.info("[EMAIL-DEV] to={} subject={}", message.to(), message.subject());
      return;
    }

    try {
      Map<String, Object> payload =
          Map.of(
              "from", emailFrom,
              "to", List.of(message.to()),
              "subject", message.subject(),
              "html", message.htmlBody());

      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create("https://api.resend.com/emails"))
              .timeout(Duration.ofSeconds(5))
              .header("Authorization", "Bearer " + apiKey)
              .header("Content-Type", "application/json")
              .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
              .build();

      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() < 200 || response.statusCode() >= 300) {
        log.warn(
            "Failed to send email via Resend API: status={}, body={}",
            response.statusCode(),
            response.body());
      }
    } catch (Exception e) {
      log.warn("Failed to send email via Resend API: to={}", message.to(), e);
    }
  }

  @Override
  public void sendVerificationEmail(String email, String token) {
    String link =
        frontendBaseUrl
            + "/register/verify-email?token="
            + token
            + "&email="
            + URLEncoder.encode(email, StandardCharsets.UTF_8);
    send(
        new EmailMessage(
            email,
            "Verify your email",
            "<p>Welcome! Click below to verify your email:</p>"
                + "<p><a href=\""
                + link
                + "\">Verify Email</a></p>"
                + "<p>Expires in 24 hours.</p>"));
  }
}
