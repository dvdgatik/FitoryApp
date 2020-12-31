package com.dimakers.fitoryapp.api.models;

import java.io.Serializable;
import java.util.ArrayList;

public class SubscripcionesGratis implements Serializable {
    private int id;
    private int cliente;
    private int sucursal;
    private String fechaSubscripcion;
    private String sucursalNombre;
    private String sucursalTelefono;
    private String sucursalLatitud;
    private String sucursalLongitud;
    private String sucursalCorreo;
    private String sucursalDireccion;
    private String fechaFin;
    private String direccion;
    private boolean activa;
    private ArrayList<SubscripcionesGratis> results = new ArrayList<>();

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

    public String getSucursalDireccion() {
        return sucursalDireccion;
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

    public String getFechaSubscripcion() {
        return fechaSubscripcion;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public String getDireccion() {
        return direccion;
    }

    public boolean isActiva() {
        return activa;
    }

    public ArrayList<SubscripcionesGratis> getResults() {
        return results;
    }
}
