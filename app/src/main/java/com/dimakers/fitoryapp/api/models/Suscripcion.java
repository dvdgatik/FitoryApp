package com.dimakers.fitoryapp.api.models;

import java.util.ArrayList;

public class Suscripcion {
    private String nombre;
    private String lat;
    private String lng;
    private String horario;
    private String telefono;
    private String tipo;
    private String sesiones;
    private String precio;
    private String sesionesRestantes;
    private ArrayList<Suscripcion> results;

    public Suscripcion(String nombre, String lat, String lng, String horario, String telefono, String tipo, String sesiones, String precio, String sesionesRestantes) {
        this.nombre = nombre;
        this.lat = lat;
        this.lng = lng;
        this.horario = horario;
        this.telefono = telefono;
        this.tipo = tipo;
        this.sesiones = sesiones;
        this.precio = precio;
        this.sesionesRestantes = sesionesRestantes;
    }

    public ArrayList<Suscripcion> getResults() {
        return results;
    }

    public String getNombre() {
        return nombre;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getHorario() {
        return horario;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getTipo() {
        return tipo;
    }

    public String getSesiones() {
        return sesiones;
    }

    public String getPrecio() {
        return precio;
    }

    public String getSesionesRestantes() {
        return sesionesRestantes;
    }
}
