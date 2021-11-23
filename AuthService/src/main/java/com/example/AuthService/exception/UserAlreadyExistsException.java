package com.example.AuthService.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(Long id){

        super("User already exists : " + id);
    }
}
