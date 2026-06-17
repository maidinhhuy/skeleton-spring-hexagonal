package com.example.app.domain.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class User {

  private Long id;
  private String email;
  private String passwordHash;
  private String role;
}
