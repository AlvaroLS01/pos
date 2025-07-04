
package com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.cancel.request;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class CancelRequest {

    @SerializedName("cancelPaymentSpecificInput")
    @Expose
    private CancelPaymentSpecificInput cancelPaymentSpecificInput;

    public CancelPaymentSpecificInput getCancelPaymentSpecificInput() {
        return cancelPaymentSpecificInput;
    }

    public void setCancelPaymentSpecificInput(CancelPaymentSpecificInput cancelPaymentSpecificInput) {
        this.cancelPaymentSpecificInput = cancelPaymentSpecificInput;
    }

}
