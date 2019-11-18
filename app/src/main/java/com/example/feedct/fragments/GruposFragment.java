package com.example.feedct.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.feedct.DataManager;
import com.example.feedct.Departamento;
import com.example.feedct.R;
import com.example.feedct.Session;
import com.example.feedct.activities.CadeiraActivity;
import com.example.feedct.activities.CriarGrupoActivity;
import com.example.feedct.activities.RegisterActivity;
import com.example.feedct.adapters.GruposAdapter;
import com.example.feedct.adapters.TodasAdapter;
import com.example.feedct.cadeiracomparators.GrupoComparator;
import com.example.feedct.pojos.Cadeira;
import com.example.feedct.pojos.CadeiraUser;
import com.example.feedct.pojos.Grupo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;


public class GruposFragment extends Fragment {
    private Cadeira cadeira;

    private GruposAdapter adapter;
    private boolean[] turnoIsFiltered;

    private Button buttonTurnos;
    private ImageButton imageButtonCancelTurnos;
    private FloatingActionButton floatingActionButton;

    public GruposFragment(Cadeira cadeira) {
        this.cadeira = cadeira;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grupos, container, false);

        //Setup recycler view
        RecyclerView recyclerView = view.findViewById(R.id.gruposRecyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        adapter = new GruposAdapter(cadeira);
        recyclerView.setAdapter(adapter);

        floatingActionButton = view.findViewById(R.id.actionButtonCriarGrupo);
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
        DataManager.db.collection("cadeiraUser").whereEqualTo("emailUser", Session.userEmail).whereEqualTo("nomeCadeira", cadeira.getNome()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                final String userTurno = queryDocumentSnapshots.getDocuments().get(0).toObject(CadeiraUser.class).getTurno();
                DataManager.db.collection("grupos").whereEqualTo("cadeira", cadeira.getNome()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Map<Grupo, String> idByGrupo = new HashMap<>();
                        List<Grupo> grupoList = new ArrayList<>(queryDocumentSnapshots.getDocuments().size());
                        boolean userHasGroup = false;
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                            Grupo grupo = documentSnapshot.toObject(Grupo.class);
                            grupoList.add(grupo);
                            idByGrupo.put(grupo, documentSnapshot.getId());
                            if (grupo.getElementos().contains(Session.userEmail))
                                userHasGroup = true;
                        }

                        if (userHasGroup)
                            floatingActionButton.hide();
                        else
                            floatingActionButton.show();

                        grupoList = applyTurnosFilter(grupoList);
                        Collections.sort(grupoList, new GrupoComparator(userTurno));
                        adapter.setData(grupoList, idByGrupo);
                    }
                });

            }
        });
    }
}
