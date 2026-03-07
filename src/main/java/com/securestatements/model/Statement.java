package com.securestatements.model;

public class Statement {

    private int id;
    private int customerId;
    private String filePath;

    public int getId(){ return id; }
    public void setId(int id){ this.id = id; }

    public int getCustomerId(){ return customerId; }
    public void setCustomerId(int customerId){ this.customerId = customerId; }

    public String getFilePath(){ return filePath; }
    public void setFilePath(String filePath){ this.filePath = filePath; }
}
