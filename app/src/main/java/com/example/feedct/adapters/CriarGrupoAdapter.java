package com.example.feedct.adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedct.DataManager;
import com.example.feedct.R;
import com.example.feedct.SearchableUser;
import com.example.feedct.Session;
import com.example.feedct.pojos.Grupo;
import com.example.feedct.pojos.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CriarGrupoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private List<String> convites;
    private List<User> current_elementos;
    private ArrayList<SearchableUser> userNames;

    public CriarGrupoAdapter(List<String> convites, ArrayList<SearchableUser> userNames) {
        current_elementos = new ArrayList<>();
        this.convites = convites;
        this.userNames = userNames;
    }

    public ArrayList<SearchableUser> getUserNames() {
        return userNames;
    }

    public void setData(List<User> elementos, ArrayList<SearchableUser> userNames) {
        if (elementos == null || userNames == null)
            return;

        current_elementos.clear();
        current_elementos.addAll(elementos);
        this.userNames.addAll(userNames);
        this.notifyDataSetChanged();
    }

    public void addElement(User user, SearchableUser item) {
        if (user == null || item == null)
            return;

        userNames.remove(item);
        convites.add(item.getUser().getEmail());
        current_elementos.add(user);
        this.notifyDataSetChanged();
    }

    public void removeElement(User element) {
        if (element == null)
            return;

        userNames.add(new SearchableUser(element.getNome() + " - " + element.getNumero(), element));
        Collections.sort(userNames);
        convites.remove(element.getEmail());
        current_elementos.remove(element);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_criar_grupo_elemento, parent, false);
        return new CriarGrupoAdapter.MyItem(v, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((CriarGrupoAdapter.MyItem) holder).setup(current_elementos.get(position));
    }

    @Override
    public int getItemCount() {
        return current_elementos.size();
    }

    public static class MyItem extends RecyclerView.ViewHolder {
        private CriarGrupoAdapter adapter;

        private TextView textViewNomeElemento;
        private ImageButton imageButtonRemove;

        public MyItem(@NonNull View itemView, CriarGrupoAdapter adapter) {
            super(itemView);
            this.adapter = adapter;

            textViewNomeElemento = itemView.findViewById(R.id.textViewNomeElemento);
            imageButtonRemove = itemView.findViewById(R.id.imageButtonRemove);
        }

        public void setup(final User user) {
            textViewNomeElemento.setText(user.getNome());
            imageButtonRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.removeElement(user);
                }
            });
        }
    }
}
