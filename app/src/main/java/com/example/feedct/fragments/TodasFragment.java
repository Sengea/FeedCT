package com.example.feedct.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedct.Departamento;
import com.example.feedct.JSONManager;
import com.example.feedct.R;
import com.example.feedct.Session;
import com.example.feedct.adapters.TodasAdapter;
import com.example.feedct.cadeiracomparators.CadeiraNameComparator;
import com.example.feedct.jsonpojos.Cadeira;
import com.example.feedct.jsonpojos.CadeiraUser;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class TodasFragment extends Fragment {
    TodasAdapter adapter;
    SortedSet<Departamento> departamentos;

    private String currentSearch = "";

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_todas, container, false);

        //Setup recycler view
        RecyclerView recyclerView = view.findViewById(R.id.todasRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new TodasAdapter(view.getContext());
        recyclerView.setAdapter(adapter);

        updateDepartamentos();
        adapter.setData(departamentos);

        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();

        updateDepartamentos();
        adapter.setData(filteredDepartamentos(currentSearch));
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
                currentSearch = newText;
                adapter.setData(filteredDepartamentos(currentSearch));
                return false;
            }
        });
    }

    private void updateDepartamentos() {
        List<String> minhasNames = new LinkedList<>();
        for (CadeiraUser cadeiraUser : JSONManager.cadeiraUsers) {
            if (cadeiraUser.getEmailUser().equals(Session.userEmail))
                minhasNames.add(cadeiraUser.getNomeCadeira());
        }

        HashMap<String, Departamento> departamentoByName = new HashMap<>();
        departamentos = new TreeSet<>();

        for (Cadeira cadeira : JSONManager.cadeiras) {
            if(!minhasNames.contains(cadeira.getNome())) {
                String nomeDepartamento = cadeira.getDepartamento();

                Departamento departamento = departamentoByName.get(nomeDepartamento);
                if (departamento == null) {
                    departamento = new Departamento(nomeDepartamento);
                    departamentos.add(departamento);

                    departamentoByName.put(nomeDepartamento, departamento);
                }

                departamento.addCadeira(cadeira);
            }
        }

        // Para cada derpatamente ordenar por nome
        Comparator<Cadeira> comparator = new CadeiraNameComparator();
        for (Departamento departamento : departamentos) {
            departamento.sortCadeiras(comparator);
        }
    }

    private SortedSet<Departamento> filteredDepartamentos(String search) {
        SortedSet<Departamento> filtered_departamentos = new TreeSet<>();

        for(Departamento departamento : departamentos) {
            if (departamento.getName().toLowerCase().startsWith(search.toLowerCase()))
                filtered_departamentos.add(departamento);
            else {
                Departamento tmp = new Departamento(departamento.getName());
                for (int semestre = 1; semestre <= 2; semestre++) {
                    for (Cadeira cadeira : departamento.getCadeirasBySem(semestre)) {
                        if (cadeira.getSigla().toLowerCase().startsWith(search.toLowerCase()) || cadeira.getNome().toLowerCase().startsWith(search.toLowerCase()))
                            tmp.addCadeira(cadeira);
                    }
                }

                if (tmp.getCadeirasBySemSize(1) + tmp.getCadeirasBySemSize(2) != 0)
                    filtered_departamentos.add(tmp);
            }
        }

        return filtered_departamentos;
    }
}
