
package com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.refund.response.error;

import java.util.List;

import javax.annotation.Generated;

import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.refund.response.AcquirerResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class StatusOutput {

    @SerializedName("errors")
    @Expose
    private List<Error__1> errors;
    @SerializedName("statusCodeChangeDateTime")
    @Expose
    private String statusCodeChangeDateTime;
    @SerializedName("isAuthorized")
    @Expose
    private Boolean isAuthorized;
    @SerializedName("acquirerResponse")
    @Expose
    private AcquirerResponse acquirerResponse;

    public List<Error__1> getErrors() {
        return errors;
    }

    public void setErrors(List<Error__1> errors) {
        this.errors = errors;
    }

    public String getStatusCodeChangeDateTime() {
        return statusCodeChangeDateTime;
    }

    public void setStatusCodeChangeDateTime(String statusCodeChangeDateTime) {
        this.statusCodeChangeDateTime = statusCodeChangeDateTime;
    }

    public Boolean getIsAuthorized() {
        return isAuthorized;
    }

    public void setIsAuthorized(Boolean isAuthorized) {
        this.isAuthorized = isAuthorized;
    }

    public AcquirerResponse getAcquirerResponse() {
        return acquirerResponse;
    }

    public void setAcquirerResponse(AcquirerResponse acquirerResponse) {
        this.acquirerResponse = acquirerResponse;
    }

}
