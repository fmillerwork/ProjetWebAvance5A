package com.example.AuthService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String description;
    private long id;
    private String password;
    private List<String> tokens;

    public User(String description, long id, String password, List<String> tokens) {
        this.description = description;
        this.id = id;
        this.password = password;
        this.tokens = tokens;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }
}
