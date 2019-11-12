package com.example.feedct.jsonpojos;

public class User {
    private String email;
    private String password;
    private String nome;
    private int numero;

    public User(String email, String password, String nome, int numero) {
        this.email = email;
        this.password = password;
        this.nome = nome;
        this.numero = numero;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNome() {
        return nome;
    }

    public int getNumero() {
        return numero;
    }
}
