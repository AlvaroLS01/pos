
package com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.cancel.request;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class CancelPaymentSpecificInput {

    @SerializedName("reason")
    @Expose
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
