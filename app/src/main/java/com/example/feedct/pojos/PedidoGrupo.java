package com.example.feedct.pojos;

public class PedidoGrupo {
    public static final int USER_TO_GROUP = 0;
    public static final int MERGE = 1;
    public static final int GROUP_TO_USER = 2;

    private String cadeira;
    private int type;
    private String sender;
    private String receiver;

    public PedidoGrupo() { }

    public PedidoGrupo(String cadeira, int type, String sender, String receiver) {
        this.cadeira = cadeira;
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getCadeira() {
        return  cadeira;
    }

    public int getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }
}
