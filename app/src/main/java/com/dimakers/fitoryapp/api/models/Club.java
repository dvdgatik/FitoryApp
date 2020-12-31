package com.dimakers.fitoryapp.api.models;

import java.io.Serializable;

public class Club implements Serializable{
    private int id;
    private String nombre;
    private String fechaIncorporacion;
    private String RFC;
    private String banco;
    private String tarjetahabiente;
    private String numCuenta;
    private String paginaWeb;
    private String facebook;
    private String instagram;
    private String twitter;
    private String evaluacionPromedio;
    private boolean activado;
    private String codigoClub;
    private String codigoRepresentante;
    private String direccion;
    private String telefono;
    private String correo;
    private String foto;

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFechaIncorporacion() {
        return fechaIncorporacion;
    }

    public String getRFC() {
        return RFC;
    }

    public String getBanco() {
        return banco;
    }

    public String getTarjetahabiente() {
        return tarjetahabiente;
    }

    public String getNumCuenta() {
        return numCuenta;
    }

    public String getPaginaWeb() {
        return paginaWeb;
    }

    public String getFacebook() {
        return facebook;
    }

    public String getInstagram() {
        return instagram;
    }

    public String getTwitter() {
        return twitter;
    }

    public boolean isActivado() {
        return activado;
    }

    public String getCodigoClub() {
        return codigoClub;
    }

    public String getCodigoRepresentante() {
        return codigoRepresentante;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public String getFoto() {
        return foto;
    }

    public String getEvaluacionPromedio() {
        return evaluacionPromedio;
    }
}
