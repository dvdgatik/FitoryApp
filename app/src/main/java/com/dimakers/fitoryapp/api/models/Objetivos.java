package com.dimakers.fitoryapp.api.models;

public class Objetivos {
    private boolean salud;
    private boolean convivir;
    private boolean vermeBien;
    private boolean diversion;

    public Objetivos(boolean salud, boolean convivir, boolean vermeBien, boolean diversion) {
        this.salud = salud;
        this.convivir = convivir;
        this.vermeBien = vermeBien;
        this.diversion = diversion;
    }

    public boolean isSalud() {
        return salud;
    }

    public boolean isConvivir() {
        return convivir;
    }

    public boolean isVermeBien() {
        return vermeBien;
    }

    public boolean isDiversion() {
        return diversion;
    }
}
