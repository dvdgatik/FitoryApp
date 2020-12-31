package com.dimakers.fitoryapp.api.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Sesion implements Serializable {
    private int id;
    private int cliente;
    private int sucursal;
    private String sucursalNombre;
    private String sucursalTelefono;
    private String sucursalLatitud;
    private String sucursalLongitud;
    private String sucursalCorreo;
    private String sucursalDireccion;
    private double total;
    private int sesiones;
    private int sesionesRestantes;
    private String caducidad;
    private boolean activo;

    public String getSucursalDireccion() {
        return sucursalDireccion;
    }

    public String getSucursalCorreo() {
        return sucursalCorreo;
    }

    public String getSucursalNombre() {
        return sucursalNombre;
    }

    public String getSucursalTelefono() {
        return sucursalTelefono;
    }

    public String getSucursalLatitud() {
        return sucursalLatitud;
    }

    public String getSucursalLongitud() {
        return sucursalLongitud;
    }

    private ArrayList<Sesion> results;

    public ArrayList<Sesion> getResults() {
        return results;
    }

    public void setSesiones(int sesiones) {
        this.sesiones = sesiones;
    }

    public void setSesionesRestantes(int sesionesRestantes) {
        this.sesionesRestantes = sesionesRestantes;
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

    public double getTotal() {
        return total;
    }

    public int getSesiones() {
        return sesiones;
    }

    public int getSesionesRestantes() {
        return sesionesRestantes;
    }

    public String getCaducidad() {
        return caducidad;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setCliente(int cliente) {
        this.cliente = cliente;
    }

    public void setSucursal(int sucursal) {
        this.sucursal = sucursal;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setCaducidad(String caducidad) {
        this.caducidad = caducidad;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
