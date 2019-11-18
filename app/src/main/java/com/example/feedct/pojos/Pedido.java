package com.example.feedct.pojos;

public class Pedido {
    public static final int SINGLE = 0;
    public static final int MERGE = 1;

    private int type;
    private String sender;
    private String receiver;

    public Pedido(int type, String sender, String receiver) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
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
