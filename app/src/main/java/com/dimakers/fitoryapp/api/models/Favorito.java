package com.dimakers.fitoryapp.api.models;

import java.util.ArrayList;

public class Favorito {
    private int id;
    private int cliente;
    private int sucursal;
    private ArrayList<Favorito> results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCliente() {
        return cliente;
    }

    public int getSucursal() {
        return sucursal;
    }

    public ArrayList<Favorito> getResults() {
        return results;
    }
}
