package com.example.app.infrastructure.repository.database;

import static com.example.app.infrastructure.jooq.Tables.USERS;

import com.example.app.domain.aggregation.user.User;
import com.example.app.domain.entity.user.Account;
import com.example.app.domain.entity.user.Profile;
import com.example.app.domain.entity.user.Role;
import com.example.app.infrastructure.jooq.tables.records.UsersRecord;
import com.example.app.port.out.user.UserRepository;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
@RequiredArgsConstructor
public class PgDatabaseUserRepository implements UserRepository {

  private final DSLContext dsl;

  @Override
  public Optional<User> findByEmail(String email) {
    return dsl.selectFrom(USERS).where(USERS.EMAIL.eq(email)).fetchOptional().map(this::toUser);
  }

  @Override
  public Optional<User> findById(Long id) {
    return dsl.selectFrom(USERS).where(USERS.USER_ID.eq(id)).fetchOptional().map(this::toUser);
  }

  @Override
  public User save(User user) {
    if (user.getUserId() == null) {
      // INSERT
      UsersRecord rec =
          dsl.insertInto(USERS)
              .set(USERS.EMAIL, user.getAccount().email())
              .set(USERS.PASSWORD_HASH, user.getAccount().passwordHash())
              .set(USERS.ROLE, user.getAccount().role().name())
              .set(USERS.IS_ACTIVE, user.getAccount().isActive())
              .set(USERS.IS_EMAIL_VERIFIED, user.getAccount().isEmailVerified())
              .set(USERS.DISPLAY_NAME, user.getProfile().displayName())
              .returning()
              .fetchOne();
      return toUser(rec);
    } else {
      // UPDATE
      dsl.update(USERS)
          .set(USERS.IS_ACTIVE, user.getAccount().isActive())
          .set(USERS.IS_EMAIL_VERIFIED, user.getAccount().isEmailVerified())
          .set(USERS.UPDATED_AT, OffsetDateTime.now(ZoneOffset.UTC))
          .where(USERS.USER_ID.eq(user.getUserId()))
          .execute();
      return user;
    }
  }

  @Override
  public boolean existsByEmail(String email) {
    return dsl.fetchExists(USERS, USERS.EMAIL.eq(email));
  }

  @Override
  public User findAdminUser() {
    return dsl.selectFrom(USERS)
        .where(USERS.ROLE.eq("ADMIN"))
        .limit(1)
        .fetchOptional()
        .map(this::toUser)
        .orElseThrow(() -> new IllegalStateException("No admin user found"));
  }

  private User toUser(UsersRecord r) {
    return User.reconstitute(
        r.getUserId(),
        new Account(
            r.getEmail(),
            r.getPasswordHash(),
            Role.valueOf(r.getRole()),
            r.getIsActive(),
            r.getIsEmailVerified()),
        new Profile(r.getDisplayName()),
        r.getVersion());
  }
}
