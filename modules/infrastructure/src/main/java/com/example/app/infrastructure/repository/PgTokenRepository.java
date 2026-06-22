package com.example.app.infrastructure.repository;

import static com.example.app.infrastructure.jooq.Tables.REFRESH_TOKENS;

import com.example.app.domain.entity.auth.RefreshToken;
import com.example.app.port.out.email.TokenRepository;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PgTokenRepository implements TokenRepository {

  private final DSLContext dsl;

  @Override
  public void save(Long userId, String token, Instant expiresAt) {
    dsl.insertInto(REFRESH_TOKENS)
        .set(REFRESH_TOKENS.USER_ID, userId)
        .set(REFRESH_TOKENS.TOKEN, token)
        .set(REFRESH_TOKENS.EXPIRES_AT, expiresAt.atOffset(ZoneOffset.UTC))
        .set(REFRESH_TOKENS.REVOKED, false)
        .execute();
  }

  @Override
  public Optional<RefreshToken> findByToken(String token) {
    return dsl.selectFrom(REFRESH_TOKENS)
        .where(REFRESH_TOKENS.TOKEN.eq(token))
        .fetchOptional()
        .map(
            r ->
                new RefreshToken(
                    r.getUserId(), r.getToken(), r.getExpiresAt().toInstant(), r.getRevoked()));
  }

  @Override
  public void revoke(String token) {
    dsl.update(REFRESH_TOKENS)
        .set(REFRESH_TOKENS.REVOKED, true)
        .where(REFRESH_TOKENS.TOKEN.eq(token))
        .execute();
  }

  @Override
  public void revokeAllForUser(Long userId) {
    dsl.update(REFRESH_TOKENS)
        .set(REFRESH_TOKENS.REVOKED, true)
        .where(REFRESH_TOKENS.USER_ID.eq(userId))
        .execute();
  }
}
