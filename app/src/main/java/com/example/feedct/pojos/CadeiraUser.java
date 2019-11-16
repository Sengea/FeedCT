package com.example.feedct.pojos;

public class CadeiraUser {
    private String nomeCadeira;
    private String emailUser;
    private String turno;

    public CadeiraUser() {

    }

    public CadeiraUser(String nomeCadeira, String emailUser, String turno) {
        this.nomeCadeira = nomeCadeira;
        this.emailUser = emailUser;
        this.turno = turno;
    }

    public String getNomeCadeira() {
        return nomeCadeira;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public String getTurno() {
        return turno;
    }
}
