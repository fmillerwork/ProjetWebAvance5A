package com.example.ProfileService.exception;

public class EmailInUseException extends RuntimeException{
    public EmailInUseException(){
        super("Email already used !");
    }
}
