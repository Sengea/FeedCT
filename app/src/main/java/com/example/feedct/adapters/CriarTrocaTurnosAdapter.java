package com.example.feedct.adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedct.R;

import java.util.Collections;
import java.util.List;

public class CriarTrocaTurnosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> procuro;
    private List<Integer> options;

    public CriarTrocaTurnosAdapter(List<String> procuro, List<Integer> options) {
        this.procuro = procuro;
        this.options = options;
    }

    public void addElement(String turno) {
        options.remove(Integer.valueOf(Integer.parseInt(turno)));
        procuro.add(turno);
        this.notifyDataSetChanged();
    }

    public void removeElement(String turno) {
        options.add(Integer.parseInt(turno));
        Collections.sort(options);
        procuro.remove(turno);
        this.notifyDataSetChanged();
    }

    public void moveUp(int position) {
        Collections.swap(procuro, position, position - 1);
        this.notifyDataSetChanged();
    }

    public void moveDown(int position) {
        Collections.swap(procuro, position, position + 1);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_criar_troca_turno_procuro, parent, false);
        return new CriarTrocaTurnosAdapter.MyItem(v, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((CriarTrocaTurnosAdapter.MyItem) holder).setup(procuro.get(position), position + 1, procuro.size());
    }

    @Override
    public int getItemCount() {
        return procuro.size();
    }

    public static class MyItem extends RecyclerView.ViewHolder {
        private Context mContext;
        private CriarTrocaTurnosAdapter adapter;

        private TextView textViewPrioridade;
        private TextView textViewProcuro;
        private ImageButton imageButtonRemove;
        private ImageButton imageButtonMoveUp;
        private ImageButton imageButtonMoveDown;

        public MyItem(@NonNull View itemView, CriarTrocaTurnosAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            mContext = itemView.getContext();

            textViewPrioridade = itemView.findViewById(R.id.textViewPrioridade);
            textViewProcuro = itemView.findViewById(R.id.textViewProcuro);
            imageButtonRemove = itemView.findViewById(R.id.imageButtonRemove);
            imageButtonMoveUp = itemView.findViewById(R.id.imageButtonMoveUp);
            imageButtonMoveDown = itemView.findViewById(R.id.imageButtonMoveDown);
        }

        public void setup(final String turno, final int currentOpcao, final int totalOpcoes) {
            textViewPrioridade.setText(String.valueOf(currentOpcao));
            textViewProcuro.setText(turno);

            imageButtonRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.removeElement(turno);
                    Toast toast = Toast.makeText(mContext, "Turno " + turno + " removido.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

            if (currentOpcao == 1) {
                imageButtonMoveUp.setVisibility(View.INVISIBLE);
                if (totalOpcoes == 1)
                    imageButtonMoveDown.setVisibility(View.INVISIBLE);
                else
                    imageButtonMoveDown.setVisibility(View.VISIBLE);
            }
            else if (currentOpcao == totalOpcoes)
                imageButtonMoveDown.setVisibility(View.INVISIBLE);
            else {
                imageButtonMoveUp.setVisibility(View.VISIBLE);
                imageButtonMoveDown.setVisibility(View.VISIBLE);
            }

            imageButtonMoveUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.moveUp(currentOpcao - 1);
                    Toast toast = Toast.makeText(mContext, "Prioridade do turno " + turno + " aumentada.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

            imageButtonMoveDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.moveDown(currentOpcao - 1);
                    Toast toast = Toast.makeText(mContext, "Prioridade do turno " + turno + " diminuida.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
    }
}
