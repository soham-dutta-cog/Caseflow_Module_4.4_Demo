package com.demo;

public class StageDefinition {
    private  int seqNum;
    private String role;
    private  int slaDays;

    public StageDefinition(int sn, String r, int sla){
        this.role = r;
        this.slaDays = sla;
        this.seqNum = sn;
    }


    public int getSeqNum() {
        return seqNum;
    }

    public int getSlaDays() {
        return slaDays;
    }

    public String getRole() {
        return role;
    }
}
