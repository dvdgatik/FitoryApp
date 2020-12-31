package com.dimakers.fitoryapp.api.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ActividadHorario implements Serializable {
    private int id;
    private int actividadClub;
    private String dia;
    private String hora;
    private ArrayList<ActividadHorario> results;

    public int getId() {
        return id;
    }

    public int getActividadClub() {
        return actividadClub;
    }

    public String getDia() {
        return dia;
    }

    public String getHora() {
        return hora;
    }

    public ArrayList<ActividadHorario> getResults() {
        return results;
    }
}
