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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FeedbackAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private List<Feedback> feedback;
    private Map<Feedback, User> userByFeedback;

    public FeedbackAdapter() {
        feedback = new ArrayList<>();
    }

    public void setData(List<Feedback> data, Map<Feedback, User> userByFeedback, Comparator<Feedback> comparator) {
        if (data == null)
            return;

        feedback.clear();
        feedback.addAll(data);
        Collections.sort(feedback, comparator);
        this.userByFeedback = userByFeedback;

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
        Feedback f = feedback.get(position);
        ((FeedbackAdapter.MyItem) holder).setup(f, userByFeedback.get(f));
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

        public void setup(final Feedback feedback, final User user) {
            nameTextView.setText(user.getNome());
            dateTextView.setText(feedback.getDate());
            opinionTextView.setText(feedback.getOpinion());
            cursoTextView.setText(user.getCurso());
            votesTextView.setText(String.valueOf(feedback.getVotes()));

            boolean userUpVoted = feedback.getUpVotes().contains(Session.userEmail);
            boolean userDownVoted = feedback.getDownVotes().contains(Session.userEmail);

            if (userUpVoted) {
                imageButtonUpVote.setBackgroundResource(R.drawable.ic_thumb_up_filled);
                imageButtonDownVote.setBackgroundResource(R.drawable.ic_thumb_down);
            }
            else if (userDownVoted) {
                imageButtonDownVote.setBackgroundResource(R.drawable.ic_thumb_down_filled);
                imageButtonUpVote.setBackgroundResource(R.drawable.ic_thumb_up);
            }
            else {
                imageButtonUpVote.setBackgroundResource(R.drawable.ic_thumb_up);
                imageButtonDownVote.setBackgroundResource(R.drawable.ic_thumb_down);
            }

            imageButtonUpVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataManager.db.collection(DataManager.FEEDBACK)
                            .whereEqualTo("userEmail", feedback.getUserEmail())
                            .whereEqualTo("cadeiraName", feedback.getCadeiraName())
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            DocumentReference ref = queryDocumentSnapshots.getDocuments().get(0).getReference();

                            boolean userUpVoted = feedback.getUpVotes().contains(Session.userEmail);
                            boolean userDownVoted = feedback.getDownVotes().contains(Session.userEmail);

                            if (userUpVoted) {
                                feedback.getUpVotes().remove(Session.userEmail);
                                feedback.downVote(1);
                                imageButtonUpVote.setBackgroundResource(R.drawable.ic_thumb_up);
                                ref.update("votes", feedback.getVotes());
                                ref.update("upVotes", feedback.getUpVotes());
                            }
                            else if (userDownVoted) {
                                feedback.getUpVotes().add(Session.userEmail);
                                feedback.getDownVotes().remove(Session.userEmail);
                                feedback.upVote(2);
                                imageButtonUpVote.setBackgroundResource(R.drawable.ic_thumb_up_filled);
                                imageButtonDownVote.setBackgroundResource(R.drawable.ic_thumb_down);
                                ref.update("votes", feedback.getVotes());
                                ref.update("upVotes", feedback.getUpVotes());
                                ref.update("downVotes", feedback.getDownVotes());
                            }
                            else {
                                feedback.getUpVotes().add(Session.userEmail);
                                feedback.upVote(1);
                                imageButtonUpVote.setBackgroundResource(R.drawable.ic_thumb_up_filled);
                                ref.update("votes", feedback.getVotes());
                                ref.update("upVotes", feedback.getUpVotes());
                            }
                            votesTextView.setText(String.valueOf(feedback.getVotes()));
                        }
                    });
                }
            });

            imageButtonDownVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataManager.db.collection(DataManager.FEEDBACK)
                            .whereEqualTo("userEmail", feedback.getUserEmail())
                            .whereEqualTo("cadeiraName", feedback.getCadeiraName())
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            DocumentReference ref = queryDocumentSnapshots.getDocuments().get(0).getReference();

                            boolean userUpVoted = feedback.getUpVotes().contains(Session.userEmail);
                            boolean userDownVoted = feedback.getDownVotes().contains(Session.userEmail);

                            if (userUpVoted) {
                                feedback.getUpVotes().remove(Session.userEmail);
                                feedback.getDownVotes().add(Session.userEmail);
                                feedback.downVote(2);
                                imageButtonDownVote.setBackgroundResource(R.drawable.ic_thumb_down_filled);
                                imageButtonUpVote.setBackgroundResource(R.drawable.ic_thumb_up);
                                ref.update("votes", feedback.getVotes());
                                ref.update("upVotes", feedback.getUpVotes());
                                ref.update("downVotes", feedback.getDownVotes());
                            }
                            else if (userDownVoted) {
                                feedback.getDownVotes().remove(Session.userEmail);
                                feedback.upVote(1);
                                imageButtonDownVote.setBackgroundResource(R.drawable.ic_thumb_down);
                                ref.update("votes", feedback.getVotes());
                                ref.update("downVotes", feedback.getDownVotes());
                            }
                            else {
                                feedback.getDownVotes().add(Session.userEmail);
                                feedback.downVote(1);
                                imageButtonDownVote.setBackgroundResource(R.drawable.ic_thumb_down_filled);
                                ref.update("votes", feedback.getVotes());
                                ref.update("downVotes", feedback.getDownVotes());
                            }
                            votesTextView.setText(String.valueOf(feedback.getVotes()));
                        }
                    });
                }
            });
        }
    }
}
