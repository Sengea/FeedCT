package com.example.feedct.pojos;

public class User {
    private String email;
    private String password;
    private String nome;
    private int numero;
    private String curso;

    public User() {

    }

    public User(String email, String password, String nome, int numero, String curso) {
        this.email = email;
        this.password = password;
        this.nome = nome;
        this.numero = numero;
        this.curso = curso;
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

    public String getCurso() {
        return curso;
    }
}
