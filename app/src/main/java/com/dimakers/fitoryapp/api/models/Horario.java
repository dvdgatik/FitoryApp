package com.dimakers.fitoryapp.api.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Horario implements Serializable{
    private int id;
    private int sucursal;
    private boolean lunes;
    private boolean martes;
    private boolean miercoles;
    private boolean jueves;
    private boolean viernes;
    private boolean sabado;
    private boolean domingo;
    private String tipo;
    private int numDias;
//    private ArrayList<Horario> results;

    public String getTipo() {        return tipo;    }

    public int getId() {
        return id;
    }

    public int getSucursal() {
        return sucursal;
    }

    public boolean isLunes() {
        return lunes;
    }

    public boolean isMartes() {
        return martes;
    }

    public boolean isMiercoles() {
        return miercoles;
    }

    public boolean isJueves() {
        return jueves;
    }

    public boolean isViernes() {
        return viernes;
    }

    public boolean isSabado() {
        return sabado;
    }

    public boolean isDomingo() {
        return domingo;
    }

    public int getNumDias() {
        return numDias;
    }
}
