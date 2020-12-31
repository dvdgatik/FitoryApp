package com.dimakers.fitoryapp.api.models;

import java.util.ArrayList;

public class Ciudad {
    private int id;
    private int estado;
    private String nombre;
    private ArrayList<Ciudad> results;

    public ArrayList<Ciudad> getResults() {
        return results;
    }

    public int getId() {
        return id;
    }

    public int getEstado() {
        return estado;
    }

    public String getNombre() {
        return nombre;
    }
}
