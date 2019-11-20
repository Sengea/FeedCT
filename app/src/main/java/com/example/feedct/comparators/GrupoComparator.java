package com.example.feedct.comparators;

import com.example.feedct.Session;
import com.example.feedct.pojos.Grupo;

import java.util.Comparator;

public class GrupoComparator implements Comparator<Grupo> {
    private String userTurno;
    private Grupo userGrupo;

    public GrupoComparator(String userTurno, Grupo userGrupo) {
        this.userTurno = userTurno;
        this.userGrupo = userGrupo;
    }

    @Override
    public int compare(Grupo o1, Grupo o2) {
        int result;

        if (o1.getElementos().contains(Session.userEmail))
            result = -1;
        else if (o2.getElementos().contains(Session.userEmail))
            result = 1;
        else {
            if(o1.getMode() == Grupo.MODE_NO_REQUESTS || (!o1.getTurnos().equals("Todos") && !o1.getTurnos().equals(userTurno))) {
                if(o2.getMode() == Grupo.MODE_NO_REQUESTS || (!o2.getTurnos().equals("Todos") && !o2.getTurnos().equals(userTurno))) {
                    //Not possible to join o1. Not possible to join o2
                    result = o2.getElementos().size() - o1.getElementos().size();
                }
                else {
                    //Not possible to join o1. Possible to join o2
                    result = 1;
                }
            }
            else {

                if(o2.getMode() == Grupo.MODE_NO_REQUESTS || (!o2.getTurnos().equals("Todos") && !o2.getTurnos().equals(userTurno))) {
                    //Possible to join o1. Not possible to join o2
                    result = -1;
                }
                else {
                    //Possible to join o1. Possible to join o2
                    if (o1.getElementos().size() == o1.getMaxElementos()) {
                        if (o2.getElementos().size() == o2.getMaxElementos()) {
                            result = o2.getElementos().size() - o1.getElementos().size();
                            //o1 is full. o2 is full
                        }
                        else {
                            //o1 is full. o2 is not full
                            result = 1;
                        }
                    }
                    else {
                        if (o2.getElementos().size() == o2.getMaxElementos()) {
                            result = -1;
                            //o1 is not full. o2 is full
                        }
                        else {
                            //o1 is not full. o2 is not full
                            if (userGrupo != null) {
                                if (userGrupo.getElementos().size() + o1.getElementos().size() > Math.max(userGrupo.getMaxElementos(), o1.getMaxElementos())) {
                                    if (userGrupo.getElementos().size() + o2.getElementos().size() > Math.max(userGrupo.getMaxElementos(), o2.getMaxElementos())) {
                                        result = o2.getElementos().size() - o1.getElementos().size();
                                        //Cannot merge with o1. Cannot merge with o2
                                    } else {
                                        result = 1;
                                        //Cannot merge with o1. Can merge with o2
                                    }
                                } else {
                                    if (userGrupo.getElementos().size() + o2.getElementos().size() > Math.max(userGrupo.getMaxElementos(), o2.getMaxElementos())) {
                                        result = -1;
                                        //Can merge with o1. Cannot merge with o2
                                    } else {
                                        result = o2.getElementos().size() - o1.getElementos().size();
                                        //Cannot merge with o1. Can merge with o2
                                    }
                                }
                            }
                            else
                                result = o2.getElementos().size() - o1.getElementos().size();
                        }
                    }
                }
            }
        }

        return result;
    }
}
