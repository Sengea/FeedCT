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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Minhas extends Fragment {

    private List<Cadeira> cadeiras;
    private MinhasAdapter adapter;

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.activity_minhas, container, false);

        InputStream inputStream = getResources().openRawResource(R.raw.cadeiras);
        String cadeiras_json = null;

        try {
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            cadeiras_json = new String(b);
        }
        catch (Exception e) { }

        Type collectionType = new TypeToken<List<Cadeira>>(){}.getType();
        List<Cadeira> allCadeiras = new Gson().fromJson(cadeiras_json, collectionType);

        cadeiras = new LinkedList<>();
        for (Cadeira cadeira : allCadeiras) {
            if (cadeira.isInscrito())
                cadeiras.add(cadeira);
        }

        Collections.sort(cadeiras);

        ListView listView = view.findViewById(R.id.minhasListView);

        adapter = new MinhasAdapter();
        adapter.setData(cadeiras);
        listView.setAdapter(adapter);

        return  view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.todas_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Cadeira> filtered_cadeiras = new LinkedList<>();

                for(Cadeira cadeira : cadeiras) {
                    if (cadeira.getSiglaText().toLowerCase().startsWith(newText.toLowerCase()) || cadeira.getNomeText().toLowerCase().startsWith(newText.toLowerCase()))
                        filtered_cadeiras.add(cadeira);
                }

                adapter.setData(filtered_cadeiras);

                return false;
            }
        });
    }
}
