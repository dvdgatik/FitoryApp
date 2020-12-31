package com.dimakers.fitoryapp.api.models;

import java.io.Serializable;

public class SubscripcionPojo implements Serializable {
    private int id;
    private int cliente;
    private int sucursal;
    private String totalCobrar;
    private String totalGym;
    private String fechaRenovacion;
    private boolean activa;

    public void setTotalGym(String totalGym) {
        this.totalGym = totalGym;
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

    public String getTotalCobrar() {
        return totalCobrar;
    }

    public void setTotalCobrar(String totalCobrar) {
        this.totalCobrar = totalCobrar;
    }

    public String getTotalGym() {
        return totalGym;
    }

    public void setTotalPorCobrar(String totalPorCobrar) {
        this.totalCobrar = totalPorCobrar;
    }
}
