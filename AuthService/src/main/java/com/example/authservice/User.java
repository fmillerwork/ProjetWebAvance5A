package com.example.authservice;

import javax.validation.constraints.NotNull;

public class User {
    @NotNull(message = "Please provide an 'id'")
    private long id;
    @NotNull(message = "Please provide a 'password'")
    private String password;

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User(int id, String password) {
        this.id = id;
        this.password = password;
    }

    public User() {
    }
}
