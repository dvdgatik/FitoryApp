package com.dimakers.fitoryapp.api.models;

import java.util.ArrayList;

public class Visita {
    private int id;
    private int cliente;
    private int sucursal;
    private String fecha;
    private String hora;
    private ArrayList<Visita> results;

    public ArrayList<Visita> getResults() {
        return results;
    }

    public int getCliente() {
        return cliente;
    }

    public int getSucursal() {
        return sucursal;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }
}
