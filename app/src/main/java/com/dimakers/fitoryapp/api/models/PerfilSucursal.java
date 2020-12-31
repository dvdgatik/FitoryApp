package com.dimakers.fitoryapp.api.models;

import java.util.ArrayList;

public class PerfilSucursal {
    private String sucursalID;
    private String clienteID;
    private ArrayList<Datos> datos;

    public String getSucursalID() {
        return sucursalID;
    }

    public String getClienteID() {
        return clienteID;
    }

    public ArrayList<Datos> getDatos() {
        return datos;
    }
}
