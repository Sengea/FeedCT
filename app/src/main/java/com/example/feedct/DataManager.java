package com.example.feedct;

import android.content.res.Resources;

import com.example.feedct.adapters.DateAdapter;
import com.example.feedct.pojos.AtendimentoDocente;
import com.example.feedct.pojos.Cadeira;
import com.example.feedct.pojos.CadeiraUser;
import com.example.feedct.pojos.Curso;
import com.example.feedct.pojos.Feedback;
import com.example.feedct.pojos.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class DataManager {
    public static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Resources resources;

    public static List<Curso> cursos;

    public DataManager(Resources resources) {
        this.resources = resources;
        readCursos();
    }

    private void readCursos() {
        db.collection("cursos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
