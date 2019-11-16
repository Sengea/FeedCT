package com.example.feedct.cadeiracomparators;

import com.example.feedct.pojos.Cadeira;

import java.util.Comparator;

public class CadeiraNameComparator implements Comparator<Cadeira> {

    @Override
    public int compare(Cadeira c1, Cadeira c2) {
        return c1.getNome().compareTo(c2.getNome());
    }
}
