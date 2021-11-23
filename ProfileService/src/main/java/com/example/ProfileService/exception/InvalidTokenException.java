package com.example.ProfileService.exception;

public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException(String tokenValue){
        super("Token not valid : " + tokenValue);
    }
}
