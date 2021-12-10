package com.example.authservice.exception;

public class NotFoundUserException extends RuntimeException{

    public NotFoundUserException(long id){
        super("User not found : " + id);
    }

}
