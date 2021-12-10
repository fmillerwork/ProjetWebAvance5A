package com.example.ProfileService.exception;

public class WrongPasswordException extends RuntimeException{
    public WrongPasswordException(String email){
        super("Wrong password for profile : " + email);
    }
}
