package com.dimakers.fitoryapp.api.models;

public class VerifyPhoneResponse {
    private boolean success;
    private String mensaje;
    private String code;

    public boolean isSuccessful() {
        return success;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getCode() {
        return code;
    }
}
