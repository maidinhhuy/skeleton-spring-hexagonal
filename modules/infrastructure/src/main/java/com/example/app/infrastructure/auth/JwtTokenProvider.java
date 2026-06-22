package com.example.app.infrastructure.auth;

import com.example.app.domain.entity.auth.AccessToken;
import com.example.app.domain.entity.auth.RefreshToken;
import com.example.app.port.out.email.TokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider implements TokenProvider {

  private final SecretKey secretKey;
  private final JwtProperties props;

  public JwtTokenProvider(JwtProperties props) {
    this.props = props;
    this.secretKey = Keys.hmacShaKeyFor(decodeSecret(props.getSecret()));
  }

  @Override
  public AccessToken generateAccessToken(Long userId, String email, String role) {
    String accessToken =
        Jwts.builder()
            .subject(String.valueOf(userId))
            .claim("email", email)
            .claim("role", role)
            .issuedAt(new Date())
            .expiration(Date.from(Instant.now().plusSeconds(props.getAccessTokenExpiry())))
            .signWith(secretKey)
            .compact();
    RefreshToken refreshToken = generateRefreshToken(userId);
    return new AccessToken(
        userId,
        accessToken,
        refreshToken.token(),
        props.getAccessTokenExpiry(),
        props.getRefreshTokenExpiry());
  }

  @Override
  public RefreshToken generateRefreshToken(Long userId) {
    String token = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
    Instant expiresAt = Instant.now().plusSeconds(props.getRefreshTokenExpiry());
    return new RefreshToken(userId, token, expiresAt, false);
  }

  @Override
  public long refreshTokenExpirySeconds() {
    return props.getRefreshTokenExpiry();
  }

  public Claims parseAccessToken(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
  }

  public boolean isTokenExpired(String token) {
    try {
      parseAccessToken(token);
      return false;
    } catch (ExpiredJwtException e) {
      return true;
    }
  }

  private static byte[] decodeSecret(String secret) {
    if (secret == null || secret.isBlank()) {
      throw new IllegalStateException("JWT secret must be configured");
    }
    try {
      return Decoders.BASE64.decode(secret);
    } catch (DecodingException ignored) {
      return Decoders.BASE64URL.decode(secret);
    }
  }
}
