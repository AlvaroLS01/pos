
package com.comerzzia.cardoso.pos.persistence.taxfree.touristdata.request;

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
    @SerializedName("TouristID")
    @Expose
    private String touristID;
    @SerializedName("Passport")
    @Expose
    private String passport;
    @SerializedName("TouristCountry")
    @Expose
    private String touristCountry;
    @SerializedName("Email")
    @Expose
    private String email;

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

    public String getTouristID() {
        return touristID;
    }

    public void setTouristID(String touristID) {
        this.touristID = touristID;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public String getTouristCountry() {
        return touristCountry;
    }

    public void setTouristCountry(String touristCountry) {
        this.touristCountry = touristCountry;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
