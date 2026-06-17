package com.example.app.adapter.web;

import com.example.app.port.bound.RegisterUserCommand;
import com.example.app.port.bound.UserResponse;
import com.example.app.port.in.RegisterUserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {

  private final RegisterUserUseCase registerUserUseCase;

  @PostMapping("/register")
  public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserCommand cmd) {
    return ResponseEntity.ok(registerUserUseCase.execute(cmd));
  }
}
