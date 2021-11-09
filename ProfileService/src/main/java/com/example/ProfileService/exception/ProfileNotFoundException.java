package com.example.ProfileService.exception;

public class ProfileNotFoundException extends RuntimeException{
    public ProfileNotFoundException(long id){
        super("Profile not found : " + id);
    }

    public ProfileNotFoundException(int id){
        super("Profile not found : " + id);
    }
}
