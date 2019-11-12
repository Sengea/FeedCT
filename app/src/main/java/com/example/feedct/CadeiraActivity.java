package com.example.feedct;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedct.adapters.AtendimentoDocenteAdapter;
import com.example.feedct.jsonpojos.AtendimentoDocente;
import com.example.feedct.jsonpojos.Cadeira;
import com.example.feedct.jsonpojos.CadeiraUser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class CadeiraActivity extends AppCompatActivity {
    private Cadeira cadeira;
    private List<AtendimentoDocente> atendimentoDocente;
    private CadeiraUser currentCadeiraUser;

    private AtendimentoDocenteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadeira);

        final String nomeCadeira = getIntent().getStringExtra("Cadeira");

        cadeira = JSONManager.cadeiraByName.get(nomeCadeira);
        atendimentoDocente = JSONManager.atendimentoDocentesByCadeira.get(nomeCadeira);

        getSupportActionBar().setTitle(cadeira.getSigla());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final TextView nome = findViewById(R.id.textViewNome);
        TextView departamento = findViewById(R.id.textViewDepartamento);
        TextView semestre = findViewById(R.id.textViewSemestre);
        TextView ects = findViewById(R.id.textViewECTS);
        RatingBar ratingBar = findViewById(R.id.ratingBar);

        nome.setText(nomeCadeira);
        departamento.setText(cadeira.getDepartamento());
        semestre.setText(cadeira.getSemestre() + "º");
        ects.setText(String.valueOf(cadeira.getCreditos()));
        ratingBar.setRating(cadeira.getRating());

        RecyclerView recyclerView = findViewById(R.id.recyclerViewAtendimentoDocente);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new AtendimentoDocenteAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setData(atendimentoDocente);

        final FloatingActionButton actionButtonInscrever = findViewById(R.id.actionButtonInscrever);
        final FloatingActionButton actionButtonDesinscrever = findViewById(R.id.actionButtonDesinscrever);

        currentCadeiraUser = null;
        for (CadeiraUser cadeiraUser : JSONManager.cadeiraUsers) {
            if(cadeiraUser.getEmailUser().equals(Session.userEmail) && cadeiraUser.getNomeCadeira().equals(nomeCadeira)) {
                currentCadeiraUser = cadeiraUser;
                break;
            }
        }

        if (currentCadeiraUser == null)
            actionButtonInscrever.show();
        else
            actionButtonDesinscrever.show();

        actionButtonInscrever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = v.getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Insira o número do turno prático:");

                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                builder.setPositiveButton("Inscrever", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String turno = input.getText().toString();
                        currentCadeiraUser = new CadeiraUser(nomeCadeira, Session.userEmail);
                        JSONManager.cadeiraUsers.add(currentCadeiraUser);

                        actionButtonDesinscrever.show();
                        actionButtonInscrever.hide();
                        Toast toast = Toast.makeText(context,"Inscrito no turno " + turno + " com sucesso.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        actionButtonDesinscrever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = v.getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Tem a certeza que se quer desinscrever a " + nomeCadeira);

                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JSONManager.cadeiraUsers.remove(currentCadeiraUser);

                        actionButtonInscrever.show();
                        actionButtonDesinscrever.hide();
                        Toast toast = Toast.makeText(context,"Desinscrito com sucesso.", Toast.LENGTH_SHORT);
                        toast.show();
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
