package com.example.ProfileService;

public class AuthServiceUser {
    private Long id;
    private String password;

    public AuthServiceUser(Long id) {
        this.id = id;
        this.password = "zertyuio";
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
