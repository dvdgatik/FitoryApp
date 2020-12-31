package com.dimakers.fitoryapp.api.models;

import java.util.ArrayList;

public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private ArrayList<User> results;
    private String detail;

    public String getDetail() {return detail;}

    public ArrayList<User> getResults() {return results;}

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
