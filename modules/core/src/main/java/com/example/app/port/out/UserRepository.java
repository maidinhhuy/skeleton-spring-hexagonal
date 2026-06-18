package com.example.app.port.out;

import com.example.app.domain.entity.User;
import com.example.app.domain.value.Email;
import java.util.Optional;

public interface UserRepository {
  Optional<User> findByEmail(Email email);

  User save(User user);
}
