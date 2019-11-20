package com.example.feedct.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.feedct.DataManager;
import com.example.feedct.R;
import com.example.feedct.Session;
import com.example.feedct.adapters.MinhasAdapter;
import com.example.feedct.pojos.Cadeira;
import com.example.feedct.pojos.CadeiraUser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MinhasFragment extends Fragment {
    private MinhasAdapter adapter;
    private FrameLayout loadingScreen;

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_minhas, container, false);

        loadingScreen = view.findViewById(R.id.loadingScreen);

        ListView listView = view.findViewById(R.id.minhasListView);

        adapter = new MinhasAdapter(getContext());
        listView.setAdapter(adapter);

        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();

        updateCadeiras();
    }

    private void updateCadeiras() {
        loadingScreen.setVisibility(View.VISIBLE);
        DataManager.db.collection(DataManager.CADEIRA_USER).whereEqualTo("emailUser", Session.userEmail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> minhasNames = new ArrayList<>(queryDocumentSnapshots.getDocuments().size());
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    minhasNames.add(document.toObject(CadeiraUser.class).getNomeCadeira());
                }

                if (minhasNames.isEmpty()) {
                    adapter.setData(new LinkedList<Cadeira>());
                }
                else {
                    DataManager.db.collection(DataManager.CADEIRAS).whereIn("nome", minhasNames).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<Cadeira> cadeiras = new ArrayList<>(queryDocumentSnapshots.getDocuments().size());
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                cadeiras.add(document.toObject(Cadeira.class));
                            }
                            Collections.sort(cadeiras);
                            adapter.setData(cadeiras);
                            loadingScreen.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }
}
