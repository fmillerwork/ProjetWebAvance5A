package com.example.ProfileService.exception;

public class ProfileNotFoundException extends RuntimeException{
    public ProfileNotFoundException(long id){
        super("Profile not found : " + id);
    }

    public ProfileNotFoundException(String email){
        super("Profile not found : " + email);
    }
}
