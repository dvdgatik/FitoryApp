package com.dimakers.fitoryapp.api.models;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Sucursal implements Serializable {
    private int id;
    private int club;
    private String nombre;
    private String descripcion;
    private String correo;
    private String telefono;
    private int estado;
    private int ciudad;
    private String municipio;
    private String calle;
    private String numExt;
    private String numInt;
    private String colonia;
    private String cp;
    private String latitud;
    private String longitud;
    private double calificacion;
    private String logo;
    private String ibeacon;
    private int maximo;
    private int minimo;
    private String mensualidad;
    private String dia;
    private String porcentajeCliente;
    private String procentajeUser;
    private String saldo;
    private int diasPruebas;
    private boolean activa;

    public int getDiasPrueba() {
        return diasPruebas;
    }

    public String getIbeacon() {
        return ibeacon;
    }

    public int getMaximo() {
        return maximo;
    }

    public int getMinimo() {
        return minimo;
    }

    public String getMensualidad() {
        return mensualidad;
    }

    public String getDia() {
        return dia;
    }

    public String getPorcentajeCliente() {
        return porcentajeCliente;
    }

    public String getProcentajeUser() {
        return procentajeUser;
    }

    public String getSaldo() {
        return saldo;
    }

    public boolean isActiva() {
        return activa;
    }

    //    private boolean favorito;

//    public boolean isFavorito() {
//        return favorito;
//    }

//    public void setFavorito(boolean favorito) {
//        this.favorito = favorito;
//    }

    private ArrayList<Sucursal> results;

    public int getId() {
        return id;
    }

    public int getClub() {
        return club;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public int getEstado() {
        return estado;
    }

    public int getCiudad() {
        return ciudad;
    }

    public String getMunicipio() {
        return municipio;
    }

    public String getCalle() {
        return calle;
    }

    public String getNumExt() {
        return numExt;
    }

    public String getNumInt() {
        return numInt;
    }

    public String getColonia() {
        return colonia;
    }

    public String getCp() {
        return cp;
    }

    public String getLatitud() {
        return latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public double getCalificacion() {
        return calificacion;
    }

    public String getLogo() {
        return logo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setClub(int club) {
        this.club = club;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public void setCiudad(int ciudad) {
        this.ciudad = ciudad;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public void setNumExt(String numExt) {
        this.numExt = numExt;
    }

    public void setNumInt(String numInt) {
        this.numInt = numInt;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public void setCalificacion(double calificacion) {
        this.calificacion = calificacion;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public void setIbeacon(String ibeacon) {
        this.ibeacon = ibeacon;
    }

    public void setMaximo(int maximo) {
        this.maximo = maximo;
    }

    public void setMinimo(int minimo) {
        this.minimo = minimo;
    }

    public void setMensualidad(String mensualidad) {
        this.mensualidad = mensualidad;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public void setPorcentajeCliente(String porcentajeCliente) {
        this.porcentajeCliente = porcentajeCliente;
    }

    public void setProcentajeUser(String procentajeUser) {
        this.procentajeUser = procentajeUser;
    }

    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public void setResults(ArrayList<Sucursal> results) {
        this.results = results;
    }

    public ArrayList<Sucursal> getResults() {
        return results;
    }
}
