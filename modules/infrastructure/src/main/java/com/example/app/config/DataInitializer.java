package com.example.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

  private final JdbcTemplate jdbcTemplate;
  private final PasswordEncoder passwordEncoder;

  @Value("${app.admin.seed-password:Admin@123}")
  private String adminSeedPassword;

  @Value("${app.admin.email:admin@app.com}")
  private String adminEmail;

  public DataInitializer(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
    this.jdbcTemplate = jdbcTemplate;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(ApplicationArguments args) {
    jdbcTemplate.update(
        "UPDATE users SET password_hash = ? WHERE email = ? AND password_hash = 'PLACEHOLDER_WILL_BE_REPLACED'",
        passwordEncoder.encode(adminSeedPassword),
        adminEmail);
  }
}
