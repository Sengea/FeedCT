package com.example.feedct.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedct.DataManager;
import com.example.feedct.R;
import com.example.feedct.adapters.FeedbackAdapter;
import com.example.feedct.cadeiracomparators.FeedbackDateComparator;
import com.example.feedct.cadeiracomparators.FeedbackVotesComparator;
import com.example.feedct.pojos.Cadeira;
import com.example.feedct.pojos.Curso;
import com.example.feedct.pojos.Feedback;
import com.example.feedct.pojos.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FeedbackFragment extends Fragment {
    private Cadeira cadeira;
    private boolean[] cursoIsFiltered;
    private List<Feedback> feedback;
    private Map<String, List<Feedback>> feedbackByCurso;
    private List<Feedback> currentFeedback;
    private Comparator<Feedback> currentComparator;

    private FeedbackAdapter adapter;

    public FeedbackFragment(Cadeira cadeira) {
        this.cadeira = cadeira;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewFeedback);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new FeedbackAdapter();
        recyclerView.setAdapter(adapter);

        RadioGroup radioGroup = view.findViewById(R.id.radioGroupOrderBy);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButtonDate:
                        currentComparator = new FeedbackDateComparator();
                        adapter.setData(currentFeedback, currentComparator);
                        break;
                    case R.id.radioButtonVotes:
                        currentComparator = new FeedbackVotesComparator();
                        adapter.setData(currentFeedback, currentComparator);
                        break;
                }
            }
        });

        cursoIsFiltered = new boolean[DataManager.cursos.size()];

        final Button buttonCurso = view.findViewById(R.id.buttonCurso);
        final ImageButton imageButtonCancelCurso = view.findViewById(R.id.imageButtonCancelCurso);
        imageButtonCancelCurso.setVisibility(View.GONE);

        buttonCurso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Cursos");

                final List<String> items = new LinkedList<>();
                for (Curso curso : DataManager.cursos)
                    items.add(curso.getSigla());

                builder.setMultiChoiceItems(items.toArray(new String[0]), cursoIsFiltered, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        cursoIsFiltered[which] = isChecked;
                    }
                });

                builder.setPositiveButton("Filtrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        applyCursoFilter(buttonCurso, imageButtonCancelCurso);
                    }
                });

                builder.show();
            }
        });

        imageButtonCancelCurso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < cursoIsFiltered.length; i++) {
                    cursoIsFiltered[i] = false;
                }

                buttonCurso.setText(getString(R.string.cursoFilter));
                imageButtonCancelCurso.setVisibility(View.GONE);

                currentFeedback = feedback;
                adapter.setData(currentFeedback, currentComparator);
            }
        });


        return view;
    }

    private void applyCursoFilter(Button buttonCurso, ImageButton imageButtonCancelCurso) {
        StringBuilder filterText = new StringBuilder();
        currentFeedback = new LinkedList<>();
        int i = 0;

        for (Curso curso : DataManager.cursos) {
            if (cursoIsFiltered[i++]) {
                List<Feedback> f = feedbackByCurso.get(curso.getSigla());
                if (f != null)
                    currentFeedback.addAll(f);

                filterText.append(" ").append(curso.getSigla());
            }
        }

        if (currentFeedback.isEmpty()) {
            currentFeedback = feedback;
            filterText = new StringBuilder(getString(R.string.cursoFilter));
            imageButtonCancelCurso.setVisibility(View.GONE);
        }
        else {
            filterText = new StringBuilder(filterText.toString().trim().replace(" ", ", "));
            imageButtonCancelCurso.setVisibility(View.VISIBLE);
        }

        buttonCurso.setText(filterText.toString());

        adapter.setData(currentFeedback, currentComparator);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateFeedback();
    }

    private void updateFeedback() {
        DataManager.db.collection("feedback").whereEqualTo("cadeiraName", cadeira.getNome()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                feedback = new LinkedList<>();
                feedbackByCurso = new HashMap<>();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    final Feedback f = documentSnapshot.toObject(Feedback.class);
                    feedback.add(f);
                    DataManager.db.collection("users").whereEqualTo("email", f.getUserEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            String curso = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class).getCurso();

                            List<Feedback> aux = feedbackByCurso.get(curso);
                            if(aux == null) {
                                aux = new LinkedList<>();
                                feedbackByCurso.put(curso, aux);
                            }

                            aux.add(f);
                        }
                    });
                }
                currentFeedback = feedback;
                currentComparator = new FeedbackVotesComparator();
                adapter.setData(currentFeedback, currentComparator);
            }
        });
    }
}
