package com.example.feedct.jsonpojos;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Feedback {
    public static final int UP_VOTE = 0;
    public static final int DOWN_VOTE = 1;

    private String userEmail;
    private String cadeiraName;
    private String opinion;
    private Date date;
    private int votes;
    private Map<String, Integer> voteByUserEmail;

    public Feedback(String userEmail, String cadeiraName, String opinion, Date date, int votes) {
        this.userEmail = userEmail;
        this.cadeiraName = cadeiraName;
        this.opinion = opinion;
        this.date = date;
        this.votes = votes;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getCadeiraName() {
        return cadeiraName;
    }

    public String getOpinion() { return opinion; }

    public Date getDate() {
        return date;
    }

    public int getVotes() {
        return votes;
    }

    public void upVote() {
        votes++;
    }

    public void downVote() {
        votes--;
    }

    public Integer getVoteByUserEmail(String email) {
        if (voteByUserEmail == null)
            voteByUserEmail = new HashMap<>();

        return voteByUserEmail.get(email);
    }

    public void setVoteByUserEmail(String email, Integer vote) {
        voteByUserEmail.put(email, vote);
    }
}