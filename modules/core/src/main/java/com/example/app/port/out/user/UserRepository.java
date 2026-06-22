package com.example.app.port.out.user;

import com.example.app.domain.aggregation.user.User;
import java.util.Optional;

public interface UserRepository {
  Optional<User> findByEmail(String email);

  Optional<User> findById(Long id);

  User save(User user);

  boolean existsByEmail(String email);

  User findAdminUser();
}
