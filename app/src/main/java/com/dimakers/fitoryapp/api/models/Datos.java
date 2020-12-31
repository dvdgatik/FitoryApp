package com.dimakers.fitoryapp.api.models;

import java.util.ArrayList;

public class Datos {
    private String latitud;
    private String calle;
    private ArrayList<Servicio> servicios = new ArrayList<>();
    private int favorito;
    private String numExt;
    private String colonia;
    private double calificacion;
    private ArrayList<Actividad> actividades = new ArrayList<>();
    private double mensualidad;
    private double dia;
    private String correo;
    private ArrayList<Club> club = new ArrayList<>();
    private String longitud;
    private String logo;
    private ArrayList<Horario> horario = new ArrayList<>();
    private String nombre;
    private String cp;
    private String telefono;
    private String numInt;
    private String municipio;
    private String tips;

    public String getTips() {
        return tips;
    }

    private ArrayList<String> galeria = new ArrayList<>();
    private ArrayList<RegistroHorario> registroHorario = new ArrayList<>();

    public ArrayList<Servicio> getServicios() {
        return servicios;
    }

    public ArrayList<Actividad> getActividades() {
        return actividades;
    }

    public ArrayList<RegistroHorario> getRegistroHorario() {
        return registroHorario;
    }

    public ArrayList<String> getGaleria() {
        return galeria;
    }

    public String getLatitud() {
        return latitud;
    }

    public String getCalle() {
        return calle;
    }

    public ArrayList<Servicio> getServicio() {
        return servicios;
    }

    public int getFavorito() {
        return favorito;
    }

    public String getNumExt() {
        return numExt;
    }

    public String getColonia() {
        return colonia;
    }

    public double getCalificacion() {
        return calificacion;
    }

    public ArrayList<Actividad> getActividad() {
        return actividades;
    }

    public double getMensualidad() {
        return mensualidad;
    }

    public double getDia() {
        return dia;
    }

    public String getCorreo() {
        return correo;
    }

    public ArrayList<Club> getClub() {
        return club;
    }

    public String getLongitud() {
        return longitud;
    }

    public String getLogo() {
        return logo;
    }

    public ArrayList<Horario> getHorario() {
        return horario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCp() {
        return cp;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getNumInt() {
        return numInt;
    }

    public String getMunicipio() {
        return municipio;
    }
}
