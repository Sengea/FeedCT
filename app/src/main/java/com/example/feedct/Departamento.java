package com.example.feedct;

import com.example.feedct.jsonpojos.Cadeira;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Departamento implements Comparable<Departamento> {
    private List<Cadeira> cadeiras_sem1;
    private List<Cadeira> cadeiras_sem2;
    private String name;

    public Departamento(String name) {
        this.name = name;
        cadeiras_sem1 = new LinkedList<>();
        cadeiras_sem2 = new LinkedList<>();
    }

    public String getName() { return name; }

    public void addCadeira(Cadeira cadeira) {
        if (cadeira.getSemestre() == 1)
            cadeiras_sem1.add(cadeira);
        else
            cadeiras_sem2.add(cadeira);
    }

    public void sortCadeiras(Comparator<Cadeira> comparator) {
        Collections.sort(cadeiras_sem1, comparator);
        Collections.sort(cadeiras_sem2, comparator);
    }

    public List<Cadeira> getCadeirasBySem(int semestre) {
        if (semestre == 1)
            return cadeiras_sem1;
        else
            return cadeiras_sem2;
    }

    public int getCadeirasBySemSize(int semestre) {
        if (semestre == 1)
            return cadeiras_sem1.size();
        else
            return cadeiras_sem2.size();
    }

    @Override
    public int compareTo(Departamento other) {
        return name.compareTo(other.getName());
    }
}
