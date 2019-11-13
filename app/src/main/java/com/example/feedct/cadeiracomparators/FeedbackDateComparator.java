package com.example.feedct.cadeiracomparators;

import com.example.feedct.jsonpojos.Feedback;

import java.util.Comparator;

public class FeedbackDateComparator implements Comparator<Feedback> {

    @Override
    public int compare(Feedback o1, Feedback o2) {
        return o2.getDate().compareTo(o1.getDate());
    }
}
