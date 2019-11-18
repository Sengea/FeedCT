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
import com.example.feedct.pojos.Pedido;
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

public class DetalhesGrupoActivity extends AppCompatActivity {
    private CriarGrupoAdapter adapter;
    private Grupo grupo;
    private String currentUserGrupoId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detalhes_grupo);
        getSupportActionBar().setTitle("Detalhes Grupo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String grupoId = getIntent().getStringExtra("GrupoId");
        final String cadeiraName = getIntent().getStringExtra("Cadeira");

        final LinearLayout linearLayoutElementos = findViewById(R.id.linearLayoutElementos);
        final TextView textViewModo = findViewById(R.id.textViewModo);
        final TextView textViewMaxMembros = findViewById(R.id.textViewMaxMembros);
        final TextView textViewTurnos = findViewById(R.id.textViewTurnos);
        final FloatingActionButton floatingActionButtonSend = findViewById(R.id.floatingActionButtonSend);
        final FloatingActionButton floatingActionButtonMerge = findViewById(R.id.floatingActionButtonMerge);

        DataManager.db.collection("grupos").document(grupoId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                grupo = documentSnapshot.toObject(Grupo.class);

                switch (grupo.getMode()) {
                    case Grupo.MODE_REQUESTS:
                        textViewModo.setText("Aceitam-se pedidos de junção");
                        break;
                    case Grupo.MODE_AUTOMATIC:
                        textViewModo.setText("Aceitam-se pedidos automaticamente");
                        break;
                    case Grupo.MODE_NO_REQUESTS:
                        textViewModo.setText("Não se aceitam pedidos");
                        break;
                }
                textViewMaxMembros.setText(String.valueOf(grupo.getMaxElementos()));
                textViewTurnos.setText(grupo.getTurnos());

                DataManager.db.collection("users").whereIn("email", grupo.getElementos()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            User user = documentSnapshot.toObject(User.class);
                            View viewElemento = LayoutInflater.from(DetalhesGrupoActivity.this).inflate(R.layout.layout_elemento_grupo, null);
                            ((TextView) viewElemento.findViewById(R.id.textViewNomeElemento)).setText(user.getNome() + " - " + user.getNumero());
                            linearLayoutElementos.addView(viewElemento);
                        }
                    }
                });

                //TODO: ver se a pessoa pode fazer tem grupo para decidir se pode fazer merge ou enviar pedido

                DataManager.db.collection("grupos").whereEqualTo("cadeira", cadeiraName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        currentUserGrupoId = null;
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            Grupo currentGrupo = documentSnapshot.toObject(Grupo.class);
                            if (currentGrupo.getElementos().contains(Session.userEmail)) {
                                currentUserGrupoId = documentSnapshot.getId();
                                break;
                            }
                        }

                        List<String> aux = new ArrayList<>(2);
                        aux.add(Session.userEmail);
                        if (currentUserGrupoId != null)
                            aux.add(currentUserGrupoId);
                        DataManager.db.collection("pedidos").whereIn("sender", aux).whereEqualTo("receiver", grupoId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (queryDocumentSnapshots.getDocuments().size() > 0) {
                                    //Ja ha pedido
                                    floatingActionButtonMerge.hide();
                                    floatingActionButtonSend.hide();
                                }
                                else {
                                    if (currentUserGrupoId == null) {
                                        floatingActionButtonMerge.hide();
                                        floatingActionButtonSend.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(final View v) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                                builder.setTitle("Quer enviar um pedido de junção para este grupo?");

                                                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Toast toast = Toast.makeText(v.getContext(), "Pedido enviado com sucesso.", Toast.LENGTH_SHORT);
                                                        toast.show();
                                                        DataManager.db.collection("pedidos").add(new Pedido(Pedido.SINGLE, Session.userEmail, grupoId));
                                                        floatingActionButtonSend.hide();
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

                                        //TODO verificar se o user já enviou pedido
                                    }
                                    else {
                                        floatingActionButtonSend.hide();
                                        floatingActionButtonMerge.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(final View v) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                                builder.setTitle("Quer enviar um pedido de junção de grupos para este grupo?");

                                                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Toast toast = Toast.makeText(v.getContext(), "Pedido enviado com sucesso.", Toast.LENGTH_SHORT);
                                                        toast.show();
                                                        DataManager.db.collection("pedidos").add(new Pedido(Pedido.MERGE, currentUserGrupoId, grupoId));
                                                        floatingActionButtonMerge.hide();
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
                                }
                            }
                        });
                    }
                });
                //Pedido constituido por emailUser, id do grupo,
            }
        });

        /*FloatingActionButton floatingActionButtonSend = findViewById(R.id.floatingActionButtonSend);
        floatingActionButtonSend.setEnabled(false);
        floatingActionButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataManager.db.collection("grupos").add(grupo);
                onBackPressed();
            }
        });*/
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
