package com.example.feedct.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedct.DataManager;
import com.example.feedct.Departamento;
import com.example.feedct.R;
import com.example.feedct.Session;
import com.example.feedct.adapters.TodasAdapter;
import com.example.feedct.cadeiracomparators.CadeiraNameComparator;
import com.example.feedct.pojos.Cadeira;
import com.example.feedct.pojos.CadeiraUser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class TodasFragment extends Fragment {
    private TodasAdapter adapter;
    private SortedSet<Departamento> departamentos;
    private SortedSet<Departamento> departamentosBearingFilter;
    private boolean initialized = false;

    private boolean sem1IsFiltered;
    private boolean sem2IsFiltered;
    private boolean[] departamentoIsFiltered;
    private String currentSearch;

    private Button buttonDepartamento;
    private ImageButton imageButtonCancelDepartamento;

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

        imageButtonCancelDepartamento = view.findViewById(R.id.imageButtonCancelDepartamento);
        final ImageButton imageButtonCancelSemestre = view.findViewById(R.id.imageButtonCancelSemestre);
        imageButtonCancelDepartamento.setVisibility(View.GONE);
        imageButtonCancelSemestre.setVisibility(View.GONE);

        buttonDepartamento = view.findViewById(R.id.buttonDepartamento);
        final Button buttonSemestre = view.findViewById(R.id.buttonCurso);

        buttonDepartamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Departamentos");

                final List<String> items = new LinkedList<>();
                for (Departamento departamento : departamentos)
                    items.add(departamento.getName());

                builder.setMultiChoiceItems(items.toArray(new String[0]), departamentoIsFiltered, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        departamentoIsFiltered[which] = isChecked;
                    }
                });

                builder.setPositiveButton("Filtrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateAdapterData();
                    }
                });

                builder.show();
            }
        });

        buttonSemestre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Semestre");

                builder.setItems(new String[]{"1º Semestre", "2º Semestre"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String filterText;

                        if (which == 0) {
                            sem1IsFiltered = true;
                            sem2IsFiltered = false;
                            filterText = "1º Semestre";
                        }
                        else {
                            sem1IsFiltered = false;
                            sem2IsFiltered = true;
                            filterText = "2º Semestre";
                        }

                        buttonSemestre.setText(filterText);
                        imageButtonCancelSemestre.setVisibility(View.VISIBLE);

                        updateAdapterData();
                    }
                });

                builder.show();
            }
        });

        imageButtonCancelDepartamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < departamentoIsFiltered.length; i++) {
                     departamentoIsFiltered[i] = false;
                }

                buttonDepartamento.setText(getString(R.string.departamentoFilter));
                imageButtonCancelDepartamento.setVisibility(View.GONE);

                updateAdapterData();
            }
        });

        imageButtonCancelSemestre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sem1IsFiltered = false;
                sem2IsFiltered = false;

                buttonSemestre.setText(getString(R.string.semestreFilter));
                imageButtonCancelSemestre.setVisibility(View.GONE);

                updateAdapterData();
            }
        });

        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();

        updateDepartamentos();
    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
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
                adapter.setData(searchInDepartamentos(currentSearch));
                return false;
            }
        });
    }

    private void updateAdapterData() {
        SortedSet<Departamento> filteredDepartamentos = applyDepartamentoFilter(departamentos);
        filteredDepartamentos = applySemestreFilter(filteredDepartamentos);
        departamentosBearingFilter = filteredDepartamentos;

        filteredDepartamentos = searchInDepartamentos(currentSearch);
        adapter.setData(filteredDepartamentos);
    }

    private SortedSet<Departamento> applyDepartamentoFilter(SortedSet<Departamento> departamentos) {
        StringBuilder filterText = new StringBuilder();
        SortedSet<Departamento> filteredDepartamentos = new TreeSet<>();
        int i = 0;
        for (Departamento departamento : departamentos) {
            if (departamentoIsFiltered[i++]) {
                filteredDepartamentos.add(departamento);
                filterText.append(" ").append(departamento.getName());
            }
        }

        if (filteredDepartamentos.isEmpty()) {
            filteredDepartamentos = departamentos;
            filterText = new StringBuilder(getString(R.string.departamentoFilter));
            imageButtonCancelDepartamento.setVisibility(View.GONE);
        }
        else {
            filterText = new StringBuilder(filterText.toString().trim().replace(" ", ", "));
            imageButtonCancelDepartamento.setVisibility(View.VISIBLE);
        }

        buttonDepartamento.setText(filterText.toString());

        return filteredDepartamentos;
    }

    private SortedSet<Departamento> applySemestreFilter(SortedSet<Departamento> departamentos) {
        SortedSet<Departamento> filteredDepartamentos = new TreeSet<>();

        for (Departamento departamento : departamentos) {
            if(sem1IsFiltered) {
                if (departamento.getCadeirasBySemSize(1) > 0) {
                    filteredDepartamentos.add(new Departamento(departamento.getName(), departamento.getCadeirasBySem(1), new LinkedList<Cadeira>()));
                }
            }
            else if (sem2IsFiltered) {
                if (departamento.getCadeirasBySemSize(2) > 0) {
                    filteredDepartamentos.add(new Departamento(departamento.getName(), new LinkedList<Cadeira>(), departamento.getCadeirasBySem(2)));
                }
            }
            else {
                filteredDepartamentos.add(departamento);
            }
        }

        return filteredDepartamentos;
    }

    private SortedSet<Departamento> searchInDepartamentos(String search) {
        SortedSet<Departamento> departamentosBearingSearch = new TreeSet<>();

        if (search == null || search.equals(""))
            return departamentosBearingFilter;

        for(Departamento departamento : departamentosBearingFilter) {
            // Pesquisa coincide com o nome de um departamento
            if (departamento.getName().toLowerCase().startsWith(search.toLowerCase()))
                departamentosBearingSearch.add(departamento);
            else {
                // Obtem as cadeiras deste departamento em que a pesquisa coincide ou com a sigla ou com o nome
                Departamento tmp = new Departamento(departamento.getName());
                for (int semestre = 1; semestre <= 2; semestre++) {
                    for (Cadeira cadeira : departamento.getCadeirasBySem(semestre)) {
                        if (cadeira.getSigla().toLowerCase().startsWith(search.toLowerCase()) || cadeira.getNome().toLowerCase().startsWith(search.toLowerCase()))
                            tmp.addCadeira(cadeira);
                    }
                }

                // Adiciona este departamento se alguma cadeira deste departamento coincidiu com a pesquisa
                if (tmp.getCadeirasBySemSize(1) + tmp.getCadeirasBySemSize(2) != 0)
                    departamentosBearingSearch.add(tmp);
            }
        }

        return departamentosBearingSearch;
    }

    private void updateDepartamentos() {
        DataManager.db.collection("cadeiraUser").whereEqualTo("emailUser", Session.userEmail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                final List<String> minhasNames = new LinkedList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    minhasNames.add(document.toObject(CadeiraUser.class).getNomeCadeira());
                }
                DataManager.db.collection("cadeiras").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        departamentos = new TreeSet<>();
                        HashMap<String, Departamento> departamentoByName = new HashMap<>();
                        List<Cadeira> cadeiras = new LinkedList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Cadeira cadeira = document.toObject(Cadeira.class);
                            if (!minhasNames.contains(cadeira.getNome())) {
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

                        Comparator<Cadeira> comparator = new CadeiraNameComparator();
                        for (Departamento departamento : departamentos) {
                            departamento.sortCadeiras(comparator);
                        }

                        if (!initialized) {
                            adapter.setData(departamentos);
                            departamentosBearingFilter = departamentos;
                            departamentoIsFiltered = new boolean[departamentos.size()];
                            sem1IsFiltered = false;
                            sem2IsFiltered = false;
                            initialized = true;
                        }
                        else {
                            updateAdapterData();
                        }
                    }
                });
            }
        });
    }
}
