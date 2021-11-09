package com.example.authservice.exception;

public class TokenNotValidException extends RuntimeException{
    public TokenNotValidException(String tokenValue){
        super("Token not valid : " + tokenValue);
    }
}
