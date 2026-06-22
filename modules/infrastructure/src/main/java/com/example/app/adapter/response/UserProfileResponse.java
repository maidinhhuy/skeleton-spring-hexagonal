package com.example.app.adapter.response;

import com.example.app.domain.aggregation.user.User;

public record UserProfileResponse(
    String userId, String email, String displayName, String role, boolean isEmailVerified) {

  public static UserProfileResponse from(User user) {
    return new UserProfileResponse(
        user.getUserId().toString(),
        user.getAccount().email(),
        user.getProfile().displayName(),
        user.getAccount().role().name(),
        user.getAccount().isEmailVerified());
  }
}
