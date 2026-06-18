package com.example.app.adapter.persistence;

import static com.example.app.jooq.tables.Users.USERS;

import com.example.app.domain.entity.User;
import com.example.app.domain.value.Email;
import com.example.app.domain.value.PasswordHash;
import com.example.app.domain.value.Role;
import com.example.app.domain.value.UserId;
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
  public Optional<User> findByEmail(Email email) {
    return dsl.selectFrom(USERS)
        .where(USERS.EMAIL.eq(email.value()))
        .fetchOptional()
        .map(
            r ->
                User.builder()
                    .id(new UserId(r.getId()))
                    .email(new Email(r.getEmail()))
                    .passwordHash(new PasswordHash(r.getPasswordHash()))
                    .role(Role.valueOf(r.getRole()))
                    .build());
  }

  @Override
  public User save(User user) {
    var record =
        dsl.insertInto(USERS)
            .set(USERS.EMAIL, user.getEmail().value())
            .set(USERS.PASSWORD_HASH, user.getPasswordHash().value())
            .set(USERS.ROLE, user.getRole().name())
            .returning()
            .fetchOne();

    return User.builder()
        .id(new UserId(record.getId()))
        .email(new Email(record.getEmail()))
        .passwordHash(new PasswordHash(record.getPasswordHash()))
        .role(Role.valueOf(record.getRole()))
        .build();
  }
}
