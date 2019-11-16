package com.example.feedct.pojos;

public class UserVote {
    public static final int UP_VOTE = 0;
    public static final int DOWN_VOTE = 1;

    private String feedbackCadeiraName;
    private String feedbackUserEmail;
    private String userEmail;
    private int voteType;

    public UserVote() {
    }

    public UserVote(String feedbackCadeiraName, String feedbackUserEmail, String userEmail, int voteType) {
        this.feedbackCadeiraName = feedbackCadeiraName;
        this.feedbackUserEmail = feedbackUserEmail;
        this.userEmail = userEmail;
        this.voteType = voteType;
    }

    public String getFeedbackCadeiraName() {
        return feedbackCadeiraName;
    }

    public String getFeedbackUserEmail() {
        return feedbackUserEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public int getVoteType() {
        return voteType;
    }
}
