
package com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas;

import javax.annotation.Generated;

import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.refund.response.AcquirerResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class StatusOutput {

    @SerializedName("statusCodeChangeDateTime")
    @Expose
    private String statusCodeChangeDateTime;
    @SerializedName("isAuthorized")
    @Expose
    private Boolean isAuthorized;
    @SerializedName("acquirerResponse")
    @Expose
    private AcquirerResponse acquirerResponse;

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
