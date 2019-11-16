package com.example.feedct.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedct.activities.CadeiraActivity;
import com.example.feedct.pojos.Cadeira;
import com.example.feedct.Departamento;
import com.example.feedct.R;

import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class TodasAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int SECTION = 0;
    private static final int ELEMENT = 1;

    private Context mContext;

    private HashMap<Integer, Integer> typeByRow;
    private HashMap<Integer, Departamento> departamentoByRow;
    private HashMap<Integer, Cadeira> cadeiraByRow;

    private SortedSet<Departamento> current_departamentos;

    public TodasAdapter(Context context) {
        mContext = context;
        current_departamentos = new TreeSet<>();
        typeByRow = new HashMap<>();
        departamentoByRow = new HashMap<>();
        cadeiraByRow = new HashMap<>();
    }

    public void setData(SortedSet<Departamento> data) {
        if (data == null)
            return;

        current_departamentos.clear();
        current_departamentos.addAll(data);

        typeByRow.clear();
        int nRows = 0;

        for (Departamento departamento : current_departamentos) {
            for (int semestre = 1; semestre <= 2; semestre++) {
                List<Cadeira> cadeirasSem = departamento.getCadeirasBySem(semestre);

                if (cadeirasSem.size() > 0) {
                    departamentoByRow.put(nRows, departamento);
                    typeByRow.put(nRows, SECTION);
                    nRows++;

                    for (Cadeira cadeira : cadeirasSem) {
                        cadeiraByRow.put(nRows, cadeira);
                        typeByRow.put(nRows, ELEMENT);
                        nRows++;
                    }
                }
            }
        }

        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder itemViewHolder;

        if (viewType == SECTION) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_layout, parent, false);
            itemViewHolder = new MySection(v);
        }
        else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cadeira_layout, parent, false);
            itemViewHolder = new MyElement(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CadeiraActivity.class);
                    intent.putExtra("Cadeira", ((TextView)v.findViewById(R.id.nome)).getText());
                    mContext.startActivity(intent);
                }
            });
        }



        return itemViewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        return typeByRow.get(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder.getItemViewType() == SECTION) {
            Departamento departamento = departamentoByRow.get(position);
            int semestre = cadeiraByRow.get(position + 1).getSemestre();
            ((MySection) holder).setup(departamento, semestre);
        }
        else {
            ((MyElement) holder).setup(cadeiraByRow.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return typeByRow.size();
    }

    public static class MyElement extends RecyclerView.ViewHolder {

        private TextView siglaTextView;
        private TextView nomeTextView;
        private TextView departamentoTextView;
        private TextView semestreTextView;
        private TextView creditosTextView;
        private RatingBar ratingBar;

        public MyElement(@NonNull View itemView) {
            super(itemView);

            siglaTextView = itemView.findViewById(R.id.sigla);
            nomeTextView = itemView.findViewById(R.id.nome);
            departamentoTextView = itemView.findViewById(R.id.departamento);
            semestreTextView = itemView.findViewById(R.id.semestre);
            creditosTextView = itemView.findViewById(R.id.creditos);
            ratingBar =  itemView.findViewById(R.id.rating);
        }

        public void setup(Cadeira cadeira) {
            siglaTextView.setText(cadeira.getSigla());
            nomeTextView.setText(cadeira.getNome());
            departamentoTextView.setText(cadeira.getDepartamento());
            semestreTextView.setText(cadeira.getSemestre() + "º SEM");
            creditosTextView.setText(cadeira.getCreditos() + " ECTS");
            ratingBar.setRating(cadeira.getRating());
        }
    }

    public static class MySection extends RecyclerView.ViewHolder {

        private TextView departamentoSemTextView;

        public MySection(@NonNull View itemView) {
            super(itemView);
            departamentoSemTextView = itemView.findViewById(R.id.departamentoSemTextView);
        }

        public void setup(Departamento departamento, int semestre) {
            departamentoSemTextView.setText(String.format(itemView.getContext().getString(R.string.todas_section_format), departamento.getName(), semestre));
        }
    }
}
