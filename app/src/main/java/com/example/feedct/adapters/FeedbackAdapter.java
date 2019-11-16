package com.example.feedct.adapters;

import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedct.DataManager;
import com.example.feedct.R;
import com.example.feedct.Session;
import com.example.feedct.pojos.Cadeira;
import com.example.feedct.pojos.Feedback;
import com.example.feedct.pojos.User;
import com.example.feedct.pojos.UserVote;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private List<Feedback> feedback;

    public FeedbackAdapter() {
        feedback = new LinkedList<>();
    }

    public void setData(List<Feedback> data, Comparator<Feedback> comparator) {
        if (data == null)
            return;

        feedback.clear();
        feedback.addAll(data);
        Collections.sort(feedback, comparator);

        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_feedback, parent, false);
        return new FeedbackAdapter.MyItem(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((FeedbackAdapter.MyItem) holder).setup(feedback.get(position));
    }

    @Override
    public int getItemCount() {
        return feedback.size();
    }

    public static class MyItem extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView dateTextView;
        private TextView opinionTextView;
        private TextView cursoTextView;
        private TextView votesTextView;
        private ImageButton imageButtonUpVote;
        private ImageButton imageButtonDownVote;

        public MyItem(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.textViewName);
            dateTextView = itemView.findViewById(R.id.textViewDate);
            opinionTextView = itemView.findViewById(R.id.textViewOpinion);
            cursoTextView = itemView.findViewById(R.id.textViewCurso);
            votesTextView = itemView.findViewById(R.id.textViewVotes);
            imageButtonUpVote = itemView.findViewById(R.id.imageButtonUpVote);
            imageButtonDownVote = itemView.findViewById(R.id.imageButtonDownVote);
        }

        public void setup(final Feedback feedback) {
            DataManager.db.collection("users").whereEqualTo("email", feedback.getUserEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    User user = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);

                    nameTextView.setText(user.getNome());
                    dateTextView.setText(feedback.getDate());
                    opinionTextView.setText(feedback.getOpinion());
                    cursoTextView.setText(user.getCurso());
                    votesTextView.setText(String.valueOf(feedback.getVotes()));

                    DataManager.db.collection("votes")
                        .whereEqualTo("feedbackCadeiraName", feedback.getCadeiraName())
                        .whereEqualTo("feedbackUserEmail", feedback.getUserEmail())
                        .whereEqualTo("userEmail", Session.userEmail)
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                                if (documentSnapshots.size() == 1) {
                                    UserVote userVote = documentSnapshots.get(0).toObject(UserVote.class);
                                    if (userVote.getVoteType() == UserVote.UP_VOTE) {
                                        imageButtonUpVote.setBackgroundResource(R.drawable.ic_up_arrow_filled);
                                    }
                                    else if (userVote.getVoteType() == UserVote.DOWN_VOTE) {
                                        imageButtonDownVote.setBackgroundResource(R.drawable.ic_down_arrow_filled);
                                    }
                                }
                                else {
                                    imageButtonUpVote.setBackgroundResource(R.drawable.ic_up_arrow_empty);
                                    imageButtonDownVote.setBackgroundResource(R.drawable.ic_down_arrow_empty);
                                }
                            }
                    });
                }
            });

            imageButtonUpVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataManager.db.collection("votes")
                            .whereEqualTo("feedbackCadeiraName", feedback.getCadeiraName())
                            .whereEqualTo("feedbackUserEmail", feedback.getUserEmail())
                            .whereEqualTo("userEmail", Session.userEmail)
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                            if (documentSnapshots.size() == 1) {
                                UserVote userVote = documentSnapshots.get(0).toObject(UserVote.class);
                                if (userVote.getVoteType() == UserVote.UP_VOTE) {
                                    documentSnapshots.get(0).getReference().delete();
                                    feedback.downVote(1);
                                    imageButtonUpVote.setBackgroundResource(R.drawable.ic_up_arrow_empty);
                                }
                                else if (userVote.getVoteType() == UserVote.DOWN_VOTE) {
                                    documentSnapshots.get(0).getReference().update("voteType", UserVote.UP_VOTE);
                                    feedback.upVote(2);
                                    imageButtonUpVote.setBackgroundResource(R.drawable.ic_up_arrow_filled);
                                    imageButtonDownVote.setBackgroundResource(R.drawable.ic_down_arrow_empty);
                                }
                            }
                            else {
                                DataManager.db.collection("votes").add(new UserVote(feedback.getCadeiraName(), feedback.getUserEmail(), Session.userEmail, UserVote.UP_VOTE));
                                feedback.upVote(1);
                                imageButtonUpVote.setBackgroundResource(R.drawable.ic_up_arrow_filled);
                            }

                            votesTextView.setText(String.valueOf(feedback.getVotes()));
                        }
                    });
                }
            });

            imageButtonDownVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataManager.db.collection("votes")
                            .whereEqualTo("feedbackCadeiraName", feedback.getCadeiraName())
                            .whereEqualTo("feedbackUserEmail", feedback.getUserEmail())
                            .whereEqualTo("userEmail", Session.userEmail)
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                            if (documentSnapshots.size() == 1) {
                                UserVote userVote = documentSnapshots.get(0).toObject(UserVote.class);
                                if (userVote.getVoteType() == UserVote.UP_VOTE) {
                                    documentSnapshots.get(0).getReference().update("voteType", UserVote.DOWN_VOTE);
                                    feedback.downVote(2);
                                    imageButtonDownVote.setBackgroundResource(R.drawable.ic_down_arrow_filled);
                                    imageButtonUpVote.setBackgroundResource(R.drawable.ic_up_arrow_empty);
                                }
                                else if (userVote.getVoteType() == UserVote.DOWN_VOTE) {
                                    documentSnapshots.get(0).getReference().delete();
                                    feedback.upVote(1);
                                    imageButtonDownVote.setBackgroundResource(R.drawable.ic_down_arrow_empty);
                                }
                            }
                            else {
                                DataManager.db.collection("votes").add(new UserVote(feedback.getCadeiraName(), feedback.getUserEmail(), Session.userEmail, UserVote.DOWN_VOTE));
                                feedback.downVote(1);
                                imageButtonDownVote.setBackgroundResource(R.drawable.ic_down_arrow_filled);
                            }

                            votesTextView.setText(String.valueOf(feedback.getVotes()));
                        }
                    });
                }
            });
        }
    }
}
