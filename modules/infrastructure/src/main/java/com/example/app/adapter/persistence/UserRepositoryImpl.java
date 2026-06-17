package com.example.app.adapter.persistence;

import static com.example.app.jooq.tables.Users.USERS;

import com.example.app.domain.entity.User;
import com.example.app.port.out.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

  private final DSLContext dsl;

  @Override
  public Optional<User> findByEmail(String email) {
    return dsl.selectFrom(USERS)
        .where(USERS.EMAIL.eq(email))
        .fetchOptional()
        .map(
            r ->
                User.builder()
                    .id(r.getId())
                    .email(r.getEmail())
                    .passwordHash(r.getPasswordHash())
                    .role(r.getRole())
                    .build());
  }

  @Override
  public User save(User user) {
    var record =
        dsl.insertInto(USERS)
            .set(USERS.EMAIL, user.getEmail())
            .set(USERS.PASSWORD_HASH, user.getPasswordHash())
            .set(USERS.ROLE, user.getRole())
            .returning()
            .fetchOne();

    return User.builder()
        .id(record.getId())
        .email(record.getEmail())
        .passwordHash(record.getPasswordHash())
        .role(record.getRole())
        .build();
  }
}
