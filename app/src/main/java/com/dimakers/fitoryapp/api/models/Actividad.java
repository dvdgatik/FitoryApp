package com.dimakers.fitoryapp.api.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Actividad implements Serializable {
    private int id;
    private String nombre;
    private String icono;
    private ArrayList<HorarioActividad> horarios = new ArrayList<>();

    public ArrayList<HorarioActividad> getHorarios() {
        return horarios;
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
