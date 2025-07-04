
package com.comerzzia.cardoso.pos.persistence.taxfree.create.request;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class TaxfreeCreateRequest {

    @SerializedName("OperationId")
    @Expose
    private String operationId;
    @SerializedName("OperationData")
    @Expose
    private OperationData operationData;

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public OperationData getOperationData() {
        return operationData;
    }

    public void setOperationData(OperationData operationData) {
        this.operationData = operationData;
    }

}
