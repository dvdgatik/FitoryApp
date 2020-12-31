package com.dimakers.fitoryapp.api.models;

public class UserLogin {
    String username;
    String token;
    String idUser;

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public String getIdUser() {
        return idUser;
    }

    @Override
    public String toString() {
        return username;
    }
}
