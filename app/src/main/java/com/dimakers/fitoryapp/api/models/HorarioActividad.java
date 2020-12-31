package com.dimakers.fitoryapp.api.models;

import java.io.Serializable;
import java.util.ArrayList;

public class HorarioActividad implements Serializable {
    private String lunes;
    private String martes;
    private String miercoles;
    private String jueves;
    private String viernes;
    private String sabado;
    private String domingo;
    private String inicio;
    private String fin;

    public String getLunes() {
        return lunes;
    }

    public String getMartes() {
        return martes;
    }

    public String getMiercoles() {
        return miercoles;
    }

    public String getJueves() {
        return jueves;
    }

    public String getViernes() {
        return viernes;
    }

    public String getSabado() {
        return sabado;
    }

    public String getDomingo() {
        return domingo;
    }

    public String getInicio() {
        return inicio;
    }

    public String getFin() {
        return fin;
    }
}
