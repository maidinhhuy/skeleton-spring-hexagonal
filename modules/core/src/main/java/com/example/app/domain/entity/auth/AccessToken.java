package com.example.app.domain.entity.auth;

public record AccessToken(
    Long userId,
    String accessToken,
    String refreshToken,
    long accessTokenExpiry,
    long refreshTokenExpiry) {}
