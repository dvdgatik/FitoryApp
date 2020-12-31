package com.dimakers.fitoryapp.api.models;

import java.io.Serializable;

public class SesionFullMes implements Serializable {
    private SubscripcionMes subscripcionMes;
    private Sucursal sucursal;
    private Club club;
    private Horario horario;

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public SubscripcionMes getSubscripcionMes() {
        return subscripcionMes;
    }

    public void setSubscripcionMes(SubscripcionMes sesion) {
        this.subscripcionMes = sesion;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }
}
