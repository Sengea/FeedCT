package com.example.feedct.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.feedct.activities.CadeiraActivity;
import com.example.feedct.pojos.Cadeira;
import com.example.feedct.R;

import java.util.LinkedList;
import java.util.List;

public class MinhasAdapter extends BaseAdapter {
    private List<Cadeira> current_cadeiras;
    private Context mContext;

    public MinhasAdapter(Context context) {
        mContext = context;
        current_cadeiras = new LinkedList<>();
    }

    public void setData(List<Cadeira> data) {
        if (data == null)
            return;

        current_cadeiras.clear();
        current_cadeiras.addAll(data);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return current_cadeiras.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cadeira_layout, null);

        TextView siglaTextView = convertView.findViewById(R.id.sigla);
        TextView nomeTextView = convertView.findViewById(R.id.nome);
        TextView departamentoTextView = convertView.findViewById(R.id.departamento);
        TextView semestreTextView = convertView.findViewById(R.id.semestre);
        TextView creditosTextView = convertView.findViewById(R.id.creditos);
        RatingBar ratingBar =  convertView.findViewById(R.id.rating);

        siglaTextView.setText(current_cadeiras.get(position).getSigla());
        nomeTextView.setText(current_cadeiras.get(position).getNome());
        departamentoTextView.setText(current_cadeiras.get(position).getDepartamento());
        semestreTextView.setText(current_cadeiras.get(position).getSemestre() + "º SEM");
        creditosTextView.setText(current_cadeiras.get(position).getCreditos() + " ECTS");
        ratingBar.setRating(current_cadeiras.get(position).getRating());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CadeiraActivity.class);
                intent.putExtra("Cadeira", ((TextView)v.findViewById(R.id.nome)).getText());
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }
}
