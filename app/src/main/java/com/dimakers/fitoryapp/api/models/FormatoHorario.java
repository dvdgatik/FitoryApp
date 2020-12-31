package com.dimakers.fitoryapp.api.models;

import java.util.Objects;

public class FormatoHorario {
    private String icono;
    private String nombre;
    private String lunes;
    private String martes;
    private String miercoles;
    private String jueves;
    private String viernes;
    private String sabado;
    private String domingo;

    public FormatoHorario() {
        this.lunes = "--";
        this.martes = "--";
        this.miercoles = "--";
        this.jueves = "--";
        this.viernes = "--";
        this.sabado = "--";
        this.domingo = "--";
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLunes() {
        return lunes;
    }

    public void setLunes(String lunes) {
        this.lunes = lunes;
    }

    public String getMartes() {
        return martes;
    }

    public void setMartes(String martes) {
        this.martes = martes;
    }

    public String getMiercoles() {
        return miercoles;
    }

    public void setMiercoles(String miercoles) {
        this.miercoles = miercoles;
    }

    public String getJueves() {
        return jueves;
    }

    public void setJueves(String jueves) {
        this.jueves = jueves;
    }

    public String getViernes() {
        return viernes;
    }

    public void setViernes(String viernes) {
        this.viernes = viernes;
    }

    public String getSabado() {
        return sabado;
    }

    public void setSabado(String sabado) {
        this.sabado = sabado;
    }

    public String getDomingo() {
        return domingo;
    }

    public void setDomingo(String domingo) {
        this.domingo = domingo;
    }

    @Override
    public String toString() {
        return nombre+" "+lunes+" "+martes+" "+miercoles+" "+jueves+" "+viernes+" "+sabado+" "+domingo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FormatoHorario horario = (FormatoHorario) o;
        return Objects.equals(icono, horario.icono) &&
                Objects.equals(nombre, horario.nombre) &&
                Objects.equals(lunes, horario.lunes) &&
                Objects.equals(martes, horario.martes) &&
                Objects.equals(miercoles, horario.miercoles) &&
                Objects.equals(jueves, horario.jueves) &&
                Objects.equals(viernes, horario.viernes) &&
                Objects.equals(sabado, horario.sabado) &&
                Objects.equals(domingo, horario.domingo);
    }
}
