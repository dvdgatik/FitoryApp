package com.dimakers.fitoryapp.api.models;

import java.util.ArrayList;

public class ActividadClub {
    private int id;
    private int sucursal;
    private int actividad;
    private ArrayList<ActividadClub> results;

    public int getId() {
        return id;
    }

    public int getSucursal() {
        return sucursal;
    }

    public int getActividad() {
        return actividad;
    }

    public ArrayList<ActividadClub> getResults() {
        return results;
    }
}
