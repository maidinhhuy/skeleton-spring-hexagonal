package com.example.app.application.service.user;

import com.example.app.domain.aggregation.user.User;
import com.example.app.domain.exception.UserNotFoundException;
import com.example.app.port.in.user.GetUserProfileUseCase;
import com.example.app.port.out.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserProfileService implements GetUserProfileUseCase {
  private final UserRepository userRepository;

  @Override
  public User execute(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId.toString()));
  }
}
