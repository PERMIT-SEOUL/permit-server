package com.permitseoul.permit_server.auth.exception;

public class AuthWrongJwtException extends RuntimeException {
  public AuthWrongJwtException(String message) {
    super(message);
  }
}
