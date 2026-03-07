package com.securestatements.model;

public class Token {

    private int id;
    private int statementId;
    private String token;
    private long expiryTime;
    private boolean used;

    public int getStatementId(){ return statementId; }
    public void setStatementId(int statementId){ this.statementId = statementId; }

    public String getToken(){ return token; }
    public void setToken(String token){ this.token = token; }

    public long getExpiryTime(){ return expiryTime; }
    public void setExpiryTime(long expiryTime){ this.expiryTime = expiryTime; }

    public boolean isUsed(){ return used; }
    public void setUsed(boolean used){ this.used = used; }
}
