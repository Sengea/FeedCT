package com.example.feedct;

public class SectionTodas {
    String departamento;
    int semestre;

    public SectionTodas(String departamento, int semestre) {
        this.departamento = departamento;
        this.semestre = semestre;
    }

    public String getDepartamento() {
        return departamento;
    }

    public int getSemestre() {
        return semestre;
    }
}
