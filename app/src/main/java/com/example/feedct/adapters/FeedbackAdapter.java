package com.example.feedct.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedct.JSONManager;
import com.example.feedct.R;
import com.example.feedct.Session;
import com.example.feedct.jsonpojos.Cadeira;
import com.example.feedct.jsonpojos.Feedback;
import com.example.feedct.jsonpojos.User;

import java.text.SimpleDateFormat;
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
            final User user = JSONManager.userByEmail.get(feedback.getUserEmail());
            final Cadeira cadeira = JSONManager.cadeiraByName.get(feedback.getCadeiraName());

            nameTextView.setText(user.getNome());
            dateTextView.setText(new SimpleDateFormat("dd/MM/yyyy").format(feedback.getDate()));
            opinionTextView.setText(feedback.getOpinion());
            cursoTextView.setText(user.getCurso());
            votesTextView.setText(String.valueOf(feedback.getVotes()));

            Integer voteType = feedback.getVoteByUserEmail(Session.userEmail);

            if (voteType != null) {
                if (voteType == Feedback.UP_VOTE) {
                    imageButtonUpVote.setBackgroundResource(R.drawable.ic_up_arrow_filled);
                } else if (voteType == Feedback.DOWN_VOTE) {
                    imageButtonDownVote.setBackgroundResource(R.drawable.ic_down_arrow_filled);
                }
            }

            imageButtonUpVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer voteType = feedback.getVoteByUserEmail(Session.userEmail);

                    if (voteType == null) {
                        feedback.setVoteByUserEmail(Session.userEmail, Feedback.UP_VOTE);
                        feedback.upVote();
                        imageButtonUpVote.setBackgroundResource(R.drawable.ic_up_arrow_filled);
                    }
                    else if (voteType == Feedback.DOWN_VOTE) {
                        feedback.setVoteByUserEmail(Session.userEmail, Feedback.UP_VOTE);
                        feedback.upVote(); feedback.upVote();
                        imageButtonUpVote.setBackgroundResource(R.drawable.ic_up_arrow_filled);
                        imageButtonDownVote.setBackgroundResource(R.drawable.ic_down_arrow_empty);
                    }
                    else {
                        feedback.setVoteByUserEmail(Session.userEmail, null);
                        feedback.downVote();
                        imageButtonUpVote.setBackgroundResource(R.drawable.ic_up_arrow_empty);
                    }

                    votesTextView.setText(String.valueOf(feedback.getVotes()));
                }
            });

            imageButtonDownVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer voteType = feedback.getVoteByUserEmail(Session.userEmail);

                    if (voteType == null) {
                        feedback.setVoteByUserEmail(Session.userEmail, Feedback.DOWN_VOTE);
                        feedback.downVote();
                        imageButtonDownVote.setBackgroundResource(R.drawable.ic_down_arrow_filled);
                    }
                    else if (voteType == Feedback.UP_VOTE) {
                        feedback.setVoteByUserEmail(Session.userEmail, Feedback.DOWN_VOTE);
                        feedback.downVote(); feedback.downVote();
                        imageButtonDownVote.setBackgroundResource(R.drawable.ic_down_arrow_filled);
                        imageButtonUpVote.setBackgroundResource(R.drawable.ic_up_arrow_empty);
                    }
                    else {
                        feedback.setVoteByUserEmail(Session.userEmail, null);
                        feedback.upVote();
                        imageButtonDownVote.setBackgroundResource(R.drawable.ic_down_arrow_empty);
                    }

                    votesTextView.setText(String.valueOf(feedback.getVotes()));
                }
            });
        }
    }
}
