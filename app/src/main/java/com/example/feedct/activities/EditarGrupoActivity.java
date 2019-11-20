package com.example.feedct.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

public class EditarGrupoActivity extends AppCompatActivity {
    private CriarGrupoAdapter adapter;
    private Grupo grupo;
    private List<String> initialConvites;
    private List<String> convites;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_editar_grupo);

        final FrameLayout loadingScreen = findViewById(R.id.loadingScreen);
        getSupportActionBar().setTitle("Editar Grupo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final FloatingActionButton floatingActionButtonLeave = findViewById(R.id.floatingActionButtonLeave);
        final FloatingActionButton floatingActionButtonConfirm = findViewById(R.id.floatingActionButtonConfirm);
        floatingActionButtonLeave.hide();
        floatingActionButtonConfirm.hide();

        //Setup recycler view
        final RecyclerView recyclerView = findViewById(R.id.recyclerViewConvitesGrupo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final NumberPicker numberPickerMaxElementos = findViewById(R.id.numberPickerMaxElementos);
        final LinearLayout linearLayoutElementos = findViewById(R.id.linearLayoutElementos);

        final String grupoId = getIntent().getStringExtra("GrupoId");
        final String cadeiraName = getIntent().getStringExtra("Cadeira");

        final ImageButton imageButtonAdicionarConvite = findViewById(R.id.imageButtonAdicionarConvite);
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

                        DataManager.db.collection(DataManager.GRUPOS).whereEqualTo("cadeira",cadeiraName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<User> grupoUsers = new ArrayList<>();
                                final List<Grupo> grupos = new ArrayList<>(queryDocumentSnapshots.getDocuments().size());
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

                                DataManager.db.collection(DataManager.GRUPOS).document(grupoId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        grupo = documentSnapshot.toObject(Grupo.class);
                                        numberPickerMaxElementos.setMinValue(grupo.getElementos().size());
                                        numberPickerMaxElementos.setValue(grupo.getMaxElementos());

                                        DataManager.db.collection(DataManager.PEDIDOS_GRUPO).whereEqualTo("sender", grupoId).whereEqualTo("type", PedidoGrupo.GROUP_TO_USER).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                initialConvites = new ArrayList<>();
                                                convites = new ArrayList<>();
                                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                                    String convite = documentSnapshot.toObject(PedidoGrupo.class).getReceiver();
                                                    initialConvites.add(convite);
                                                    convites.add(convite);
                                                }

                                                final List<User> usersConvidados = new ArrayList<>();
                                                final ArrayList<SearchableUser> stringUsersConvidados = new ArrayList<>();
                                                final ArrayList<SearchableUser> userNames = new ArrayList<>(users.size());
                                                for (User user : users) {
                                                    if (!convites.contains(user.getEmail()))
                                                        userNames.add(new SearchableUser(user.getNome() + " - " + user.getNumero(), user));
                                                    else {
                                                        usersConvidados.add(user);
                                                        stringUsersConvidados.add(new SearchableUser(user.getNome() + " - " + user.getNumero(), user));
                                                    }
                                                }

                                                Collections.sort(userNames);

                                                DataManager.db.collection(DataManager.USERS).whereIn("email", grupo.getElementos()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                                            User user = documentSnapshot.toObject(User.class);
                                                            View viewElemento = LayoutInflater.from(EditarGrupoActivity.this).inflate(R.layout.layout_elemento_grupo, null);
                                                            ((TextView) viewElemento.findViewById(R.id.textViewNomeElemento)).setText(user.getNome() + " - " + user.getNumero());
                                                            linearLayoutElementos.addView(viewElemento);
                                                        }

                                                        adapter = new CriarGrupoAdapter(convites, userNames);
                                                        recyclerView.setAdapter(adapter);
                                                        adapter.setData(usersConvidados, stringUsersConvidados);
                                                        floatingActionButtonLeave.show();
                                                        floatingActionButtonConfirm.show();
                                                        loadingScreen.setVisibility(View.GONE);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });


        imageButtonAdicionarConvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (grupo.getElementos().size() < grupo.getMaxElementos())
                    new SimpleSearchDialogCompat<>(EditarGrupoActivity.this, "Procurar...", "Nome ou número do aluno...?", null, adapter.getUserNames(), new SearchResultListener<SearchableUser>() {
                        @Override
                        public void onSelected(BaseSearchDialogCompat dialog, SearchableUser item, int position) { dialog.dismiss();
                            adapter.addElement(item.getUser(), item);
                        }
                    }).show();
                else {
                    Toast toast = Toast.makeText(v.getContext(), "Não pode enviar mais convites. O número máximo de elementos já foi atingido.", Toast.LENGTH_LONG);
                    toast.show();
                }
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


        numberPickerMaxElementos.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (grupo != null)
                    grupo.setMaxElementos(newVal);
            }
        });

        floatingActionButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Tem a certeza que deseja gravar as alterações efetuadas a este grupo?");

                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataManager.db.collection(DataManager.GRUPOS).document(grupoId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                int mode = grupo.getMode();
                                int maxElementos = grupo.getMaxElementos();

                                for (String convite : convites) {
                                    if (!initialConvites.contains(convite))
                                        DataManager.db.collection(DataManager.PEDIDOS_GRUPO).add(new PedidoGrupo(cadeiraName, PedidoGrupo.GROUP_TO_USER, grupoId, convite));
                                }

                                List<String> convitesToRemove = new ArrayList<>();
                                for (String convite : initialConvites) {
                                    if (!convites.contains(convite)) {
                                        convitesToRemove.add(convite);
                                    }
                                }

                                if (!convitesToRemove.isEmpty()) {
                                    DataManager.db.collection(DataManager.PEDIDOS_GRUPO).whereEqualTo("sender", grupoId).whereIn("receiver", convitesToRemove).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                                                documentSnapshot.getReference().delete();
                                        }
                                    });
                                }

                                DocumentReference ref = documentSnapshot.getReference();
                                ref.update("mode", mode);
                                ref.update("maxElementos", maxElementos);

                                onBackPressed();
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

        floatingActionButtonLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Tem a certeza que deseja sair este grupo?");

                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataManager.db.collection(DataManager.GRUPOS).document(grupoId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                                if (grupo.getElementos().size() == 1) {
                                    documentSnapshot.getReference().delete();
                                    DataManager.db.collection(DataManager.PEDIDOS_GRUPO).whereEqualTo("sender", grupoId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                                documentSnapshot.getReference().delete();
                                            }

                                            DataManager.db.collection(DataManager.PEDIDOS_GRUPO).whereEqualTo("receiver", grupoId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                                        documentSnapshot.getReference().delete();
                                                    }
                                                    onBackPressed();
                                                }
                                            });
                                        }
                                    });
                                }
                                else {
                                    List<String> elementos = grupo.getElementos();
                                    elementos.remove(Session.userEmail);
                                    documentSnapshot.getReference().update("elementos", elementos);
                                    onBackPressed();
                                }
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
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
