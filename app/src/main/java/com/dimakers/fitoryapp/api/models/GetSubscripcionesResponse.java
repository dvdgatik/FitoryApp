package com.dimakers.fitoryapp.api.models;

import java.util.ArrayList;

public class GetSubscripcionesResponse {
    private ArrayList<SubscripcionesGratis> subscripcionesGratis = new ArrayList<>();
    private ArrayList<SubscripcionMes> subscripciones = new ArrayList<>();
    private ArrayList<Sesion> sesiones = new ArrayList<>();

    public ArrayList<SubscripcionesGratis> getSubscripcionesGratis() {
        return subscripcionesGratis;
    }

    public ArrayList<SubscripcionMes> getSubscripciones() {
        return subscripciones;
    }

    public ArrayList<Sesion> getSesiones() {
        return sesiones;
    }
}
