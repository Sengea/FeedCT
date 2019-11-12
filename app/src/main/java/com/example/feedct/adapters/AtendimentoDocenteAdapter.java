package com.example.feedct.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedct.R;
import com.example.feedct.jsonpojos.AtendimentoDocente;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AtendimentoDocenteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<AtendimentoDocente> atendimentoDocentes;

    public AtendimentoDocenteAdapter() {
        atendimentoDocentes = new LinkedList<>();
    }

    public void setData(List<AtendimentoDocente> data) {
        if (data == null)
            return;

        atendimentoDocentes.clear();
        atendimentoDocentes.addAll(data);
        Collections.sort(atendimentoDocentes);

        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.atendimento_docente_item, parent, false);
        return new AtendimentoDocenteAdapter.MyItem(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((AtendimentoDocenteAdapter.MyItem) holder).setup(atendimentoDocentes.get(position));
    }

    @Override
    public int getItemCount() {
        return atendimentoDocentes.size();
    }

    public static class MyItem extends RecyclerView.ViewHolder {
        private TextView professorTextView;
        private TextView diaTextView;
        private TextView horarioTextView;
        private TextView salaTextView;

        public MyItem(@NonNull View itemView) {
            super(itemView);

            professorTextView = itemView.findViewById(R.id.textViewProfessor);
            diaTextView = itemView.findViewById(R.id.textViewDia);
            horarioTextView = itemView.findViewById(R.id.textViewHorario);
            salaTextView = itemView.findViewById(R.id.textViewSala);
        }

        public void setup(AtendimentoDocente atendimentoDocente) {
            professorTextView.setText(atendimentoDocente.getProfessor());
            diaTextView.setText(atendimentoDocente.getDia());
            horarioTextView.setText(atendimentoDocente.getHoraInicio() + "-" + atendimentoDocente.getHoraFim());
            salaTextView.setText(atendimentoDocente.getSala());
        }
    }
}
