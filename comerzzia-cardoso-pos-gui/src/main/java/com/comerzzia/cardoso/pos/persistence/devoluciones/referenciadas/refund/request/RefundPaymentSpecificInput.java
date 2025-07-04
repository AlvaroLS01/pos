
package com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.refund.request;

import javax.annotation.Generated;

import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.AmountOfMoney;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class RefundPaymentSpecificInput {

    @SerializedName("amountOfMoney")
    @Expose
    private AmountOfMoney amountOfMoney;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("merchantReference")
    @Expose
    private String merchantReference;

    public AmountOfMoney getAmountOfMoney() {
        return amountOfMoney;
    }

    public void setAmountOfMoney(AmountOfMoney amountOfMoney) {
        this.amountOfMoney = amountOfMoney;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getMerchantReference() {
        return merchantReference;
    }

    public void setMerchantReference(String merchantReference) {
        this.merchantReference = merchantReference;
    }

}
