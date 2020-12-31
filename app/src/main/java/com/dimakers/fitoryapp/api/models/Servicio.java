package com.dimakers.fitoryapp.api.models;

import java.io.Serializable;

public class Servicio implements Serializable {
    private int id;
    private String nombre;
    private String icono;

    public Servicio(String nombre, String icono) {
        this.nombre = nombre;
        this.icono = icono;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getIcono() {
        return icono;
    }
}
