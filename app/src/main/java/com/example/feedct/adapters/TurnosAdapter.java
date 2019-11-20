package com.example.feedct.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.example.feedct.pojos.PedidoTurnos;
import com.example.feedct.pojos.TrocaTurnos;
import com.example.feedct.pojos.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TurnosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<TrocaTurnos> current_trocasTurnos;
    private Map<TrocaTurnos, User> userByTroca;
    private Map<TrocaTurnos, String> idByTroca;
    private CadeiraUser cadeiraUser;

    public TurnosAdapter() {
        current_trocasTurnos = new ArrayList<>();
    }

    public void setData(List<TrocaTurnos> trocasTurnos, Map<TrocaTurnos, User> userByTroca, Map<TrocaTurnos, String> idByTroca, CadeiraUser cadeiraUser) {
        if (trocasTurnos == null)
            return;

        current_trocasTurnos.clear();
        current_trocasTurnos.addAll(trocasTurnos);

        this.userByTroca = userByTroca;
        this.idByTroca = idByTroca;
        this.cadeiraUser = cadeiraUser;

        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_pedido_turno, parent, false);
        return new TurnosAdapter.MyItem(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TrocaTurnos trocaTurnos = current_trocasTurnos.get(position);
        ((TurnosAdapter.MyItem) holder).setup(trocaTurnos, userByTroca.get(trocaTurnos), idByTroca.get(trocaTurnos), cadeiraUser, this);
    }

    @Override
    public int getItemCount() {
        return current_trocasTurnos.size();
    }

    public void onSendRequest(TrocaTurnos trocaTurnos) {
        current_trocasTurnos.remove(trocaTurnos);
        DataManager.db.collection(DataManager.PEDIDOS_TURNOS).add(new PedidoTurnos(cadeiraUser.getNomeCadeira(), Session.userEmail, idByTroca.get(trocaTurnos)));
        this.notifyDataSetChanged();
    }

    public static class MyItem extends RecyclerView.ViewHolder {
        private Context mContext;

        private ConstraintLayout constraintLayout;
        private TextView textViewNome, textViewTem, textViewQuer;
        private ImageButton imageButtonSend;


        public MyItem(@NonNull View itemView) {
            super(itemView);
            mContext = itemView.getContext();

            constraintLayout = itemView.findViewById(R.id.constraintLayout);

            textViewNome = itemView.findViewById(R.id.textViewNome);
            textViewTem = itemView.findViewById(R.id.textViewTem);
            textViewQuer = itemView.findViewById(R.id.textViewQuer);

            imageButtonSend = itemView.findViewById(R.id.imageButtonSend);
        }

        public void setup(final TrocaTurnos trocaTurnos, final User user, final String trocaTurnosId, final CadeiraUser cadeiraUser, final TurnosAdapter turnosAdapter) {
            textViewNome.setText(user.getNome());

            textViewTem.setText(String.valueOf(trocaTurnos.getTenho()));

            StringBuilder queroStringBuilder = new StringBuilder();
            for (String quero : trocaTurnos.getProcuro()) {
                queroStringBuilder.append(quero + ", ");
            }
            String queroString = queroStringBuilder.toString();
            queroString = queroString.substring(0, queroString.length() - 2);
            textViewQuer.setText(queroString);

            if (trocaTurnos.getUserEmail().equals(Session.userEmail)) {
                constraintLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorMyGroup));
                imageButtonSend.setVisibility(View.GONE);
            }
            else if (trocaTurnos.getProcuro().contains(cadeiraUser.getTurno())) {
                constraintLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorOtherAvailableGroup));
                imageButtonSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Enviar Pedido?");
                        builder.setMessage("Quer enviar um pedido de troca de turnos a " + user.getNome() + "? Abdicaria do turno " + cadeiraUser.getTurno() + " pelo turno " + trocaTurnos.getTenho()+ ".");

                        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                turnosAdapter.onSendRequest(trocaTurnos);
                                Toast toast = Toast.makeText(mContext,"Pedido enviado com sucesso.", Toast.LENGTH_SHORT);
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
                imageButtonSend.setVisibility(View.VISIBLE);
            }
            else {
                constraintLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorOtherDisabledGroup));
                imageButtonSend.setVisibility(View.GONE);
            }

        }
    }
}
