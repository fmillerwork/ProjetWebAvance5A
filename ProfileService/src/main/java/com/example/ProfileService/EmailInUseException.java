package com.example.ProfileService;

public class EmailInUseException extends RuntimeException {

    public EmailInUseException(String email) {
        super("Email already used : " + email);
    }
}
