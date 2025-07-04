
package com.comerzzia.cardoso.pos.persistence.taxfree.create.request;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Store {

    @SerializedName("countryId")
    @Expose
    private String countryId;
    @SerializedName("custAccount")
    @Expose
    private String custAccount;

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getCustAccount() {
        return custAccount;
    }

    public void setCustAccount(String custAccount) {
        this.custAccount = custAccount;
    }

}
