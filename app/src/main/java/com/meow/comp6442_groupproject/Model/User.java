package com.meow.comp6442_groupproject.Model;

import java.util.ArrayList;

public class User {

    private String userId;
    private String email;
    private String username;
    private String gender;


    public User(String userId, String email, String username, String gender) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.gender = gender;
    }

    public User(){}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
