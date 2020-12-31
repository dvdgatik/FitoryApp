package com.dimakers.fitoryapp.api.models;

import java.io.Serializable;
import java.util.ArrayList;

public class EvaluacionSucursal implements Serializable {
    private int id;
    private int cliente;
    private int sucursal;
    private float puntaje;
    private ArrayList<EvaluacionSucursal> results;

    public EvaluacionSucursal(int cliente, int sucursal, float puntaje) {
        this.cliente = cliente;
        this.sucursal = sucursal;
        this.puntaje = puntaje;
    }

    public ArrayList<EvaluacionSucursal> getResults() {
        return results;
    }

    public int getId() {
        return id;
    }

    public int getCliente() {
        return cliente;
    }

    public int getSucursal() {
        return sucursal;
    }

    public float getPuntaje() {
        return puntaje;
    }
}
