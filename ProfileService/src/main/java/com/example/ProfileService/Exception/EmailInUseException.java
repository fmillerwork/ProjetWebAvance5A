package com.example.ProfileService.Exception;

public class EmailInUseException extends RuntimeException{
    public EmailInUseException(){
        super("Email already used !");
    }
}
