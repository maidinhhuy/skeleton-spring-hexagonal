package com.example.app.port.out;

import com.example.app.domain.entity.User;
import java.util.Optional;

public interface UserRepository {
  Optional<User> findByEmail(String email);

  User save(User user);
}
