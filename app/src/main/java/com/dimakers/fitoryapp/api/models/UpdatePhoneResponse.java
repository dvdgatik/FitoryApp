package com.dimakers.fitoryapp.api.models;

public class UpdatePhoneResponse {
    private boolean success;
    private String code;
    private String mensaje;
    private String celular;

    public boolean isSuccessful() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getCelular() {
        return celular;
    }
}
