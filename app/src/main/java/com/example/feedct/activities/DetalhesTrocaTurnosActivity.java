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
import com.example.feedct.pojos.PedidoTurnos;
import com.example.feedct.pojos.TrocaTurnos;
import com.example.feedct.pojos.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DetalhesTrocaTurnosActivity extends AppCompatActivity {
    private User user;
    private TrocaTurnos trocaTurnos;
    private String trocaTurnosId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detalhes_troca_turnos);

        final FrameLayout loadingScreen = findViewById(R.id.loadingScreen);

        getSupportActionBar().setTitle("Pedido de Troca de Turnos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String userEmail = getIntent().getStringExtra("UserEmail");
        final String cadeiraName = getIntent().getStringExtra("Cadeira");
        final String userTurno = getIntent().getStringExtra("UserTurno");
        final boolean joinable = getIntent().getBooleanExtra("Joinable", false);

        final TextView textViewNomeAluno = findViewById(R.id.textViewNomeAluno);
        final TextView textViewTem = findViewById(R.id.textViewTem);
        final TextView textViewQuer = findViewById(R.id.textViewQuer);
        final FloatingActionButton floatingActionButtonSend = findViewById(R.id.floatingActionButtonSend);

        floatingActionButtonSend.hide();

        loadingScreen.setVisibility(View.VISIBLE);
        DataManager.db.collection(DataManager.TROCA_TURNOS).whereEqualTo("cadeira", cadeiraName).whereEqualTo("userEmail", userEmail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                trocaTurnos = documentSnapshot.toObject(TrocaTurnos.class);
                trocaTurnosId = documentSnapshot.getId();

                textViewTem.setText(String.valueOf(trocaTurnos.getTenho()));
                StringBuilder queroStringBuilder = new StringBuilder();
                for (String quero : trocaTurnos.getProcuro()) {
                    queroStringBuilder.append(quero + ", ");
                }
                String queroString = queroStringBuilder.toString();
                queroString = queroString.substring(0, queroString.length() - 2);
                textViewQuer.setText(queroString);

                DataManager.db.collection(DataManager.USERS).whereEqualTo("email", userEmail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        user = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);

                        textViewNomeAluno.setText(user.getNome());

                        if (joinable)
                            floatingActionButtonSend.show();
                        else
                            floatingActionButtonSend.hide();

                        loadingScreen.setVisibility(View.GONE);
                    }
                });
            }
        });

        floatingActionButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetalhesTrocaTurnosActivity.this);
                builder.setTitle("Enviar Pedido?");
                builder.setMessage("Quer enviar um pedido de troca de turnos a " + user.getNome() + "? Abdicaria do turno " + userTurno + " pelo turno " + trocaTurnos.getTenho()+ ".");

                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadingScreen.setVisibility(View.VISIBLE);
                        DataManager.db.collection(DataManager.PEDIDOS_TURNOS).add(new PedidoTurnos(cadeiraName, Session.userEmail, trocaTurnosId)).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                floatingActionButtonSend.hide();
                                loadingScreen.setVisibility(View.GONE);
                                Toast toast = Toast.makeText(DetalhesTrocaTurnosActivity.this,"Pedido enviado com sucesso.", Toast.LENGTH_SHORT);
                                toast.show();
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
        finish();
        return super.onOptionsItemSelected(item);
    }




}
