package com.example.feedct.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.feedct.DataManager;
import com.example.feedct.Departamento;
import com.example.feedct.R;
import com.example.feedct.Session;
import com.example.feedct.activities.CriarTrocaTurnosActivity;
import com.example.feedct.activities.DetalhesGrupoActivity;
import com.example.feedct.activities.EditarGrupoActivity;
import com.example.feedct.adapters.TurnosAdapter;
import com.example.feedct.comparators.TrocaTurnosComparator;
import com.example.feedct.pojos.Cadeira;
import com.example.feedct.pojos.CadeiraUser;
import com.example.feedct.pojos.PedidoGrupo;
import com.example.feedct.pojos.PedidoTurnos;
import com.example.feedct.pojos.TrocaTurnos;
import com.example.feedct.pojos.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TurnosFragment extends Fragment {
    private Cadeira cadeira;
    private CadeiraUser cadeiraUser;
    private List<TrocaTurnos> trocaTurnosList;
    private HashMap<Integer, Integer> priorityByTurno;

    private TurnosAdapter adapter;
    private boolean[] procuroTurnoIsFiltered;
    private int tenhoTurnoFilter;
    private boolean userHasTroca;

    private Button buttonProcuro;
    private ImageButton imageButtonCancelProcuro;
    private Button buttonTenho;
    private ImageButton imageButtonCancelTenho;
    private FloatingActionButton floatingActionButtonAdd;
    private FloatingActionButton floatingActionButtonDelete;

    private FrameLayout loadingScreen;

    public TurnosFragment(Cadeira cadeira, CadeiraUser cadeiraUser) {
        this.cadeira = cadeira;
        this.cadeiraUser = cadeiraUser;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_turnos, container, false);

        loadingScreen = view.findViewById(R.id.loadingScreen);

        //Setup recycler view
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewTurnos);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        adapter = new TurnosAdapter();
        recyclerView.setAdapter(adapter);

        buttonProcuro = view.findViewById(R.id.buttonProcuroFilter);
        imageButtonCancelProcuro = view.findViewById(R.id.imageButtonCancelProcuro);

        buttonTenho = view.findViewById(R.id.buttonTenhoFilter);
        imageButtonCancelTenho = view.findViewById(R.id.imageButtonCancelTenho);

        floatingActionButtonAdd = view.findViewById(R.id.floatingActionButtonAdd);
        floatingActionButtonDelete = view.findViewById(R.id.floatingActionButtonDelete);

        floatingActionButtonAdd.hide();
        floatingActionButtonDelete.hide();

        procuroTurnoIsFiltered = new boolean[15];
        tenhoTurnoFilter = -1;

        buttonProcuro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Procuro");

                final List<String> items = new ArrayList<>(procuroTurnoIsFiltered.length);
                for(int i = 1; i <= procuroTurnoIsFiltered.length; i++) {
                    items.add(String.valueOf(i));
                }

                builder.setMultiChoiceItems(items.toArray(new String[0]), procuroTurnoIsFiltered, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked)
                    {
                        String tmp = ((AlertDialog) dialog).getListView().getItemAtPosition(which).toString();
                        if (isChecked) {
                            if (priorityByTurno == null)
                                priorityByTurno = new HashMap<>();

                            priorityByTurno.put(which + 1, priorityByTurno.size());
                        }
                        else {
                            priorityByTurno.remove(which + 1);

                            for (Map.Entry<Integer, Integer> entry : priorityByTurno.entrySet()) {
                                priorityByTurno.put(entry.getKey(), entry.getValue() - 1);
                            }
                            if (priorityByTurno.size() == 0)
                                priorityByTurno = null;
                        }

                        procuroTurnoIsFiltered[which] = isChecked;
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

        buttonTenho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Tenho");

                final List<String> items = new ArrayList<>(procuroTurnoIsFiltered.length);
                for(int i = 1; i <= procuroTurnoIsFiltered.length; i++) {
                    items.add(String.valueOf(i));
                }

                builder.setItems(items.toArray(new String[0]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tenhoTurnoFilter = which + 1;
                        buttonTenho.setText(String.valueOf(tenhoTurnoFilter));
                        imageButtonCancelTenho.setVisibility(View.VISIBLE);

                        updateAdapterData();
                    }
                });

                builder.show();
            }
        });

        imageButtonCancelProcuro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < procuroTurnoIsFiltered.length; i++) {
                    procuroTurnoIsFiltered[i] = false;
                }

                buttonProcuro.setText(getString(R.string.procuroFilter));
                imageButtonCancelProcuro.setVisibility(View.GONE);

                priorityByTurno = null;

                updateAdapterData();
            }
        });

        imageButtonCancelTenho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tenhoTurnoFilter = -1;

                buttonTenho.setText(getString(R.string.tenhoFilter));
                imageButtonCancelTenho.setVisibility(View.GONE);

                updateAdapterData();
            }
        });

        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CriarTrocaTurnosActivity.class);
                intent.putExtra("Cadeira", cadeiraUser.getNomeCadeira());
                v.getContext().startActivity(intent);
            }
        });

        floatingActionButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
                builder.setTitle("Tem a certeza que deseja eliminar o seu pedido de troca de turnos?");

                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataManager.db.collection(DataManager.TROCA_TURNOS).whereEqualTo("userEmail", Session.userEmail).whereEqualTo("cadeira", cadeira.getNome()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                final WriteBatch batch = DataManager.db.batch();
                                batch.delete(queryDocumentSnapshots.getDocuments().get(0).getReference());
                                DataManager.db.collection(DataManager.PEDIDOS_TURNOS).whereEqualTo("sender", Session.userEmail).whereEqualTo("cadeira", cadeira.getNome()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                            batch.delete(documentSnapshot.getReference());
                                        }

                                        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                updateAdapterData();
                                            }
                                        });
                                    }
                                });
                            }
                        });
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

    @Override
    public void onStart() {
        super.onStart();
        updateAdapterData();
    }

    private List<TrocaTurnos> applyTenhoFilter(List<TrocaTurnos> trocasTurnos) {
        if (tenhoTurnoFilter == -1) {
            imageButtonCancelTenho.setVisibility(View.GONE);
            buttonTenho.setText(getString(R.string.tenhoFilter));
            return trocasTurnos;
        }

        List<TrocaTurnos> filteredTrocaTurnos = new ArrayList<>(trocasTurnos.size());
        for (TrocaTurnos trocaTurnos : trocasTurnos) {
            if (trocaTurnos.getProcuro().contains(String.valueOf(tenhoTurnoFilter)))
                filteredTrocaTurnos.add(trocaTurnos);
        }

        imageButtonCancelTenho.setVisibility(View.VISIBLE);
        buttonTenho.setText(String.valueOf(tenhoTurnoFilter));

        return filteredTrocaTurnos;
    }

    private List<TrocaTurnos> applyPrucuroFilter(List<TrocaTurnos> trocasTurnos) {
        if (priorityByTurno == null) {
            imageButtonCancelProcuro.setVisibility(View.GONE);
            buttonProcuro.setText(getString(R.string.procuroFilter));
            return trocasTurnos;
        }

        String[] aux = new String[priorityByTurno.size()];
        boolean isFiltered = false;
        for (int i = 0; i < procuroTurnoIsFiltered.length; i++) {
            if (procuroTurnoIsFiltered[i]) {
                aux[priorityByTurno.get(i + 1)] = String.valueOf(i + 1);
            }
        }

        StringBuilder filterText = new StringBuilder();
        for (int i = 0; i < aux.length; i++) {
            filterText.append(aux[i] + " ");
        }

        List<TrocaTurnos> filteredTrocaTurnos = new ArrayList<>(trocasTurnos.size());
        for (TrocaTurnos trocaTurnos : trocasTurnos) {
            for (int turno = 1; turno <= procuroTurnoIsFiltered.length; turno++) {
                if(procuroTurnoIsFiltered[turno - 1] && trocaTurnos.getTenho().equals(String.valueOf(turno))) {
                    filteredTrocaTurnos.add(trocaTurnos);
                    break;
                }
            }
        }

        filterText = new StringBuilder(filterText.toString().trim().replace(" ", ", "));
        imageButtonCancelProcuro.setVisibility(View.VISIBLE);
        buttonProcuro.setText(filterText.toString());

        return filteredTrocaTurnos;
    }

    private void updateAdapterData() {
        loadingScreen.setVisibility(View.VISIBLE);
        userHasTroca = false;
        DataManager.db.collection(DataManager.PEDIDOS_TURNOS).whereEqualTo("sender", Session.userEmail).whereEqualTo("cadeira", cadeira.getNome()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                final List<String> trocaTurnosIds = new ArrayList<>(queryDocumentSnapshots.getDocuments().size());
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    trocaTurnosIds.add(documentSnapshot.toObject(PedidoTurnos.class).getTrocaTurnosId());
                }

                DataManager.db.collection(DataManager.TROCA_TURNOS).whereEqualTo("cadeira", cadeira.getNome()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        final Map<TrocaTurnos, String> idByTroca = new HashMap<>();
                        trocaTurnosList = new ArrayList<>(queryDocumentSnapshots.getDocuments().size());
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            if (!trocaTurnosIds.contains(documentSnapshot.getId())) {
                                TrocaTurnos trocaTurnos = documentSnapshot.toObject(TrocaTurnos.class);
                                if (trocaTurnos.getUserEmail().equals(Session.userEmail))
                                    userHasTroca = true;
                                trocaTurnosList.add(trocaTurnos);
                                idByTroca.put(trocaTurnos, documentSnapshot.getId());
                            }
                        }

                        trocaTurnosList = applyPrucuroFilter(trocaTurnosList);
                        trocaTurnosList = applyTenhoFilter(trocaTurnosList);

                        Collections.sort(trocaTurnosList, new TrocaTurnosComparator(cadeiraUser.getTurno(), priorityByTurno));

                        List<String> userEmails = new ArrayList<>(trocaTurnosList.size());
                        for (TrocaTurnos trocaTurnos : trocaTurnosList) {
                            userEmails.add(trocaTurnos.getUserEmail());
                        }

                        if (trocaTurnosList.isEmpty()) {
                            adapter.setData(trocaTurnosList, new HashMap<TrocaTurnos, User>(), idByTroca, cadeiraUser);
                            loadingScreen.setVisibility(View.GONE);
                        }
                        else {
                            DataManager.db.collection("users").whereIn("email", userEmails).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    Map<TrocaTurnos, User> userByTrocaTurnos = new HashMap<>();
                                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                        User user = documentSnapshot.toObject(User.class);
                                        for (TrocaTurnos trocaTurnos : trocaTurnosList) {
                                            if(trocaTurnos.getUserEmail().equals(user.getEmail())) {
                                                userByTrocaTurnos.put(trocaTurnos, user);
                                                break;
                                            }
                                        }
                                    }
                                    adapter.setData(trocaTurnosList, userByTrocaTurnos, idByTroca, cadeiraUser);

                                    if (userHasTroca) {
                                        floatingActionButtonAdd.hide();
                                        floatingActionButtonDelete.show();
                                    }
                                    else {
                                        floatingActionButtonAdd.show();
                                        floatingActionButtonDelete.hide();
                                    }

                                    loadingScreen.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}
