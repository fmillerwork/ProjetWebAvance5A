package com.example.ProfileService;

public class Profile {
    private long id;
    private String name;

    // Email, description (pas de doublon), ....

    public Profile(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Profile() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }




}
