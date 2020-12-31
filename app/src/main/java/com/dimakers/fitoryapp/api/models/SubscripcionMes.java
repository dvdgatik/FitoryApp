package com.dimakers.fitoryapp.api.models;

import java.io.Serializable;
import java.util.ArrayList;

public class SubscripcionMes implements Serializable {
    private int id;
    private int cliente;
    private int sucursal;
    private String sucursalNombre;
    private String sucursalTelefono;
    private String sucursalLatitud;
    private String sucursalLongitud;
    private String sucursalCorreo;
    private String sucursalDireccion;
    private double totalCobrar;
    private double totalGym;
    private String fechaRenovacion;
    private boolean activa;
    private ArrayList<SubscripcionMes> results = new ArrayList<>();

    public String getSucursalDireccion() {
        return sucursalDireccion;
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

    public String getSucursalCorreo() {
        return sucursalCorreo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCliente() {
        return cliente;
    }

    public void setCliente(int cliente) {
        this.cliente = cliente;
    }

    public int getSucursal() {
        return sucursal;
    }

    public void setSucursal(int sucursal) {
        this.sucursal = sucursal;
    }

    public double getTotalCobrar() {
        return totalCobrar;
    }

    public double getTotalGym() {
        return totalGym;
    }

    public String getFechaRenovacion() {
        return fechaRenovacion;
    }

    public void setFechaRenovacion(String fechaRenovacion) {
        this.fechaRenovacion = fechaRenovacion;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public ArrayList<SubscripcionMes> getResults() {
        return results;
    }

    public void setResults(ArrayList<SubscripcionMes> results) {
        this.results = results;
    }
}
