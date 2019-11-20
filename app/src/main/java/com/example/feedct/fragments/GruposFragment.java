package com.example.feedct.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.feedct.DataManager;
import com.example.feedct.R;
import com.example.feedct.Session;
import com.example.feedct.activities.CriarGrupoActivity;
import com.example.feedct.adapters.GruposAdapter;
import com.example.feedct.comparators.GrupoComparator;
import com.example.feedct.pojos.Cadeira;
import com.example.feedct.pojos.CadeiraUser;
import com.example.feedct.pojos.Grupo;
import com.example.feedct.pojos.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GruposFragment extends Fragment {
    private Cadeira cadeira;

    private GruposAdapter adapter;
    private boolean[] turnoIsFiltered;

    private Button buttonTurnos;
    private ImageButton imageButtonCancelTurnos;
    private FloatingActionButton floatingActionButton;

    private List<Grupo> grupoList;
    private Grupo userGrupo;

    private FrameLayout loadingScreen;

    public GruposFragment(Cadeira cadeira) {
        this.cadeira = cadeira;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grupos, container, false);

        loadingScreen = view.findViewById(R.id.loadingScreen);

        //Setup recycler view
        RecyclerView recyclerView = view.findViewById(R.id.gruposRecyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        adapter = new GruposAdapter(cadeira);
        recyclerView.setAdapter(adapter);

        floatingActionButton = view.findViewById(R.id.actionButtonCriarGrupo);
        floatingActionButton.hide();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CriarGrupoActivity.class);
                intent.putExtra("Cadeira", cadeira.getNome());
                v.getContext().startActivity(intent);
            }
        });

        turnoIsFiltered = new boolean[15];
        buttonTurnos = view.findViewById(R.id.buttonTurnos);
        buttonTurnos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Turnos");

                final String[] items = new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};

                builder.setMultiChoiceItems(items, turnoIsFiltered, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        turnoIsFiltered[which] = isChecked;
                    }
                });

                builder.setPositiveButton("Filtrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateAdapterData();
                    }
                });

                builder.show();
            }
        });

        imageButtonCancelTurnos = view.findViewById(R.id.imageButtonCancelTurnos);
        imageButtonCancelTurnos.setVisibility(View.GONE);
        imageButtonCancelTurnos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < turnoIsFiltered.length; i++) {
                    turnoIsFiltered[i] = false;
                }

                buttonTurnos.setText(getString(R.string.departamentoFilter));
                imageButtonCancelTurnos.setVisibility(View.GONE);

                updateAdapterData();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateAdapterData();
    }

    private List<Grupo> applyTurnosFilter(List<Grupo> grupos) {
        StringBuilder filterText = new StringBuilder();

        boolean isFiltered = false;
        for (int i = 0; i < turnoIsFiltered.length; i++) {
            if (turnoIsFiltered[i]) {
                isFiltered = true;
                filterText.append(" ").append(i + 1);
            }
        }

        if (!isFiltered) {
            imageButtonCancelTurnos.setVisibility(View.GONE);
            buttonTurnos.setText(getString(R.string.turnosFilter));
            return grupos;
        }

        List<Grupo> filteredGrupos = new ArrayList<>();
        for (Grupo grupo : grupos) {
            String turnos = grupo.getTurnos();
            for (int turno = 1; turno <= turnoIsFiltered.length; turno++) {
                if (turnos.equals("Todos") || (turnos.equals(String.valueOf(turno)) && turnoIsFiltered[turno - 1])) {
                    filteredGrupos.add(grupo);
                    break;
                }
            }
        }

        filterText = new StringBuilder(filterText.toString().trim().replace(" ", ", "));
        imageButtonCancelTurnos.setVisibility(View.VISIBLE);
        buttonTurnos.setText(filterText.toString());

        return filteredGrupos;
    }

    private void updateAdapterData() {
        loadingScreen.setVisibility(View.VISIBLE);
        DataManager.db.collection(DataManager.CADEIRA_USER).whereEqualTo("emailUser", Session.userEmail).whereEqualTo("nomeCadeira", cadeira.getNome()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                final String userTurno = queryDocumentSnapshots.getDocuments().get(0).toObject(CadeiraUser.class).getTurno();
                DataManager.db.collection(DataManager.GRUPOS).whereEqualTo("cadeira", cadeira.getNome()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        final Map<Grupo, String> idByGrupo = new HashMap<>();
                        grupoList = new ArrayList<>(queryDocumentSnapshots.getDocuments().size());
                        List<String> elementosList = new ArrayList<>();
                        userGrupo = null;
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            Grupo grupo = documentSnapshot.toObject(Grupo.class);
                            grupoList.add(grupo);
                            idByGrupo.put(grupo, documentSnapshot.getId());
                            if (grupo.getElementos().contains(Session.userEmail))
                                userGrupo = grupo;
                            elementosList.addAll(grupo.getElementos());
                        }


                        grupoList = applyTurnosFilter(grupoList);
                        Collections.sort(grupoList, new GrupoComparator(userTurno, userGrupo));

                        if (elementosList.isEmpty()) {
                            DataManager.db.collection(DataManager.CADEIRA_USER).whereEqualTo("emailUser", Session.userEmail).whereEqualTo("nomeCadeira", cadeira.getNome()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    CadeiraUser cadeiraUser = queryDocumentSnapshots.getDocuments().get(0).toObject(CadeiraUser.class);

                                    adapter.setData(grupoList, idByGrupo, new HashMap<Grupo, List<String>>(), cadeiraUser, userGrupo);

                                    if (userGrupo != null)
                                        floatingActionButton.hide();
                                    else
                                        floatingActionButton.show();

                                    loadingScreen.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            DataManager.db.collection(DataManager.USERS).whereIn("email", elementosList).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    final Map<Grupo, List<String>> userNamesByGrupo = new HashMap<>();
                                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                        User user = documentSnapshot.toObject(User.class);
                                        for (Grupo grupo : grupoList) {
                                            if (grupo.getElementos().contains(user.getEmail())) {
                                                List<String> userNames = userNamesByGrupo.get(grupo);
                                                if (userNames == null) {
                                                    userNames = new ArrayList<>(grupo.getElementos().size());
                                                    userNamesByGrupo.put(grupo, userNames);
                                                }
                                                userNames.add(user.getNome());
                                                break;
                                            }
                                        }
                                    }

                                    DataManager.db.collection(DataManager.CADEIRA_USER).whereEqualTo("emailUser", Session.userEmail).whereEqualTo("nomeCadeira", cadeira.getNome()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            CadeiraUser cadeiraUser = queryDocumentSnapshots.getDocuments().get(0).toObject(CadeiraUser.class);

                                            adapter.setData(grupoList, idByGrupo, userNamesByGrupo, cadeiraUser, userGrupo);

                                            if (userGrupo != null)
                                                floatingActionButton.hide();
                                            else
                                                floatingActionButton.show();

                                            loadingScreen.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}
