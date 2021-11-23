package com.example.AuthService.exception;

public class TokenNotValidException extends RuntimeException {
    public TokenNotValidException(String token) {
        super("Token not valid: " + token);
    }
}
