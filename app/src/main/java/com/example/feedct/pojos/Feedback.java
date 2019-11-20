package com.example.feedct.pojos;

import com.example.feedct.DataManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class Feedback {
    private String userEmail;
    private String cadeiraName;
    private String opinion;
    private String date;
    private int votes;
    private List<String> upVotes;
    private List<String> downVotes;

    public Feedback() { }

    public Feedback(String userEmail, String cadeiraName, String opinion, String date, int votes, List<String> upVotes, List<String> downVotes) {
        this.userEmail = userEmail;
        this.cadeiraName = cadeiraName;
        this.opinion = opinion;
        this.date = date;
        this.votes = votes;
        this.upVotes = upVotes;
        this.downVotes = downVotes;
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
        //updateVotes();
    }

    public void downVote(int n) {
        votes-=n;
        //updateVotes();
    }

    public List<String> getUpVotes() {
        return upVotes;
    }

    public List<String> getDownVotes() {
        return downVotes;
    }

    private void updateVotes() {
        DataManager.db.collection(DataManager.FEEDBACK)
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