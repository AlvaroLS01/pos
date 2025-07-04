
package com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Payment {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("paymentOutput")
    @Expose
    private PaymentOutput paymentOutput;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("statusOutput")
    @Expose
    private StatusOutput statusOutput;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PaymentOutput getPaymentOutput() {
        return paymentOutput;
    }

    public void setPaymentOutput(PaymentOutput paymentOutput) {
        this.paymentOutput = paymentOutput;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public StatusOutput getStatusOutput() {
        return statusOutput;
    }

    public void setStatusOutput(StatusOutput statusOutput) {
        this.statusOutput = statusOutput;
    }

}
