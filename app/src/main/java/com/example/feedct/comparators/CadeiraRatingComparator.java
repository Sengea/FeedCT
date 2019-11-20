package com.example.feedct.comparators;

import com.example.feedct.pojos.Cadeira;

import java.util.Comparator;

public class CadeiraRatingComparator implements Comparator<Cadeira> {

    @Override
    public int compare(Cadeira c1, Cadeira c2) {
        int result;

        if (c1.getRating() > c2.getRating())
            result = 1;
        else if (c1.getRating() < c2.getRating())
            result = -1;
        else
            result = 0;

        return result;
    }
}
