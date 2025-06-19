package com.example.fotscope.models;

public class User {

    private String fullName;
    private String email;
    private String userId;

    // Default constructor for Firebase
    public User() {
    }

    // Constructor with parameters (this is the one you're trying to use)
    public User(String fullName, String email, String userId) {
        this.fullName = fullName;
        this.email = email;
        this.userId = userId;
    }

    // Getter and setter methods for each field
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
