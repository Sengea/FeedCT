package com.example.feedct.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.feedct.DataManager;
import com.example.feedct.R;
import com.example.feedct.Session;
import com.example.feedct.activities.CadeiraActivity;
import com.example.feedct.adapters.AtendimentoDocenteAdapter;
import com.example.feedct.adapters.SectionsPageAdapter;
import com.example.feedct.pojos.AtendimentoDocente;
import com.example.feedct.pojos.Cadeira;
import com.example.feedct.pojos.CadeiraUser;
import com.example.feedct.pojos.Grupo;
import com.example.feedct.pojos.PedidoTurnos;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CadeiraFragment extends Fragment {
    private Cadeira cadeira;
    private CadeiraActivity activity;
    private FrameLayout loadingScreen;

    private CadeiraUser currentCadeiraUser;

    private AtendimentoDocenteAdapter adapter;

    public CadeiraFragment(Cadeira cadeira, CadeiraActivity activity) {
        this.cadeira = cadeira;
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_cadeira, container, false);

        loadingScreen = view.findViewById(R.id.loadingScreen);
        loadingScreen.setVisibility(View.VISIBLE);

        TextView nomeTextView = view.findViewById(R.id.textViewNome);
        TextView departamentoTextView = view.findViewById(R.id.textViewDepartamento);
        TextView semestreTextView = view.findViewById(R.id.textViewSemestre);
        TextView ectsTextView = view.findViewById(R.id.textViewECTS);
        RatingBar ratingBarTextView = view.findViewById(R.id.ratingBar);

        nomeTextView.setText(cadeira.getNome());
        departamentoTextView.setText(cadeira.getDepartamento());
        semestreTextView.setText(cadeira.getSemestre() + "º");
        ectsTextView.setText(String.valueOf(cadeira.getCreditos()));
        ratingBarTextView.setRating(cadeira.getRating());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewAtendimentoDocente);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new AtendimentoDocenteAdapter();
        recyclerView.setAdapter(adapter);

        final FloatingActionButton actionButtonInscrever = view.findViewById(R.id.actionButtonInscrever);
        final FloatingActionButton actionButtonDesinscrever = view.findViewById(R.id.actionButtonDesinscrever);
        final TextView turnoTextView = view.findViewById(R.id.textViewTurno);

        DataManager.db.collection(DataManager.CADEIRA_USER).whereEqualTo("emailUser", Session.userEmail).whereEqualTo("nomeCadeira", cadeira.getNome()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                if (documentSnapshots.size() == 1) {
                    currentCadeiraUser = documentSnapshots.get(0).toObject(CadeiraUser.class);
                    actionButtonDesinscrever.show();
                    turnoTextView.setText(String.format(getString(R.string.inscrito_em), currentCadeiraUser.getTurno()));
                    turnoTextView.setVisibility(View.VISIBLE);
                }
                else {
                    currentCadeiraUser = null;
                    actionButtonInscrever.show();
                    turnoTextView.setVisibility(View.INVISIBLE);
                }

                DataManager.db.collection(DataManager.ATENDIMENTO_DOCENTE).whereEqualTo("cadeira", cadeira.getNome()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<AtendimentoDocente> atendimentoDocentes = new LinkedList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            atendimentoDocentes.add(document.toObject(AtendimentoDocente.class));
                        }

                        if(atendimentoDocentes.isEmpty())
                            view.findViewById(R.id.labelAtendimentoDocente).setVisibility(View.GONE);

                        adapter.setData(atendimentoDocentes);

                        loadingScreen.setVisibility(View.GONE);
                    }
                });
            }
        });

        actionButtonInscrever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = v.getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Insira o número do turno prático:");

                final NumberPicker numberPicker = new NumberPicker(context);
                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(15);
                builder.setView(numberPicker);

                builder.setPositiveButton("Inscrever", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String turno = String.valueOf(numberPicker.getValue());
                        currentCadeiraUser = new CadeiraUser(cadeira.getNome(), Session.userEmail, turno);
                        DataManager.db.collection(DataManager.CADEIRA_USER).add(currentCadeiraUser);
                        turnoTextView.setText(String.format(getString(R.string.inscrito_em), currentCadeiraUser.getTurno()));
                        turnoTextView.setVisibility(View.VISIBLE);

                        actionButtonDesinscrever.show();
                        actionButtonInscrever.hide();
                        Toast toast = Toast.makeText(context,"Inscrito no turno " + turno + " com sucesso.", Toast.LENGTH_SHORT);
                        toast.show();

                        activity.showExtraTabs(currentCadeiraUser);
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        actionButtonDesinscrever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = v.getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Tem a certeza que se quer desinscrever a " + cadeira.getNome());

                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataManager.db.collection(DataManager.CADEIRA_USER).whereEqualTo("emailUser", Session.userEmail).whereEqualTo("nomeCadeira", cadeira.getNome()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                queryDocumentSnapshots.getDocuments().get(0).getReference().delete();
                                DataManager.db.collection(DataManager.GRUPOS).whereEqualTo("cadeira", cadeira.getNome()).whereArrayContains("elementos", Session.userEmail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (queryDocumentSnapshots.getDocuments().size() > 0) {
                                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                            final String grupoID = documentSnapshot.getId();
                                            Grupo grupo = documentSnapshot.toObject(Grupo.class);
                                            if (grupo.getElementos().size() == 1) {
                                                documentSnapshot.getReference().delete();
                                                //Eliminate group to user and group to group requests
                                                DataManager.db.collection(DataManager.PEDIDOS_GRUPO).whereEqualTo("cadeira", cadeira.getNome()).whereEqualTo("sender", grupoID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments())
                                                            documentSnapshot.getReference().delete();

                                                        DataManager.db.collection(DataManager.PEDIDOS_GRUPO).whereEqualTo("cadeira", cadeira.getNome()).whereEqualTo("receiver", grupoID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments())
                                                                    documentSnapshot.getReference().delete();
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                            else {
                                                // Remove user from group
                                                List<String> elementos = grupo.getElementos();
                                                elementos.remove(Session.userEmail);
                                                documentSnapshot.getReference().update("elementos", elementos);
                                            }

                                        }
                                        else {
                                            DataManager.db.collection(DataManager.PEDIDOS_GRUPO).whereEqualTo("cadeira", cadeira.getNome()).whereEqualTo("sender", Session.userEmail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments())
                                                        documentSnapshot.getReference().delete();

                                                    DataManager.db.collection(DataManager.PEDIDOS_GRUPO).whereEqualTo("cadeira", cadeira.getNome()).whereEqualTo("receiver", Session.userEmail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments())
                                                                documentSnapshot.getReference().delete();
                                                        }
                                                    });
                                                }
                                            });
                                        }

                                        DataManager.db.collection(DataManager.TROCA_TURNOS).whereEqualTo("cadeira", cadeira.getNome()).whereEqualTo("userEmail", Session.userEmail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                if (queryDocumentSnapshots.getDocuments().size() > 0) {
                                                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                                    final String trocaTurnosId = documentSnapshot.getId();
                                                    documentSnapshot.getReference().delete();
                                                    DataManager.db.collection(DataManager.PEDIDOS_TURNOS).whereEqualTo("cadeira", cadeira.getNome()).whereEqualTo("trocaTurnosId", trocaTurnosId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments())
                                                                documentSnapshot.getReference().delete();
                                                        }
                                                    });
                                                }
                                                DataManager.db.collection(DataManager.PEDIDOS_TURNOS).whereEqualTo("cadeira", cadeira.getNome()).whereEqualTo("sender", Session.userEmail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments())
                                                            documentSnapshot.getReference().delete();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        turnoTextView.setVisibility(View.INVISIBLE);

                        actionButtonInscrever.show();
                        actionButtonDesinscrever.hide();
                        Toast toast = Toast.makeText(context,"Desinscrito com sucesso.", Toast.LENGTH_SHORT);
                        toast.show();

                        activity.hideExtraTabs();
                    }
                });

                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });



        return view;
    }
}
