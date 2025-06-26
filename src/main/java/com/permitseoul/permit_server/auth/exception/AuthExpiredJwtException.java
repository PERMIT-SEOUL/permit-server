package com.permitseoul.permit_server.auth.exception;

public class AuthExpiredJwtException extends RuntimeException {
  public AuthExpiredJwtException(String message) {
    super(message);
  }
}
