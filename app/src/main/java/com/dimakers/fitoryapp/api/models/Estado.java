package com.dimakers.fitoryapp.api.models;

import java.util.ArrayList;

public class Estado {
    private int id;
    private String nombre;
    private ArrayList<Estado> results;

    public Estado(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public ArrayList<Estado> getResults() {
        return results;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
