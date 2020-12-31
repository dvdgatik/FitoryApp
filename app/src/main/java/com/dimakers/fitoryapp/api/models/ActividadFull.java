package com.dimakers.fitoryapp.api.models;

import java.io.Serializable;
import java.util.ArrayList ;

public class ActividadFull implements Serializable {
    private ArrayList<ActividadHorario> horarios = new ArrayList<>();
    private Actividad actividad;

    public Actividad getActividad() {
        return actividad;
    }

    public void setActividad(Actividad actividad) {
        this.actividad = actividad;
    }

    public ArrayList<ActividadHorario> getHorarios() {
        return horarios;
    }
}
