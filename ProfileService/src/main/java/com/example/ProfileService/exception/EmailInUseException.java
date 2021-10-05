package com.example.ProfileService.exception;

public class EmailInUseException extends RuntimeException {

    public EmailInUseException(String email) {
        super("Email already used : " + email);
    }
}
