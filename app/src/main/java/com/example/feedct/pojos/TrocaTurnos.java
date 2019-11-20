package com.example.feedct.pojos;

import java.util.List;

public class TrocaTurnos {
    private String cadeira;
    private String userEmail;
    private List<String> procuro;
    private String tenho;

    public TrocaTurnos() {

    }

    public TrocaTurnos(String cadeira, String userEmail, List<String> procuro, String tenho) {
        this.cadeira = cadeira;
        this.userEmail = userEmail;
        this.procuro = procuro;
        this.tenho = tenho;
    }

    public String getCadeira() {
        return cadeira;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public List<String> getProcuro() {
        return procuro;
    }

    public String getTenho() {
        return tenho;
    }
}
