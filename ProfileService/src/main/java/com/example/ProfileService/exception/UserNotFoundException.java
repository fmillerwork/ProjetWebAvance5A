package com.example.ProfileService.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(long id){
        super("Profile not found : " + id);
    }

    public UserNotFoundException(int id){
        super("Profile not found : " + id);
    }
}
