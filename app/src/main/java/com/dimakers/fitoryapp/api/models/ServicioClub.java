package com.dimakers.fitoryapp.api.models;

import java.util.ArrayList;

public class ServicioClub {
    private int id;
    private int sucursal;
    private int servicio;
    private ArrayList<ServicioClub> results;

    public ArrayList<ServicioClub> getResults() {
        return results;
    }

    public int getId() {
        return id;
    }

    public int getSucursal() {
        return sucursal;
    }

    public int getServicio() {
        return servicio;
    }
}
