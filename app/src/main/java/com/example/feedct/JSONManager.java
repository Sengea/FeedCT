package com.example.feedct;

import android.content.res.Resources;

import com.example.feedct.adapters.DateAdapter;
import com.example.feedct.adapters.FeedbackAdapter;
import com.example.feedct.jsonpojos.AtendimentoDocente;
import com.example.feedct.jsonpojos.Cadeira;
import com.example.feedct.jsonpojos.CadeiraUser;
import com.example.feedct.jsonpojos.Curso;
import com.example.feedct.jsonpojos.Feedback;
import com.example.feedct.jsonpojos.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JSONManager {
    private Resources resources;

    public static List<Cadeira> cadeiras = null;
    public static List<AtendimentoDocente> atendimentoDocentes = null;
    public static List<CadeiraUser> cadeiraUsers = null;
    public static List<User> users = null;
    public static List<Feedback> feedbacks = null;
    public static List<Curso> cursos = null;

    public static Map<String, User> userByEmail = null;
    public static Map<String, Cadeira> cadeiraByName = null;
    public static Map<String, List<AtendimentoDocente>> atendimentoDocentesByCadeira = null;
    public static Map<String, List<Feedback>> feedbackByCadeira = null;
    public static Map<String, Map<String, List<Feedback>>> feedbackByCadeiraAndCurso = null;

    public JSONManager(Resources resources) {
        this.resources = resources;
        readCadeiras();
        readAtendimentoDocente();
        readCadeiraUser();
        readUsers();
        readFeedback();
        readCursos();
    }

    private void readCadeiras() {
        String json = getJsonString(R.raw.cadeiras);

        cadeiras = new Gson().fromJson(json, new TypeToken<List<Cadeira>>(){}.getType());

        cadeiraByName = new HashMap<>();
        for (Cadeira cadeira : cadeiras) {
            cadeiraByName.put(cadeira.getNome(), cadeira);
        }
    }

    private void readAtendimentoDocente() {
        String json = getJsonString(R.raw.atendimentodocente);

        atendimentoDocentes = new Gson().fromJson(json, new TypeToken<List<AtendimentoDocente>>(){}.getType());

        atendimentoDocentesByCadeira = new HashMap<>();
        for (AtendimentoDocente aD : atendimentoDocentes) {
            String cadeiraName = aD.getCadeira();
            List<AtendimentoDocente> aux = atendimentoDocentesByCadeira.get(cadeiraName);
            if (aux == null) {
                aux = new LinkedList<>();
                atendimentoDocentesByCadeira.put(cadeiraName, aux);
            }

            aux.add(aD);
        }
    }

    private void readCadeiraUser() {
        String json = getJsonString(R.raw.cadeirauser);

        if (json != null)
            cadeiraUsers = new Gson().fromJson(json, new TypeToken<List<CadeiraUser>>(){}.getType());
    }

    private void readUsers() {
        String json = getJsonString(R.raw.users);

        users = new Gson().fromJson(json, new TypeToken<List<User>>(){}.getType());

        userByEmail = new HashMap<>();
        for (User user : users) {
            userByEmail.put(user.getEmail(), user);
        }
    }

    private void readFeedback() {
        String json = getJsonString(R.raw.feedback);

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new DateAdapter());
        Gson gson = builder.create();

        feedbacks = gson.fromJson(json, new TypeToken<List<Feedback>>(){}.getType());

        feedbackByCadeira = new HashMap<>();
        feedbackByCadeiraAndCurso = new HashMap<>();

        for (Feedback feedback : feedbacks) {
            String cadeiraName = feedback.getCadeiraName();

            List<Feedback> feedbackByCadeiraAux = feedbackByCadeira.get(cadeiraName);
            if (feedbackByCadeiraAux == null) {
                feedbackByCadeiraAux = new LinkedList<>();
                feedbackByCadeira.put(cadeiraName, feedbackByCadeiraAux);
            }
            feedbackByCadeiraAux.add(feedback);

            Map<String, List<Feedback>> feedbackByCadeiraAndCursoAux1 = feedbackByCadeiraAndCurso.get(cadeiraName);
            if (feedbackByCadeiraAndCursoAux1 == null) {
                feedbackByCadeiraAndCursoAux1 = new HashMap<>();
                feedbackByCadeiraAndCurso.put(cadeiraName, feedbackByCadeiraAndCursoAux1);
            }

            User user = userByEmail.get(feedback.getUserEmail());
            List<Feedback> feedbackByCadeiraAndCursoAux2 = feedbackByCadeiraAndCursoAux1.get(user.getCurso());
            if (feedbackByCadeiraAndCursoAux2 == null) {
                feedbackByCadeiraAndCursoAux2 = new LinkedList<>();
                feedbackByCadeiraAndCursoAux1.put(user.getCurso(), feedbackByCadeiraAndCursoAux2);
            }
            feedbackByCadeiraAndCursoAux2.add(feedback);



        }
    }

    private void readCursos() {
        String json = getJsonString(R.raw.cursos);

        if(json != null)
            cursos = new Gson().fromJson(json, new TypeToken<List<Curso>>(){}.getType());
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
