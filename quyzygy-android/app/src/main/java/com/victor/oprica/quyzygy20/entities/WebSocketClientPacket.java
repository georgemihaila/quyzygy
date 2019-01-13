package com.victor.oprica.quyzygy20.entities;

public class WebSocketClientPacket {
    public Identity Identity;
    public String Action;
    public String Data;
    public String toJson(){
        return "{" + Identity.toJson() + ",\"Action\":\"" +
                Action + "\",\"Data\":\"" + Data + "\"}";
    }
}
