package com.example.feedct.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedct.DataManager;
import com.example.feedct.R;
import com.example.feedct.SearchableUser;
import com.example.feedct.Session;
import com.example.feedct.adapters.CriarGrupoAdapter;
import com.example.feedct.adapters.CriarTrocaTurnosAdapter;
import com.example.feedct.pojos.CadeiraUser;
import com.example.feedct.pojos.Grupo;
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
import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;

public class CriarTrocaTurnosActivity extends AppCompatActivity {
    private CriarTrocaTurnosAdapter adapter;
    private CadeiraUser cadeiraUser;
    private List<String> procuro;
    private List<Integer> options;

    public CriarTrocaTurnosActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_criar_troca_turnos);
        getSupportActionBar().setTitle("Criar Pedido");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String cadeiraName = getIntent().getStringExtra("Cadeira");

        //Setup recycler view
        final RecyclerView recyclerView = findViewById(R.id.recyclerViewProcuro);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final TextView textViewTenho = findViewById(R.id.textViewTenho);


        options = new ArrayList<>(15);
        for (int i = 1; i <= 15; i++) {
            options.add(i);
        }

        procuro = new ArrayList<>(16);
        adapter = new CriarTrocaTurnosAdapter(procuro, options);
        recyclerView.setAdapter(adapter);

        DataManager.db.collection(DataManager.CADEIRA_USER).whereEqualTo("emailUser", Session.userEmail).whereEqualTo("nomeCadeira", cadeiraName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                cadeiraUser = queryDocumentSnapshots.getDocuments().get(0).toObject(CadeiraUser.class);
                textViewTenho.setText(String.valueOf(cadeiraUser.getTurno()));
            }
        });

        final ImageButton imageButtonAddProcuro = findViewById(R.id.imageButtonAddProcuro);
        imageButtonAddProcuro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Procuro (Opção " + (procuro.size() + 1)  + ")");

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                String[] optionsString = new String[options.size()];
                for (int i = 0; i < options.size(); i++) {
                    optionsString[i] = String.valueOf(options.get(i));
                }
                builder.setItems(optionsString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String turno = ((AlertDialog) dialog).getListView().getItemAtPosition(which).toString();
                        adapter.addElement(turno);
                    }
                });

                builder.show();
            }
        });

        final FloatingActionButton floatingActionButtonConfirm = findViewById(R.id.floatingActionButtonConfirm);
        floatingActionButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (procuro.isEmpty()) {
                    AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                    alertDialog.setMessage("Pelo menos um turno deve ser selecionado em \"Procuro\"");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                else {
                    DataManager.db.collection(DataManager.TROCA_TURNOS).whereArrayContains("procuro", cadeiraUser.getTurno()).whereIn("tenho", procuro).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            final Map<Integer, List<TrocaTurnos>> trocaTurnosByPriority = new HashMap<>();
                            final Map<TrocaTurnos, String> idByTrocaTurnos = new HashMap<>();
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                TrocaTurnos trocaTurnos = documentSnapshot.toObject(TrocaTurnos.class);
                                for (int i = 0; i < procuro.size(); i++) {
                                    if (procuro.get(i).equals(String.valueOf(trocaTurnos.getTenho()))) {
                                        List<TrocaTurnos> value = trocaTurnosByPriority.get(i);
                                        if (value == null) {
                                            value = new ArrayList<>();
                                            trocaTurnosByPriority.put(i, value);
                                        }
                                        value.add(trocaTurnos);
                                        idByTrocaTurnos.put(trocaTurnos, documentSnapshot.getId());
                                        break;
                                    }
                                }
                            }

                            if (!trocaTurnosByPriority.isEmpty()) {
                                final List<String> userEmails = new ArrayList<>();
                                for (int i = 0; i < procuro.size(); i++) {
                                    List<TrocaTurnos> aux = trocaTurnosByPriority.get(i);
                                    if (aux != null && !aux.isEmpty()) {
                                        for (TrocaTurnos trocaTurnos : aux) {
                                            userEmails.add(trocaTurnos.getUserEmail());
                                        }
                                    }
                                }
                                DataManager.db.collection(DataManager.USERS).whereIn("email", userEmails).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        Map<String, User> userByEmail = new HashMap<>();
                                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                            User user = documentSnapshot.toObject(User.class);
                                            userByEmail.put(user.getEmail(), user);
                                        }

                                        List<String> items = new ArrayList<>();
                                        final List<TrocaTurnos> trocaTurnosList = new ArrayList<>();
                                        for (int i = 0; i < procuro.size(); i++) {
                                            List<TrocaTurnos> aux = trocaTurnosByPriority.get(i);
                                            if (aux != null && !aux.isEmpty()) {
                                                for (TrocaTurnos trocaTurnos : aux) {
                                                    User user = userByEmail.get(trocaTurnos.getUserEmail());
                                                    items.add(user.getNome() + " - " + String.valueOf(user.getNumero()) + ": " + "turno " + trocaTurnos.getTenho());
                                                    trocaTurnosList.add(trocaTurnos);
                                                }
                                            }
                                        }

                                        final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                        builder.setTitle("Trocas Compatíveis");

                                        final boolean[] sendRequest = new boolean[items.size()];

                                        builder.setMultiChoiceItems(items.toArray(new String[0]), sendRequest, new DialogInterface.OnMultiChoiceClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which, boolean isChecked)
                                            {
                                                //String tmp = ((AlertDialog) dialog).getListView().getItemAtPosition(which).toString();
                                                sendRequest[which] = isChecked;
                                            }
                                        });

                                        builder.setPositiveButton("Enviar Pedidos", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (sendRequest.length > 0) {
                                                    WriteBatch batch = DataManager.db.batch();

                                                    for (int i = 0; i < sendRequest.length; i++) {
                                                        if (sendRequest[i]) {
                                                            DocumentReference ref = DataManager.db.collection(DataManager.PEDIDOS_TURNOS).document();
                                                            batch.set(ref, new PedidoTurnos(cadeiraName, Session.userEmail, idByTrocaTurnos.get(trocaTurnosList.get(i))));
                                                        }
                                                    }

                                                    batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            DataManager.db.collection(DataManager.TROCA_TURNOS).add(new TrocaTurnos(cadeiraName, Session.userEmail, procuro, cadeiraUser.getTurno())).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                @Override
                                                                public void onSuccess(DocumentReference documentReference) {
                                                                    onBackPressed();
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                                else {
                                                    DataManager.db.collection(DataManager.TROCA_TURNOS).add(new TrocaTurnos(cadeiraName, Session.userEmail, procuro, cadeiraUser.getTurno())).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            onBackPressed();
                                                        }
                                                    });
                                                }
                                            }
                                        });

                                        builder.setNegativeButton("Não Enviar Pedidos", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                DataManager.db.collection(DataManager.TROCA_TURNOS).add(new TrocaTurnos(cadeiraName, Session.userEmail, procuro, cadeiraUser.getTurno())).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        onBackPressed();
                                                    }
                                                });
                                            }
                                        });

                                        builder.show();
                                    }
                                });
                            }
                            else {
                                DataManager.db.collection(DataManager.TROCA_TURNOS).add(new TrocaTurnos(cadeiraName, Session.userEmail, procuro, cadeiraUser.getTurno())).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        onBackPressed();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
