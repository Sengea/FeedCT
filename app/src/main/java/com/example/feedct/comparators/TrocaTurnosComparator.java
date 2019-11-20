package com.example.feedct.comparators;

import com.example.feedct.Session;
import com.example.feedct.pojos.TrocaTurnos;

import java.util.Comparator;
import java.util.Map;

public class TrocaTurnosComparator implements Comparator<TrocaTurnos> {
    private String userTurno;
    private Map<Integer, Integer> priorityByTurno;

    public TrocaTurnosComparator(String userTurno, Map<Integer, Integer> priorityByTurno) {
        this.userTurno = userTurno;
        this.priorityByTurno = priorityByTurno;
    }

    @Override
    public int compare(TrocaTurnos o1, TrocaTurnos o2) {
        int result;

        if(o1.getUserEmail().equals(Session.userEmail))
            result = -1;
        else if (o2.getUserEmail().equals(Session.userEmail))
            result = 1;
        else {
            if (priorityByTurno == null) {
                //No filters
                if (o1.getProcuro().contains(userTurno)) {
                    if (o2.getProcuro().contains(userTurno)) {
                        result = 0;
                        //o1 procura turno do utilizador. o2 procura turno do utilizador
                    } else {
                        result = -1;
                        //o1 procura turno do utilizador. o2 não procura turno do utilizador
                    }
                } else {
                    if (o2.getProcuro().contains(userTurno)) {
                        result = 1;
                        //o1 não procura turno do utilizador. o2 procura turno do utilizador
                    } else {
                        result = 0;
                        //o1 não procura turno do utilizador. o2 não procura turno do utilizador
                    }
                }
            } else {
                Integer priorityO1 = priorityByTurno.get(o1.getTenho());
                Integer priorityO2 = priorityByTurno.get(o2.getTenho());

                if (priorityO1 == null && priorityO2 == null) {
                    if (o1.getProcuro().contains(userTurno)) {
                        if (o2.getProcuro().contains(userTurno)) {
                            result = 0;
                            //o1 procura turno do utilizador. o2 procura turno do utilizador
                        } else {
                            result = -1;
                            //o1 procura turno do utilizador. o2 não procura turno do utilizador
                        }
                    } else {
                        if (o2.getProcuro().contains(userTurno)) {
                            result = 1;
                            //o1 não procura turno do utilizador. o2 procura turno do utilizador
                        } else {
                            result = 0;
                            //o1 não procura turno do utilizador. o2 não procura turno do utilizador
                        }
                    }
                } else if (priorityO1 == null) {
                    if (o1.getProcuro().contains(userTurno)) {
                        if (o2.getProcuro().contains(userTurno)) {
                            result = 1;
                            //o1 procura turno do utilizador. o2 procura turno do utilizador
                        } else {
                            result = -1;
                            //o1 procura turno do utilizador. o2 não procura turno do utilizador
                        }
                    } else {
                        result = 1;
                    }
                } else if (priorityO2 == null) {
                    if (o1.getProcuro().contains(userTurno)) {
                        result = -1;
                    } else {
                        if (o2.getProcuro().contains(userTurno)) {
                            result = 1;
                            //o1 não procura turno do utilizador. o2 procura turno do utilizador
                        } else {
                            result = -1;
                            //o1 não procura turno do utilizador. o2 não procura turno do utilizador
                        }
                    }
                } else {
                    if (priorityO1 == priorityO2) {
                        if (o1.getProcuro().contains(userTurno)) {
                            if (o2.getProcuro().contains(userTurno)) {
                                result = 0;
                                //o1 procura turno do utilizador. o2 procura turno do utilizador
                            } else {
                                result = -1;
                                //o1 procura turno do utilizador. o2 não procura turno do utilizador
                            }
                        } else {
                            if (o2.getProcuro().contains(userTurno)) {
                                result = 1;
                                //o1 não procura turno do utilizador. o2 procura turno do utilizador
                            } else {
                                result = 0;
                                //o1 não procura turno do utilizador. o2 não procura turno do utilizador
                            }
                        }
                    } else if (priorityO1 < priorityO2) {
                        if (o1.getProcuro().contains(userTurno)) {
                            result = -1;
                        } else {
                            if (o2.getProcuro().contains(userTurno)) {
                                result = 1;
                                //o1 não procura turno do utilizador. o2 procura turno do utilizador
                            } else {
                                result = -1;
                                //o1 não procura turno do utilizador. o2 não procura turno do utilizador
                            }
                        }
                    } else {
                        if (o1.getProcuro().contains(userTurno)) {
                            if (o2.getProcuro().contains(userTurno)) {
                                result = 1;
                                //o1 procura turno do utilizador. o2 procura turno do utilizador
                            } else {
                                result = -1;
                                //o1 procura turno do utilizador. o2 não procura turno do utilizador
                            }
                        } else {
                            result = 1;
                        }
                    }
                }
            }
        }

        return result;
    }
}
