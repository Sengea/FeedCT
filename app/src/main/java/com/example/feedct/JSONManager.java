package com.example.feedct;

import android.content.res.Resources;

import com.example.feedct.jsonpojos.AtendimentoDocente;
import com.example.feedct.jsonpojos.Cadeira;
import com.example.feedct.jsonpojos.CadeiraUser;
import com.example.feedct.jsonpojos.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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

    public static Map<String, Cadeira> cadeiraByName = null;
    public static Map<String, List<AtendimentoDocente>> atendimentoDocentesByCadeira = null;

    public JSONManager(Resources resources) {
        this.resources = resources;
        readCadeiras();
        readAtendimentoDocente();
        readCadeiraUser();
        readUsers();
    }

    public static void desinscrever(CadeiraUser cadeiraUser) {
        cadeiraUsers.remove(cadeiraUser);

        Gson gson = new Gson();
        String jsonString = gson.toJson(cadeiraUsers);

        FileOutputStream outputStream;
    }

    private void readCadeiras() {
        String json = null;

        try {
            InputStream inputStream = resources.openRawResource(R.raw.cadeiras);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            json = new String(b);

        }
        catch (Exception e) { }

        cadeiras = new Gson().fromJson(json, new TypeToken<List<Cadeira>>(){}.getType());

        cadeiraByName = new HashMap<>();
        for (Cadeira cadeira : cadeiras) {
            cadeiraByName.put(cadeira.getNome(), cadeira);
        }
    }

    private void readAtendimentoDocente() {
        String json = null;

        try {
            InputStream inputStream = resources.openRawResource(R.raw.atendimentodocente);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            json = new String(b);

        }
        catch (Exception e) { }

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
        String json = null;

        try {
            InputStream inputStream = resources.openRawResource(R.raw.cadeirauser);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            json = new String(b);

        }
        catch (Exception e) { }

        cadeiraUsers = new Gson().fromJson(json, new TypeToken<List<CadeiraUser>>(){}.getType());
    }

    private void readUsers() {
        String json = null;

        try {
            InputStream inputStream = resources.openRawResource(R.raw.users);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            json = new String(b);
        }
        catch (Exception e) { }

        users = new Gson().fromJson(json, new TypeToken<List<User>>(){}.getType());
    }
}
