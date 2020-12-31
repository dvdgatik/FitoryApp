package com.dimakers.fitoryapp.api.models;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Comparator;

public class SesionFull implements Serializable, Comparable<SesionFull>{
    public int id;
    private Sesion sesion;
    private Sucursal sucursal;
    private Club club;
    private Horario horario;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public Sesion getSesion() {
        return sesion;
    }

    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
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


    @Override
    public int compareTo(@NonNull SesionFull o) {
        return toString().compareTo(o.toString());
    }

    @Override
    public String toString() {
        return "SesionFull{" +
                "id=" + id +
                ", sesion=" + sesion +
                ", sucursal=" + sucursal +
                ", club=" + club +
                ", horario=" + horario +
                '}';
    }
}
