package com.example.app.adapter.controller;

import com.example.app.adapter.response.UserProfileResponse;
import com.example.app.domain.aggregation.user.User;
import com.example.app.port.in.user.GetUserProfileUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final GetUserProfileUseCase getUserProfileUseCase;

  @GetMapping("/me")
  public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal Long userId) {
    User user = getUserProfileUseCase.execute(userId);
    return ResponseEntity.ok(UserProfileResponse.from(user));
  }
}
