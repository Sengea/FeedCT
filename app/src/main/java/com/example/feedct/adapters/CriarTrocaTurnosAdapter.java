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

import com.example.feedct.DataManager;
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
        this.notifyItemInserted(procuro.size() - 1);
    }

    public void removeElement(String turno) {
        options.add(Integer.parseInt(turno));
        Collections.sort(options);
        int position = -1;
        for (int i = 0; i < procuro.size(); i++) {
            if (procuro.get(i).equals(turno)) {
                position = i;
                break;
            }
        }

        if (position != -1){
            procuro.remove(turno);

            this.notifyItemRemoved(position);
            for (int i = position; i < getItemCount(); i++ ){
                this.notifyItemChanged(i);
            }
        }
    }

    public void moveUp(int position) {
        Collections.swap(procuro, position, position - 1);
        this.notifyItemMoved(position, position - 1);
        this.notifyItemChanged(position);
        this.notifyItemChanged(position - 1);
    }

    public void moveDown(int position) {
        if (position != this.getItemCount())
        Collections.swap(procuro, position, position + 1);
        this.notifyItemMoved(position, position + 1);
        this.notifyItemChanged(position);
        this.notifyItemChanged(position + 1);
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

        private boolean moveUpEnabled;
        private boolean moveDownEnabled;

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

        public void setup(final String turno, int current, int total) {
            textViewPrioridade.setText(String.valueOf(getAdapterPosition() + 1));
            textViewProcuro.setText(turno);

            imageButtonRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.removeElement(turno);
                    /*Toast toast = Toast.makeText(mContext, "Turno " + turno + " removido.", Toast.LENGTH_SHORT);
                    toast.show();*/
                }
            });

            imageButtonMoveUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateArrowsEnabled();
                    if (moveUpEnabled)
                        adapter.moveUp(getAdapterPosition());
                    else
                        Toast.makeText(mContext,"Este turno já tem a prioridade máxima.", Toast.LENGTH_SHORT).show();
                }
            });

            imageButtonMoveDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateArrowsEnabled();
                    if (moveDownEnabled)
                        adapter.moveDown(getAdapterPosition());
                    else
                        Toast.makeText(mContext,"Este turno já tem a prioridade mínima.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void updateArrowsEnabled() {
            if (getAdapterPosition() == 0) {
                moveUpEnabled = false;
                if (adapter.getItemCount() == 1)
                    moveDownEnabled = false;
                else
                    moveDownEnabled = true;
            }
            else if (getAdapterPosition() == adapter.getItemCount() - 1) {
                moveUpEnabled = true;
                moveDownEnabled = false;
            }
            else {
                moveUpEnabled = true;
                moveDownEnabled = true;
            }
        }
    }
}
