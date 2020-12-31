package com.dimakers.fitoryapp.api.models;

import java.io.Serializable;

public class SubscripcionFreeFull implements Serializable {
    private SubscripcionesGratis subscripcionGratis;
    private Sucursal sucursal;
    private Club club;
    private Horario horario;

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public SubscripcionesGratis getSubscripcionMes() {
        return subscripcionGratis;
    }

    public void setSubscripcionMes(SubscripcionesGratis sesion) {
        this.subscripcionGratis = sesion;
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
