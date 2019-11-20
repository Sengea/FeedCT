package com.example.feedct.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedct.DataManager;
import com.example.feedct.R;
import com.example.feedct.Session;
import com.example.feedct.activities.DetalhesGrupoActivity;
import com.example.feedct.activities.EditarGrupoActivity;
import com.example.feedct.pojos.Cadeira;
import com.example.feedct.pojos.CadeiraUser;
import com.example.feedct.pojos.Grupo;
import com.example.feedct.pojos.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GruposAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private Context mContext;

    private List<Grupo> current_grupos;
    private Map<Grupo, String> current_idByGrupo;
    private Cadeira cadeira;
    private CadeiraUser cadeiraUser;
    private Map<Grupo, List<String>> current_userNamesByGrupo;
    private Grupo current_userGrupo;

    public GruposAdapter(Cadeira cadeira) {
        current_grupos = new ArrayList<>();
        this.cadeira = cadeira;
    }

    public void setData(final List<Grupo> data, final Map<Grupo, String> idByGrupo, final Map<Grupo, List<String>> userNamesByGrupo, CadeiraUser cadeiraUser, Grupo userGrupo) {
        if (data == null)
            return;

        this.cadeiraUser = cadeiraUser;
        current_grupos.clear();
        current_grupos.addAll(data);
        current_idByGrupo = idByGrupo;
        current_userNamesByGrupo = userNamesByGrupo;
        current_userGrupo = userGrupo;
        GruposAdapter.this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grupo, parent, false);
        return new GruposAdapter.MyItem(v, cadeiraUser);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Grupo grupo = current_grupos.get(position);
        ((GruposAdapter.MyItem) holder).setup(grupo, current_idByGrupo.get(grupo), current_userNamesByGrupo.get(grupo), current_userGrupo);
    }

    @Override
    public int getItemCount() {
        return current_grupos.size();
    }

    public static class MyItem extends RecyclerView.ViewHolder {
        private Context mContext;
        private CadeiraUser cadeiraUser;
        private boolean joinable;

        private CardView cardView;
        private LinearLayout linearLayout;
        private TextView textViewMaxElementos;
        private ConstraintLayout constraintLayout;

        public MyItem(@NonNull View itemView, CadeiraUser cadeiraUser) {
            super(itemView);
            mContext = itemView.getContext();
            this.cadeiraUser = cadeiraUser;

            cardView = itemView.findViewById(R.id.cardView);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            textViewMaxElementos = itemView.findViewById(R.id.textView2);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }

        public void setup(final Grupo grupo, final String grupoId, final List<String> userNames, final Grupo userGrupo) {
            linearLayout.removeAllViews();

            Collections.sort(userNames);
            for (String userName : userNames) {
                View viewElemento = LayoutInflater.from(itemView.getContext()).inflate(R.layout.layout_elemento_grupo, null);
                ((TextView) viewElemento.findViewById(R.id.textViewNomeElemento)).setText(userName);
                linearLayout.addView(viewElemento);
            }

            joinable = false;
            if (grupo.getElementos().contains(Session.userEmail)) {
                constraintLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorMyGroup));
            }
            else if(grupo.getMode() == Grupo.MODE_NO_REQUESTS || (!grupo.getTurnos().equals("Todos") && !grupo.getTurnos().equals(cadeiraUser.getTurno())) || (userGrupo != null && grupo.getElementos().size() + userGrupo.getElementos().size() > Math.max(grupo.getMaxElementos(), userGrupo.getMaxElementos()))){
                constraintLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorOtherDisabledGroup));
            }
            else {
                constraintLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorOtherAvailableGroup));
                joinable = true;
            }

            textViewMaxElementos.setText(grupo.getElementos().size() + "/" + grupo.getMaxElementos());

            itemView.findViewById(R.id.cardView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    if (grupo.getElementos().contains(Session.userEmail))
                        intent = new Intent(v.getContext(), EditarGrupoActivity.class);
                    else
                        intent = new Intent(v.getContext(), DetalhesGrupoActivity.class);
                    intent.putExtra("GrupoId", grupoId);
                    intent.putExtra("Cadeira", cadeiraUser.getNomeCadeira());
                    intent.putExtra("Joinable", joinable);
                    v.getContext().startActivity(intent);
                }
            });

        }
    }
}
