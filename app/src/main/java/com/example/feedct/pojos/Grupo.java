package com.example.feedct.pojos;

import androidx.core.content.ContextCompat;

import com.example.feedct.DataManager;
import com.example.feedct.R;
import com.example.feedct.Session;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Grupo implements Comparable<Grupo> {
    public static final int MODE_REQUESTS = 0;
    public static final int MODE_AUTOMATIC = 1;
    public static final int MODE_NO_REQUESTS = 2;

    private String cadeira;
    private List<String> elementos;
    private int maxElementos;
    private String turnos;
    private int mode;

    public Grupo(){}

    public Grupo(String cadeira, String elemento) {
        this.cadeira = cadeira;
        elementos = new LinkedList<>();
        elementos.add(elemento);
        maxElementos = 2;
        turnos = "Todos";
        mode = MODE_REQUESTS;
    }

    public Grupo(String cadeira, List<String> elementos, int maxElementos) {
        this.cadeira = cadeira;
        this.elementos = elementos;
        this.maxElementos = maxElementos;
    }

    public String getCadeira() {
        return cadeira;
    }

    public List<String> getElementos() {
        return elementos;
    }

    public int getMaxElementos() {
        return maxElementos;
    }

    public String getTurnos() {
        return turnos;
    }

    public int getMode() {
        return mode;
    }

    public void setMaxElementos(int maxElementos) {
        this.maxElementos = maxElementos;
    }

    public void setTurnos(String turnos) {
        this.turnos = turnos;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public int compareTo(Grupo o) {
        int result;

        if (elementos.contains(Session.userEmail))
            result = -1;
        else if (o.getElementos().contains(Session.userEmail))
            result = 1;
        else {
            result = o.getElementos().size() - elementos.size();
        }


        return result;
    }


}
