package com.victor.oprica.quyzygy20.entities;

public class Identity {
    public String SecretKey;
    public String WSID;
    public int AccessCode;
    public String toJson(){
        return "\"Identity\":{" +
                "\"SecretKey\":\"" +
                SecretKey + "\"," +
                "\"WSID\":\"" +
                WSID + "\"," +
                "\"AccessCode\":" + AccessCode + "}";
    }
}
