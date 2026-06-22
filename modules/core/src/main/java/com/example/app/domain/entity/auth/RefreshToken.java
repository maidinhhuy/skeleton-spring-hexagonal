package com.example.app.domain.entity.auth;

import java.time.Instant;

public record RefreshToken(Long userId, String token, Instant expiresAt, boolean revoked) {}
