package com.example.feedct.cadeiracomparators;

import com.example.feedct.pojos.Cadeira;

import java.util.Comparator;

public class CadeiraCreditosComparator implements Comparator<Cadeira> {

    @Override
    public int compare(Cadeira c1, Cadeira c2) {
        return c1.getCreditos() - c2.getCreditos();
    }
}
