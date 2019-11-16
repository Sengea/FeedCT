package com.example.feedct.pojos;

public class AtendimentoDocente implements Comparable<AtendimentoDocente> {
    private String cadeira;
    private String professor;
    private String dia;
    private String horaInicio;
    private String horaFim;
    private String sala;

    public AtendimentoDocente() {

    }

    public AtendimentoDocente(String cadeira, String professor, String dia, String horaInicio, String horaFim, String sala) {
        this.cadeira = cadeira;
        this.professor = professor;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.sala = sala;
    }

    public String getCadeira() {
        return cadeira;
    }

    public String getProfessor() {
        return professor;
    }

    public String getDia() {
        return dia;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getHoraFim() {
        return horaFim;
    }

    public String getSala() {
        return sala;
    }

    @Override
    public int compareTo(AtendimentoDocente o) {
        return this.getProfessor().compareTo(o.getProfessor());
    }
}
