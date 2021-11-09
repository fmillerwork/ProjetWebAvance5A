package com.example.authservice.exception;

public class IDInUseException extends RuntimeException{
    public IDInUseException(long id){
        super("ID already used : " + id);
    }
}
