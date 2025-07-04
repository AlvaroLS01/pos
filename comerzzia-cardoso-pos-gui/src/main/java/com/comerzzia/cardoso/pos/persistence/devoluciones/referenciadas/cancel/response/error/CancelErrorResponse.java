
package com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.cancel.response.error;

import java.util.List;

import javax.annotation.Generated;

import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.Payment;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class CancelErrorResponse {

    @SerializedName("errorId")
    @Expose
    private String errorId;
    @SerializedName("errors")
    @Expose
    private List<Error> errors;
    @SerializedName("payment")
    @Expose
    private Payment payment;

    public String getErrorId() {
        return errorId;
    }

    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

}
