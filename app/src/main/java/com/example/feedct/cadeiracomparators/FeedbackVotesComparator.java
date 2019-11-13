package com.example.feedct.cadeiracomparators;

import com.example.feedct.jsonpojos.Feedback;

import java.util.Comparator;

public class FeedbackVotesComparator implements Comparator<Feedback> {

    @Override
    public int compare(Feedback o1, Feedback o2) {
        return o2.getVotes() -  o1.getVotes();
    }
}
