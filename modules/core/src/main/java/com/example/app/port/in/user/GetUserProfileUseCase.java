package com.example.app.port.in.user;

import com.example.app.domain.aggregation.user.User;

public interface GetUserProfileUseCase {
  User execute(Long userId);
}
