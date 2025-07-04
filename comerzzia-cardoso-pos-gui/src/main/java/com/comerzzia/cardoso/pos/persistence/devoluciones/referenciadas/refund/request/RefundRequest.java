
package com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.refund.request;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class RefundRequest {

    @SerializedName("refundPaymentSpecificInput")
    @Expose
    private RefundPaymentSpecificInput refundPaymentSpecificInput;

    public RefundPaymentSpecificInput getRefundPaymentSpecificInput() {
        return refundPaymentSpecificInput;
    }

    public void setRefundPaymentSpecificInput(RefundPaymentSpecificInput refundPaymentSpecificInput) {
        this.refundPaymentSpecificInput = refundPaymentSpecificInput;
    }

}
