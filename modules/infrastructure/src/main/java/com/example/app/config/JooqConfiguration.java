package com.example.app.config;

import org.jooq.SQLDialect;
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JooqConfiguration {

  @Bean
  public DefaultConfigurationCustomizer jooqDefaultConfigurationCustomizer() {
    return c -> c.set(SQLDialect.POSTGRES);
  }
}
