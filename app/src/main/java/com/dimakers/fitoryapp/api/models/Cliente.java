package com.dimakers.fitoryapp.api.models;

import java.util.ArrayList;

public class Cliente {
    private int id;
    private int user;
    private String nombre;
    private String apellido;
    private String telefono;
    private boolean hombre;
    private boolean mujer;
    private String fechaIngreso;
    private boolean salud;
    private boolean convivir;
    private boolean vermeBien;
    private boolean diversion;
    private int estado;
    private int ciudad;
    private boolean ubicacion;
    private boolean bluetooth;
    private String idFacebook;
    private String idGoogle;
    private String idCustomer;
    private String foto;
    private int plan;
    private ArrayList<Cliente> results;

    public int getId() {
        return id;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

    public int getUser() {
        return user;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getTelefono() { return telefono; }

    public boolean isHombre() {
        return hombre;
    }

    public boolean isMujer() {
        return mujer;
    }

    public boolean isSalud() {
        return salud;
    }

    public boolean isConvivir() {
        return convivir;
    }

    public boolean isVermeBien() {
        return vermeBien;
    }

    public boolean isDiversion() {
        return diversion;
    }

    public int getEstado() {
        return estado;
    }

    public int getCiudad() {
        return ciudad;
    }

    public boolean isUbicacion() {
        return ubicacion;
    }

    public boolean isBluetooth() {
        return bluetooth;
    }

    public String getIdFacebook() {
        return idFacebook;
    }

    public String getIdGoogle() {
        return idGoogle;
    }

    public String getIdCustomer() {
        return idCustomer;
    }

    public String getFoto() {
        return foto;
    }

    public int getPlan() {
        return plan;
    }

    public ArrayList<Cliente> getResults() {
        return results;
    }

    public void setId(int id) {
        this.id = id;
    }
}
