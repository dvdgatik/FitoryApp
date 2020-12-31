package com.dimakers.fitoryapp.api.models;

public class Metodo {
    private String last4;
    private String brand;
    private String id;

    public Metodo(String brand, String last4) {
        this.last4 = last4;
        this.brand = brand;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return brand + " " + last4;
    }
}
