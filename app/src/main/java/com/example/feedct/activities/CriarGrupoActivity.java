package com.example.feedct.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

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
import com.example.feedct.pojos.CadeiraUser;
import com.example.feedct.pojos.Grupo;
import com.example.feedct.pojos.PedidoGrupo;
import com.example.feedct.pojos.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;

public class CriarGrupoActivity extends AppCompatActivity {
    private CriarGrupoAdapter adapter;
    private Grupo grupo;
    private List<String> convites;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_criar_grupo);
        getSupportActionBar().setTitle("Criar Grupo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setup recycler view
        final RecyclerView recyclerView = findViewById(R.id.recyclerViewConvitesGrupo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final String cadeiraName = getIntent().getStringExtra("Cadeira");

        final ImageButton imageButtonAddElement = findViewById(R.id.imageButtonAdicionarConvite);
        DataManager.db.collection(DataManager.CADEIRA_USER).whereEqualTo("nomeCadeira", cadeiraName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> userEmails = new ArrayList<>(queryDocumentSnapshots.getDocuments().size());
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    userEmails.add(documentSnapshot.toObject(CadeiraUser.class).getEmailUser());
                }

                DataManager.db.collection(DataManager.USERS).whereIn("email", userEmails).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        final List<User> users = new ArrayList<>(queryDocumentSnapshots.getDocuments().size());
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            users.add(documentSnapshot.toObject(User.class));
                        }
                        DataManager.db.collection(DataManager.GRUPOS).whereEqualTo("cadeira", cadeiraName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<Grupo> grupos = new ArrayList<>(queryDocumentSnapshots.getDocuments().size());
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                    grupos.add(documentSnapshot.toObject(Grupo.class));
                                    for (String elementoEmail : documentSnapshot.toObject(Grupo.class).getElementos()) {
                                        for (int i = 0; i < users.size(); i++) {
                                            String userEmail = users.get(i).getEmail();
                                            if (userEmail.equals(elementoEmail)) {
                                                users.remove(i);
                                                break;
                                            }
                                        }
                                    }
                                }

                                ArrayList<SearchableUser> userNames = new ArrayList<>(users.size());
                                for (User user : users) {
                                    if (!user.getEmail().equals(Session.userEmail))
                                        userNames.add(new SearchableUser(user.getNome() + " - " + user.getNumero(), user));
                                }
                                Collections.sort(userNames);

                                grupo = new Grupo(cadeiraName, Session.userEmail);
                                convites = new ArrayList<>();
                                adapter = new CriarGrupoAdapter(convites, userNames);
                                recyclerView.setAdapter(adapter);
                            }
                        });
                    }
                });
            }
        });

        imageButtonAddElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SimpleSearchDialogCompat<>(CriarGrupoActivity.this, "Procurar...", "Nome ou número do aluno...?", null, adapter.getUserNames(), new SearchResultListener<SearchableUser>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat dialog, SearchableUser item, int position) {
                        dialog.dismiss();
                        adapter.addElement(item.getUser(), item);
                    }
                }).show();
            }
        });

        AppCompatSpinner spinnerTurnos = findViewById(R.id.spinnerTurnos);
        ArrayAdapter<CharSequence> turnosAdapter = ArrayAdapter.createFromResource(this, R.array.turnosOptions, android.R.layout.simple_spinner_item);
        turnosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTurnos.setAdapter(turnosAdapter);
        spinnerTurnos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (grupo != null)
                    grupo.setTurnos(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        AppCompatSpinner spinnerModo = findViewById(R.id.spinnerModo);
        ArrayAdapter<CharSequence> modoAdapter = ArrayAdapter.createFromResource(this, R.array.modoOptions, android.R.layout.simple_spinner_item);
        modoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModo.setAdapter(modoAdapter);
        spinnerModo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (grupo != null)
                    grupo.setMode(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        NumberPicker numberPickerMaxElementos = findViewById(R.id.numberPickerMaxElementos);
        numberPickerMaxElementos.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (grupo != null)
                    grupo.setMaxElementos(newVal);
            }
        });

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButtonConfirm);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataManager.db.collection(DataManager.GRUPOS).add(grupo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(final DocumentReference documentReference) {
                        DataManager.db.collection(DataManager.PEDIDOS_GRUPO).whereEqualTo("sender", Session.userEmail).whereEqualTo("cadeira", cadeiraName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                    documentSnapshot.getReference().delete();
                                }

                                for(String convite : convites)
                                    DataManager.db.collection(DataManager.PEDIDOS_GRUPO).add(new PedidoGrupo(cadeiraName, PedidoGrupo.GROUP_TO_USER, documentReference.getId(), convite));
                                onBackPressed();
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
