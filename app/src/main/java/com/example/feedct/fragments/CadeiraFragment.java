package com.example.feedct.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedct.JSONManager;
import com.example.feedct.R;
import com.example.feedct.Session;
import com.example.feedct.adapters.AtendimentoDocenteAdapter;
import com.example.feedct.jsonpojos.AtendimentoDocente;
import com.example.feedct.jsonpojos.Cadeira;
import com.example.feedct.jsonpojos.CadeiraUser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class CadeiraFragment extends Fragment {
    private Cadeira cadeira;
    private List<AtendimentoDocente> atendimentoDocente;
    private CadeiraUser currentCadeiraUser;

    private AtendimentoDocenteAdapter adapter;

    public CadeiraFragment(Cadeira cadeira) {
        this.cadeira = cadeira;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cadeira, container, false);

        atendimentoDocente = JSONManager.atendimentoDocentesByCadeira.get(cadeira.getNome());

        TextView nome = view.findViewById(R.id.textViewNome);
        TextView departamento = view.findViewById(R.id.textViewDepartamento);
        TextView semestre = view.findViewById(R.id.textViewSemestre);
        TextView ects = view.findViewById(R.id.textViewECTS);
        RatingBar ratingBar = view.findViewById(R.id.ratingBar);

        nome.setText(cadeira.getNome());
        departamento.setText(cadeira.getDepartamento());
        semestre.setText(cadeira.getSemestre() + "º");
        ects.setText(String.valueOf(cadeira.getCreditos()));
        ratingBar.setRating(cadeira.getRating());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewAtendimentoDocente);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new AtendimentoDocenteAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setData(atendimentoDocente);

        if(atendimentoDocente == null || atendimentoDocente.isEmpty())
            view.findViewById(R.id.labelAtendimentoDocente).setVisibility(View.GONE);

        final FloatingActionButton actionButtonInscrever = view.findViewById(R.id.actionButtonInscrever);
        final FloatingActionButton actionButtonDesinscrever = view.findViewById(R.id.actionButtonDesinscrever);

        currentCadeiraUser = null;
        for (CadeiraUser cadeiraUser : JSONManager.cadeiraUsers) {
            if(cadeiraUser.getEmailUser().equals(Session.userEmail) && cadeiraUser.getNomeCadeira().equals(cadeira.getNome())) {
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

                final NumberPicker numberPicker = new NumberPicker(context);
                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(15);
                builder.setView(numberPicker);

                builder.setPositiveButton("Inscrever", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int turno = numberPicker.getValue();
                        currentCadeiraUser = new CadeiraUser(cadeira.getNome(), Session.userEmail);
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
                builder.setTitle("Tem a certeza que se quer desinscrever a " + cadeira.getNome());

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

        return view;
    }


}