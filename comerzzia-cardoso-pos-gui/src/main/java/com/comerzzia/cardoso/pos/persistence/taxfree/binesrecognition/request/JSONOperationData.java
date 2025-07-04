
package com.comerzzia.cardoso.pos.persistence.taxfree.binesrecognition.request;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class JSONOperationData {

    @SerializedName("DataRequest")
    @Expose
    private String dataRequest;
    @SerializedName("CustAccount")
    @Expose
    private String custAccount;
    @SerializedName("FormCountry")
    @Expose
    private String formCountry;
    @SerializedName("Bines")
    @Expose
    private String bines;
    @SerializedName("Amount")
    @Expose
    private String amount;

    public String getDataRequest() {
        return dataRequest;
    }

    public void setDataRequest(String dataRequest) {
        this.dataRequest = dataRequest;
    }

    public String getCustAccount() {
        return custAccount;
    }

    public void setCustAccount(String custAccount) {
        this.custAccount = custAccount;
    }

    public String getFormCountry() {
        return formCountry;
    }

    public void setFormCountry(String formCountry) {
        this.formCountry = formCountry;
    }

    public String getBines() {
        return bines;
    }

    public void setBines(String bines) {
        this.bines = bines;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

}
