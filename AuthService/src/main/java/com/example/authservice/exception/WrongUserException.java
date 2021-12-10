package com.example.authservice.exception;

public class WrongUserException extends RuntimeException{

    public WrongUserException(String tokenValue){
        super(String.format("The token '[%s]' is taken by another user !", tokenValue));
    }
}
