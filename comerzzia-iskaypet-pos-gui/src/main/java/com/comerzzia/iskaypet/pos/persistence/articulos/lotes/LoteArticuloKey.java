package com.comerzzia.iskaypet.pos.persistence.articulos.lotes;

public class LoteArticuloKey {
    private String activityUid;

    private String whCode;

    private String itemCode;

    private String combination1Code;

    private String combination2Code;

    private String batchNumberS4;

    public String getActivityUid() {
        return activityUid;
    }

    public void setActivityUid(String activityUid) {
        this.activityUid = activityUid == null ? null : activityUid.trim();
    }

    public String getWhCode() {
        return whCode;
    }

    public void setWhCode(String whCode) {
        this.whCode = whCode == null ? null : whCode.trim();
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode == null ? null : itemCode.trim();
    }

    public String getCombination1Code() {
        return combination1Code;
    }

    public void setCombination1Code(String combination1Code) {
        this.combination1Code = combination1Code == null ? null : combination1Code.trim();
    }

    public String getCombination2Code() {
        return combination2Code;
    }

    public void setCombination2Code(String combination2Code) {
        this.combination2Code = combination2Code == null ? null : combination2Code.trim();
    }

    public String getBatchNumberS4() {
        return batchNumberS4;
    }

    public void setBatchNumberS4(String batchNumberS4) {
        this.batchNumberS4 = batchNumberS4 == null ? null : batchNumberS4.trim();
    }
}