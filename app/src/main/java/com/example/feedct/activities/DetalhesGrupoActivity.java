package com.example.feedct.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.feedct.DataManager;
import com.example.feedct.R;
import com.example.feedct.Session;
import com.example.feedct.adapters.CriarGrupoAdapter;
import com.example.feedct.pojos.Grupo;
import com.example.feedct.pojos.PedidoGrupo;
import com.example.feedct.pojos.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DetalhesGrupoActivity extends AppCompatActivity {
    private CriarGrupoAdapter adapter;
    private Grupo grupo;
    private String currentUserGrupoId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detalhes_grupo);

        final FrameLayout loadingScreen = findViewById(R.id.loadingScreen);

        getSupportActionBar().setTitle("Detalhes Grupo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String grupoId = getIntent().getStringExtra("GrupoId");
        final String cadeiraName = getIntent().getStringExtra("Cadeira");
        final boolean joinable = getIntent().getBooleanExtra("Joinable", false);

        final LinearLayout linearLayoutElementos = findViewById(R.id.linearLayoutElementos);
        final TextView textViewModo = findViewById(R.id.textViewModo);
        final TextView textViewMaxMembros = findViewById(R.id.textViewMaxMembros);
        final TextView textViewTurnos = findViewById(R.id.textViewTurnos);
        final FloatingActionButton floatingActionButtonSend = findViewById(R.id.floatingActionButtonSend);
        final FloatingActionButton floatingActionButtonMerge = findViewById(R.id.floatingActionButtonMerge);

        floatingActionButtonSend.hide();
        floatingActionButtonMerge.hide();

        DataManager.db.collection(DataManager.GRUPOS).document(grupoId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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

                DataManager.db.collection(DataManager.USERS).whereIn("email", grupo.getElementos()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            User user = documentSnapshot.toObject(User.class);
                            View viewElemento = LayoutInflater.from(DetalhesGrupoActivity.this).inflate(R.layout.layout_elemento_grupo, null);
                            ((TextView) viewElemento.findViewById(R.id.textViewNomeElemento)).setText(user.getNome() + " - " + user.getNumero());
                            linearLayoutElementos.addView(viewElemento);
                        }

                        if (joinable) {
                            DataManager.db.collection(DataManager.GRUPOS).whereEqualTo("cadeira", cadeiraName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                                    DataManager.db.collection(DataManager.PEDIDOS_GRUPO).whereIn("sender", aux).whereEqualTo("receiver", grupoId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if (queryDocumentSnapshots.getDocuments().size() == 0) {
                                                if (currentUserGrupoId == null) {
                                                    floatingActionButtonSend.show();
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
                                                                    DataManager.db.collection(DataManager.PEDIDOS_GRUPO).add(new PedidoGrupo(cadeiraName, PedidoGrupo.USER_TO_GROUP, Session.userEmail, grupoId));
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
                                                } else {
                                                    floatingActionButtonMerge.show();
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
                                                                    DataManager.db.collection(DataManager.PEDIDOS_GRUPO).add(new PedidoGrupo(cadeiraName, PedidoGrupo.MERGE, currentUserGrupoId, grupoId));
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
                                            loadingScreen.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            });
                        }
                        else
                            loadingScreen.setVisibility(View.GONE);
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
