package com.example.app.adapter.controller;

import com.example.app.adapter.request.auth.ChangePasswordRequest;
import com.example.app.adapter.request.auth.LoginRequest;
import com.example.app.adapter.request.auth.RegisterRequest;
import com.example.app.adapter.request.auth.ResendVerificationRequest;
import com.example.app.domain.entity.auth.AccessToken;
import com.example.app.port.bound.command.auth.LoginUserCommand;
import com.example.app.port.bound.command.auth.RegisterUserCommand;
import com.example.app.port.in.auth.ChangePasswordUseCase;
import com.example.app.port.in.auth.LoginUserUseCase;
import com.example.app.port.in.auth.LogoutUseCase;
import com.example.app.port.in.auth.RefreshTokenUseCase;
import com.example.app.port.in.auth.RegisterUserUseCase;
import com.example.app.port.in.auth.ResendVerificationUseCase;
import com.example.app.port.in.auth.VerifyEmailUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final RegisterUserUseCase registerUserUseCase;
  private final LoginUserUseCase loginUserUseCase;
  private final RefreshTokenUseCase refreshTokenUseCase;
  private final LogoutUseCase logoutUseCase;
  private final VerifyEmailUseCase verifyEmailUseCase;
  private final ResendVerificationUseCase resendVerificationUseCase;
  private final ChangePasswordUseCase changePasswordUseCase;

  @PostMapping("/register")
  public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest req) {
    registerUserUseCase.execute(
        new RegisterUserCommand(req.getEmail(), req.getPassword(), req.getDisplayName()));
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/login")
  public ResponseEntity<Void> login(
      @Valid @RequestBody LoginRequest req, HttpServletResponse servletResponse) {
    AccessToken token =
        loginUserUseCase.execute(new LoginUserCommand(req.getEmail(), req.getPassword()));
    return attachAuthCookies(servletResponse, token);
  }

  @PostMapping("/refresh")
  public ResponseEntity<Void> refresh(
      HttpServletRequest request, HttpServletResponse servletResponse) {
    String oldRefreshToken = extractCookie(request, "app_refresh_token");
    if (oldRefreshToken == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    AccessToken token = refreshTokenUseCase.execute(oldRefreshToken);
    return attachAuthCookies(servletResponse, token);
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(
      HttpServletRequest request, HttpServletResponse servletResponse) {
    String refreshToken = extractCookie(request, "app_refresh_token");
    if (refreshToken != null) {
      logoutUseCase.execute(refreshToken);
    }
    return clearAuthCookies(servletResponse);
  }

  @PostMapping("/verify-email")
  public ResponseEntity<Void> verifyEmail(@RequestParam("token") String token) {
    verifyEmailUseCase.execute(token);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/resend-verification")
  public ResponseEntity<Void> resendVerification(
      @Valid @RequestBody ResendVerificationRequest req) {
    resendVerificationUseCase.execute(req.email());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/change-password")
  public ResponseEntity<Void> changePassword(
      @Valid @RequestBody ChangePasswordRequest req, @AuthenticationPrincipal Long userId) {
    changePasswordUseCase.execute(userId, req.oldPassword(), req.newPassword());
    return ResponseEntity.ok().build();
  }

  private ResponseEntity<Void> attachAuthCookies(
      HttpServletResponse servletResponse, AccessToken token) {
    servletResponse.addHeader(
        HttpHeaders.SET_COOKIE,
        buildCookie("app_access_token", token.accessToken(), token.accessTokenExpiry()).toString());
    servletResponse.addHeader(
        HttpHeaders.SET_COOKIE,
        buildCookie("app_refresh_token", token.refreshToken(), token.refreshTokenExpiry())
            .toString());
    return ResponseEntity.ok().build();
  }

  private ResponseEntity<Void> clearAuthCookies(HttpServletResponse servletResponse) {
    servletResponse.addHeader(HttpHeaders.SET_COOKIE, clearCookie("app_access_token").toString());
    servletResponse.addHeader(HttpHeaders.SET_COOKIE, clearCookie("app_refresh_token").toString());
    return ResponseEntity.ok().build();
  }

  private ResponseCookie buildCookie(String name, String value, long maxAgeSecs) {
    return ResponseCookie.from(name, value).httpOnly(true).path("/").maxAge(maxAgeSecs).build();
  }

  private ResponseCookie clearCookie(String name) {
    return ResponseCookie.from(name, "").httpOnly(true).path("/").maxAge(0).build();
  }

  private String extractCookie(HttpServletRequest request, String name) {
    if (request.getCookies() == null) return null;
    for (var c : request.getCookies()) {
      if (name.equals(c.getName())) return c.getValue();
    }
    return null;
  }
}
