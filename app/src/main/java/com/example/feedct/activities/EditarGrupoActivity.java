package com.example.feedct.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.feedct.pojos.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_editar_grupo);
        getSupportActionBar().setTitle("Editar Grupo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setup recycler view
        final RecyclerView recyclerView = findViewById(R.id.recyclerViewConvitesGrupo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final NumberPicker numberPickerMaxElementos = findViewById(R.id.numberPickerMaxElementos);
        final LinearLayout linearLayoutElementos = findViewById(R.id.linearLayoutElementos);

        final String grupoId = getIntent().getStringExtra("GrupoId");
        final String cadeiraName = getIntent().getStringExtra("Cadeira");

        final ImageButton imageButtonAdicionarConvite = findViewById(R.id.imageButtonAdicionarConvite);
        DataManager.db.collection("cadeiraUser").whereEqualTo("nomeCadeira", cadeiraName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> userEmails = new ArrayList<>(queryDocumentSnapshots.getDocuments().size());
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    userEmails.add(documentSnapshot.toObject(CadeiraUser.class).getEmailUser());
                }

                DataManager.db.collection("users").whereIn("email", userEmails).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        final List<User> users = new ArrayList<>(queryDocumentSnapshots.getDocuments().size());
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            users.add(documentSnapshot.toObject(User.class));
                        }

                        DataManager.db.collection("grupos").whereEqualTo("cadeira",cadeiraName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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

                                DataManager.db.collection("grupos").document(grupoId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        grupo = documentSnapshot.toObject(Grupo.class);
                                        if (grupo.getConvites() == null)
                                            grupo.initializeConvites();
                                        numberPickerMaxElementos.setMinValue(grupo.getElementos().size());
                                        numberPickerMaxElementos.setValue(grupo.getMaxElementos());

                                        final List<User> usersConvidados = new ArrayList<>();
                                        final ArrayList<SearchableUser> stringUsersConvidados = new ArrayList<>();
                                        final ArrayList<SearchableUser> userNames = new ArrayList<>(users.size());
                                        for (User user : users) {
                                            if (!grupo.getConvites().contains(user.getEmail()))
                                                userNames.add(new SearchableUser(user.getNome() + " - " + user.getNumero(), user));
                                            else {
                                                usersConvidados.add(user);
                                                stringUsersConvidados.add(new SearchableUser(user.getNome() + " - " + user.getNumero(), user));
                                            }
                                        }

                                        Collections.sort(userNames);

                                        DataManager.db.collection("users").whereIn("email", grupo.getElementos()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                                    User user = documentSnapshot.toObject(User.class);
                                                    View viewElemento = LayoutInflater.from(EditarGrupoActivity.this).inflate(R.layout.layout_elemento_grupo, null);
                                                    ((TextView) viewElemento.findViewById(R.id.textViewNomeElemento)).setText(user.getNome() + " - " + user.getNumero());
                                                    linearLayoutElementos.addView(viewElemento);
                                                }

                                                adapter = new CriarGrupoAdapter(grupo, userNames);
                                                recyclerView.setAdapter(adapter);
                                                adapter.setData(usersConvidados, stringUsersConvidados);
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

        FloatingActionButton floatingActionButtonConfirm = findViewById(R.id.floatingActionButtonConfirm);
        floatingActionButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Tem a certeza que deseja gravar as alterações efetuadas a este grupo?");

                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataManager.db.collection("grupos").document(grupoId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                documentSnapshot.getReference().delete();
                                DataManager.db.collection("grupos").add(grupo);
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

        FloatingActionButton floatingActionButtonDelete = findViewById(R.id.floatingActionButtonDelete);
        floatingActionButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Tem a certeza que deseja eliminar este grupo?");

                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataManager.db.collection("grupos").document(grupoId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                documentSnapshot.getReference().delete();
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
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
