package com.dimakers.fitoryapp.api.models;

import java.io.Serializable;
import java.util.ArrayList;

public class SucursalFull implements Serializable {
    private Club club;
    private Sucursal sucursal;
    private Horario horario;
    private ArrayList<Servicio> servicios = new ArrayList<>();
    private ArrayList<ActividadFull> actividades = new ArrayList<>();
    private boolean is_favorito;
    private int favoritoID;

    public Club getClub() {
        return club;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public Horario getHorario() {
        return horario;
    }

    public boolean isFavorito() {
        return is_favorito;
    }

    public void setIsFavorito(boolean is_favorito) {
        this.is_favorito = is_favorito;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public int getFavoritoID() {
        return favoritoID;
    }

    public void setFavoritoID(int favoritoID) {
        this.favoritoID = favoritoID;
    }

    public ArrayList<Servicio> getServicios() {
        return servicios;
    }

    public ArrayList<ActividadFull> getActividades() {
        return actividades;
    }
}
