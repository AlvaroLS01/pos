
package com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.approve.request;

import javax.annotation.Generated;

import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.EmvData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class ApproveRequest {

    @SerializedName("amount")
    @Expose
    private Integer amount;
    @SerializedName("emvData")
    @Expose
    private EmvData emvData;
    @SerializedName("paymentProductId")
    @Expose
    private Integer paymentProductId;

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public EmvData getEmvData() {
        return emvData;
    }

    public void setEmvData(EmvData emvData) {
        this.emvData = emvData;
    }

    public Integer getPaymentProductId() {
        return paymentProductId;
    }

    public void setPaymentProductId(Integer paymentProductId) {
        this.paymentProductId = paymentProductId;
    }

}
