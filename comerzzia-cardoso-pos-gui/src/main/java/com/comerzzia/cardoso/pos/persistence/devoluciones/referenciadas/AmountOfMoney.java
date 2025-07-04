
package com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class AmountOfMoney {

    @SerializedName("amount")
    @Expose
    private Integer amount;
    @SerializedName("currencyCode")
    @Expose
    private String currencyCode;

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

}
