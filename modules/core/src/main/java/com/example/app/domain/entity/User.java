package com.example.app.domain.entity;

import com.example.app.domain.value.Email;
import com.example.app.domain.value.PasswordHash;
import com.example.app.domain.value.Role;
import com.example.app.domain.value.UserId;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class User {

  private UserId id;
  private Email email;
  private PasswordHash passwordHash;
  private Role role;
}
