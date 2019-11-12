package com.example.feedct.jsonpojos;

public class CadeiraUser {
    private String nomeCadeira;
    private String emailUser;

    public CadeiraUser(String nomeCadeira, String emailUser) {
        this.nomeCadeira = nomeCadeira;
        this.emailUser = emailUser;
    }

    public String getNomeCadeira() {
        return nomeCadeira;
    }

    public String getEmailUser() {
        return emailUser;
    }
}
