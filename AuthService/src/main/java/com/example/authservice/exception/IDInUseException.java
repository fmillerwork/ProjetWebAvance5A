package com.example.authservice.exception;

public class IDInUseException extends RuntimeException{
    public IDInUseException(){
        super("ID already used !");
    }
}
