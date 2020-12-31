package com.dimakers.fitoryapp.api.models;

import java.util.List;

public class MetodosPago {

        private String clienteID;
        private List<Metodo> metodos = null;
        private String error;

    public String getError() {
        return error;
    }

    public String getClienteID() {
            return clienteID;
        }

        public void setClienteID(String clienteID) {
            this.clienteID = clienteID;
        }

        public List<Metodo> getMetodos() {
            return metodos;
        }

        public void setMetodos(List<Metodo> metodos) {
            this.metodos = metodos;
        }
}
