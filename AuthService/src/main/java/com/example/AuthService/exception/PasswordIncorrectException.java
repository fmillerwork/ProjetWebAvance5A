package com.example.AuthService.exception;

public class PasswordIncorrectException extends RuntimeException {
    public PasswordIncorrectException(Long id) {
        super("Password incorrect for user: " + id);
    }
}
