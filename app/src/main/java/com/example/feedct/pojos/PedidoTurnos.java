package com.example.feedct.pojos;

import java.util.List;

public class PedidoTurnos {
    private String cadeira;
    private String sender;
    private String trocaTurnosId;

    public PedidoTurnos() {

    }

    public PedidoTurnos(String cadeira, String sender, String trocaTurnosId) {
        this.cadeira = cadeira;
        this.sender = sender;
        this.trocaTurnosId = trocaTurnosId;
    }

    public String getCadeira() {
        return cadeira;
    }

    public String getSender() {
        return sender;
    }

    public String getTrocaTurnosId() {
        return trocaTurnosId;
    }
}
