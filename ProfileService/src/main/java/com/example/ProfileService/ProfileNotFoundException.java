package com.example.ProfileService;

public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(long id) {
        super("Profile not found : " + id);
    }
}
