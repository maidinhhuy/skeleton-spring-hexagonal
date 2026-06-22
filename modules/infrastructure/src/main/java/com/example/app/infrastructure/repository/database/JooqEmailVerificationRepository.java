package com.example.app.infrastructure.repository.database;

import static com.example.app.infrastructure.jooq.Tables.EMAIL_VERIFICATIONS;

import com.example.app.port.out.email.EmailVerificationRepository;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JooqEmailVerificationRepository implements EmailVerificationRepository {

  private final DSLContext dsl;

  @Override
  public void save(Long userId, String token, Instant expiresAt) {
    dsl.insertInto(EMAIL_VERIFICATIONS)
        .set(EMAIL_VERIFICATIONS.USER_ID, userId)
        .set(EMAIL_VERIFICATIONS.TOKEN, token)
        .set(EMAIL_VERIFICATIONS.EXPIRES_AT, OffsetDateTime.ofInstant(expiresAt, ZoneOffset.UTC))
        .execute();
  }

  @Override
  public Optional<Long> findUserIdByToken(String token) {
    return dsl.select(EMAIL_VERIFICATIONS.USER_ID)
        .from(EMAIL_VERIFICATIONS)
        .where(EMAIL_VERIFICATIONS.TOKEN.eq(token))
        .and(EMAIL_VERIFICATIONS.EXPIRES_AT.gt(OffsetDateTime.now(ZoneOffset.UTC)))
        .fetchOptional()
        .map(r -> r.value1());
  }

  @Override
  public void deleteByUserId(Long userId) {
    dsl.deleteFrom(EMAIL_VERIFICATIONS).where(EMAIL_VERIFICATIONS.USER_ID.eq(userId)).execute();
  }

  @Override
  public int countRecentByUserId(Long userId, Instant since) {
    return dsl.fetchCount(
        EMAIL_VERIFICATIONS,
        EMAIL_VERIFICATIONS
            .USER_ID
            .eq(userId)
            .and(
                EMAIL_VERIFICATIONS.CREATED_AT.gt(
                    OffsetDateTime.ofInstant(since, ZoneOffset.UTC))));
  }
}
