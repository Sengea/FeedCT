package com.example.feedct;

import android.content.res.Resources;

import com.example.feedct.pojos.Curso;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class DataManager {
    public static final String ATENDIMENTO_DOCENTE = "atendimentoDocentes";
    public static final String CADEIRA_USER = "cadeiraUser";
    public static final String CADEIRAS = "cadeiras";
    public static final String CURSOS = "cursos";
    public static final String FEEDBACK = "feedback";
    public static final String GRUPOS = "grupos";
    public static final String PEDIDOS_GRUPO = "pedidosGrupo";
    public static final String USERS = "users";
    public static final String PEDIDOS_TURNOS = "pedidosTurnos";
    public static final String TROCA_TURNOS = "trocaTurnos";
    public static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Resources resources;

    public static List<Curso> cursos;

    public DataManager(Resources resources) {
        this.resources = resources;
        readCursos();
    }

    private void readCursos() {
        db.collection(CURSOS).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                cursos = new LinkedList<>();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments())
                    cursos.add(documentSnapshot.toObject(Curso.class));
            }
        });
    }

    private String getJsonString(int resourceId) {
        String json = null;

        try {
            InputStream inputStream = resources.openRawResource(resourceId);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            json = new String(b);
        }
        catch (Exception e) { }

        return json;
    }
}
