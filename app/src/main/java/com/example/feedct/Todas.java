package com.example.feedct;

import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedct.adapters.TodasAdapter;
import com.example.feedct.cadeiracomparators.CadeiraNameComparator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class Todas extends Fragment {
    SortedSet<Departamento> departamentos;

    TodasAdapter adapter;

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.activity_todas, container, false);

        InputStream inputStream = getResources().openRawResource(R.raw.cadeiras);
        String cadeiras_json = null;

        try {
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            cadeiras_json = new String(b);
        }
        catch (Exception e) { }

        Type collectionType = new TypeToken<List<Cadeira>>(){}.getType();
        List<Cadeira> cadeiras_list = new Gson().fromJson(cadeiras_json, collectionType);

        HashMap<String, Departamento> departamentoByName = new HashMap<>();
        departamentos = new TreeSet<>();

        for (Cadeira cadeira : cadeiras_list) {
            if(!cadeira.isInscrito()) {
                String nomeDepartamento = cadeira.getDepartamentoText();

                Departamento departamento = departamentoByName.get(nomeDepartamento);
                if (departamento == null) {
                    departamento = new Departamento(nomeDepartamento);
                    departamentos.add(departamento);

                    departamentoByName.put(nomeDepartamento, departamento);
                }

                departamento.addCadeira(cadeira);
            }
        }

        // PAra cada derpatamente ordenar por nome
        Comparator<Cadeira> comparator = new CadeiraNameComparator();
        for (Departamento departamento : departamentos) {
            departamento.sortCadeiras(comparator);
        }

        RecyclerView recyclerView = view.findViewById(R.id.todasRecyclerView);
        //recyclerView.setHasFixedSize(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new TodasAdapter();
        adapter.setData(departamentos);
        recyclerView.setAdapter(adapter);

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
                SortedSet<Departamento> filtered_departamento = new TreeSet<>();

                for(Departamento departamento : departamentos) {
                    if (departamento.getName().toLowerCase().startsWith(newText.toLowerCase()))
                        filtered_departamento.add(departamento);
                    else {
                        Departamento tmp = new Departamento(departamento.getName());
                        for (int semestre = 1; semestre <= 2; semestre++) {
                            for (Cadeira cadeira : departamento.getCadeirasBySem(semestre)) {
                                if (cadeira.getSiglaText().toLowerCase().startsWith(newText.toLowerCase()) || cadeira.getNomeText().toLowerCase().startsWith(newText.toLowerCase()))
                                    tmp.addCadeira(cadeira);
                            }
                        }

                        if (tmp.getCadeirasBySemSize(1) + tmp.getCadeirasBySemSize(2) != 0)
                            filtered_departamento.add(tmp);
                    }
                }

                adapter.setData(filtered_departamento);

                return false;
            }
        });
    }
}
