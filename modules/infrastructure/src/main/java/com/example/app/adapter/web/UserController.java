package com.example.app.adapter.web;

import com.example.app.adapter.web.dto.RegisterUserHttpRequest;
import com.example.app.adapter.web.dto.UserHttpResponse;
import com.example.app.domain.value.Email;
import com.example.app.domain.value.Password;
import com.example.app.port.bound.RegisterUserCommand;
import com.example.app.port.in.RegisterUserUseCase;
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
  public ResponseEntity<UserHttpResponse> register(@RequestBody RegisterUserHttpRequest req) {
    var cmd = new RegisterUserCommand(new Email(req.email()), new Password(req.password()));
    var res = registerUserUseCase.execute(cmd);
    return ResponseEntity.ok(
        new UserHttpResponse(res.id().value(), res.email().value(), res.role().name()));
  }
}
