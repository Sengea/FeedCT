package com.example.feedct.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.feedct.Cadeira;
import com.example.feedct.R;

import java.util.LinkedList;
import java.util.List;

public class MinhasAdapter extends BaseAdapter {
    private List<Cadeira> current_cadeiras;

    public MinhasAdapter() {
        current_cadeiras = new LinkedList<>();
    }

    public void setData(List<Cadeira> data) {
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

        siglaTextView.setText(current_cadeiras.get(position).getSiglaText());
        nomeTextView.setText(current_cadeiras.get(position).getNomeText());
        departamentoTextView.setText(current_cadeiras.get(position).getDepartamentoText());
        semestreTextView.setText(current_cadeiras.get(position).getSemestreText());
        creditosTextView.setText(current_cadeiras.get(position).getCreditosText());
        ratingBar.setRating(current_cadeiras.get(position).getRating());

        return convertView;
    }
}
