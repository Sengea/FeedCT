package com.example.feedct;

public class Cadeira implements Comparable<Cadeira> {
    private boolean inscrito;
    private String sigla;
    private String nome;
    private String departamento;
    private int semestre;
    private int creditos;
    private float rating;

    public Cadeira(boolean inscrito, String sigla, String nome, String departamento, int semestre, int creditos, float rating) {
        this.inscrito = inscrito;
        this.sigla = sigla;
        this.nome = nome;
        this.departamento = departamento;
        this.semestre = semestre;
        this.creditos = creditos;
        this.rating = rating;
    }

    public boolean isInscrito() { return inscrito; }

    public String getSiglaText() {
        return sigla;
    }

    public String getNomeText() {
        return nome;
    }

    public String getDepartamentoText() {
        return departamento;
    }

    public String getSemestreText() {
        return String.valueOf(semestre) + "º SEM";
    }

    public String getCreditosText() {
        return String.valueOf(creditos) + " ECTS";
    }

    public int getSemestre() { return semestre; }

    public int getCreditos() { return creditos; }

    public float getRating() {
        return rating;
    }

    @Override
    public int compareTo(Cadeira other) {
        return nome.compareTo(other.getNomeText());
    }
}
