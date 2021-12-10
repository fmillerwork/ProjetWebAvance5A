package com.example.authservice.exception;

public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException(String tokenValue){
        super("Token not valid : " + tokenValue);
    }
}
