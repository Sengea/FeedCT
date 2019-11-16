package com.example.feedct.pojos;

import com.example.feedct.DataManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;

public class Feedback {
    private String userEmail;
    private String cadeiraName;
    private String opinion;
    private String date;
    private int votes;

    public Feedback() { }

    public Feedback(String userEmail, String cadeiraName, String opinion, String date, int votes) {
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

    public String getDate() {
        return date;
    }

    public int getVotes() {
        return votes;
    }

    public void upVote(int n) {
        votes+=n;
        updateVotes();
    }

    public void downVote(int n) {
        votes-=n;
        updateVotes();
    }

    private void updateVotes() {
        DataManager.db.collection("feedback")
                .whereEqualTo("userEmail", userEmail)
                .whereEqualTo("cadeiraName", cadeiraName)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                queryDocumentSnapshots.getDocuments().get(0).getReference().update("votes", votes);
            }
        });
    }
}