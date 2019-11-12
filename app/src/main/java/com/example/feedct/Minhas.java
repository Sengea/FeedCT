package com.example.feedct;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.feedct.adapters.MinhasAdapter;
import com.example.feedct.jsonpojos.Cadeira;
import com.example.feedct.jsonpojos.CadeiraUser;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Minhas extends Fragment {
    private String userEmail = "af.moura@campus.fct.unl.pt";

    private List<Cadeira> cadeiras;
    private MinhasAdapter adapter;

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.activity_minhas, container, false);

        ListView listView = view.findViewById(R.id.minhasListView);

        adapter = new MinhasAdapter(getContext());
        listView.setAdapter(adapter);

        updateCadeiras();
        adapter.setData(cadeiras);


        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();

        updateCadeiras();
        adapter.setData(cadeiras);
    }

    private void updateCadeiras() {
        List<String> minhasNames = new LinkedList<>();
        for (CadeiraUser cadeiraUser : JSONManager.cadeiraUsers) {
            if (cadeiraUser.getEmailUser().equals(userEmail))
                minhasNames.add(cadeiraUser.getNomeCadeira());
        }

        cadeiras = new LinkedList<>();
        for (Cadeira cadeira : JSONManager.cadeiras) {
            if (minhasNames.contains(cadeira.getNome()))
                cadeiras.add(cadeira);
        }

        Collections.sort(cadeiras);
    }
}
