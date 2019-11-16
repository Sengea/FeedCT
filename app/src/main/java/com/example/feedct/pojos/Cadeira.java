package com.example.feedct.pojos;

public class Cadeira implements Comparable<Cadeira> {
    private String sigla;
    private String nome;
    private String departamento;
    private int semestre;
    private int creditos;
    private float rating;

    public Cadeira() {

    }

    public Cadeira(String sigla, String nome, String departamento, int semestre, int creditos, float rating) {
        this.sigla = sigla;
        this.nome = nome;
        this.departamento = departamento;
        this.semestre = semestre;
        this.creditos = creditos;
        this.rating = rating;
    }

    public String getSigla() {
        return sigla;
    }

    public String getNome() {
        return nome;
    }

    public String getDepartamento() {
        return departamento;
    }

    public int getSemestre() { return semestre; }

    public int getCreditos() { return creditos; }

    public float getRating() {
        return rating;
    }

    @Override
    public int compareTo(Cadeira other) {
        return nome.compareTo(other.getNome());
    }
}
