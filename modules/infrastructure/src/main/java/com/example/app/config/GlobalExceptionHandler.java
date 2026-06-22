package com.example.app.config;

import com.example.app.domain.exception.AuthenticationException;
import com.example.app.domain.exception.EmailConflictException;
import com.example.app.domain.exception.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  private final ObjectMapper objectMapper;

  public GlobalExceptionHandler(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ObjectNode> handleDomainAuthentication(
      AuthenticationException ex, HttpServletRequest request) {
    if (ex.getType() == AuthenticationException.Type.WRONG_PASSWORD) {
      return buildError("WRONG_PASSWORD", ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }
    if (ex.getType() == AuthenticationException.Type.TOO_MANY_REQUESTS) {
      return buildError(
          "TOO_MANY_REQUESTS", ex.getMessage(), HttpStatus.TOO_MANY_REQUESTS, request);
    }
    if (ex.getType() == AuthenticationException.Type.EMAIL_NOT_VERIFIED) {
      return buildError("EMAIL_NOT_VERIFIED", ex.getMessage(), HttpStatus.FORBIDDEN, request);
    }
    return buildError("AUTHENTICATION_FAILED", ex.getMessage(), HttpStatus.UNAUTHORIZED, request);
  }

  @ExceptionHandler(EmailConflictException.class)
  public ResponseEntity<ObjectNode> handleEmailConflict(
      EmailConflictException ex, HttpServletRequest request) {
    return buildError("EMAIL_CONFLICT", ex.getMessage(), HttpStatus.CONFLICT, request);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ObjectNode> handleUserNotFound(
      UserNotFoundException ex, HttpServletRequest request) {
    return buildError("USER_NOT_FOUND", ex.getMessage(), HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ObjectNode> handleNoHandlerFound(
      NoHandlerFoundException ex, HttpServletRequest request) {
    return buildError(
        "NOT_FOUND", "The requested resource was not found", HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
  public ResponseEntity<ObjectNode> handleSpringAuthentication(
      org.springframework.security.core.AuthenticationException ex, HttpServletRequest request) {
    return buildError(
        "UNAUTHORIZED", "Authentication is required", HttpStatus.UNAUTHORIZED, request);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ObjectNode> handleAccessDenied(
      AccessDeniedException ex, HttpServletRequest request) {
    return buildError("FORBIDDEN", "Access is denied", HttpStatus.FORBIDDEN, request);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ObjectNode> handleBadCredentials(
      BadCredentialsException ex, HttpServletRequest request) {
    return buildError(
        "BAD_CREDENTIALS", "Invalid username or password", HttpStatus.UNAUTHORIZED, request);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ObjectNode> handleIllegalArgument(
      IllegalArgumentException ex, HttpServletRequest request) {
    return buildError(
        "BAD_REQUEST",
        ex.getMessage() != null ? ex.getMessage() : "Invalid request parameters",
        HttpStatus.BAD_REQUEST,
        request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ObjectNode> handleValidation(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    ObjectNode body = objectMapper.createObjectNode();
    body.put("error", "VALIDATION_FAILED");
    body.put("message", "Validation failed");
    body.put("status", 400);
    body.put("path", request.getRequestURI());
    body.put("timestamp", Instant.now().toString());
    String trackingId = MDC.get("trackingId");
    body.put("trackingId", trackingId != null ? trackingId : "");
    ObjectNode details = body.putObject("details");
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(fe -> details.put(fe.getField(), fe.getDefaultMessage()));
    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ObjectNode> handleGeneric(Exception ex, HttpServletRequest request) {
    log.error("Unhandled exception: {} {}", request.getMethod(), request.getRequestURI(), ex);
    return buildError(
        "INTERNAL_SERVER_ERROR",
        "An unexpected error occurred",
        HttpStatus.INTERNAL_SERVER_ERROR,
        request);
  }

  private ResponseEntity<ObjectNode> buildError(
      String errorCode, String message, HttpStatus status, HttpServletRequest request) {
    ObjectNode body = objectMapper.createObjectNode();
    body.put("error", errorCode);
    body.put("message", message);
    body.put("status", status.value());
    body.put("path", request.getRequestURI());
    body.put("timestamp", Instant.now().toString());
    String trackingId = MDC.get("trackingId");
    body.put("trackingId", trackingId != null ? trackingId : "");
    body.putObject("details");
    return new ResponseEntity<>(body, status);
  }
}
