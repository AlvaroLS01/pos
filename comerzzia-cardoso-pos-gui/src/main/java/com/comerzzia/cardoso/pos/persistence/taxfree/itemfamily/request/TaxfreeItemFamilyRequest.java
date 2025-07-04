
package com.comerzzia.cardoso.pos.persistence.taxfree.itemfamily.request;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class TaxfreeItemFamilyRequest {

    @SerializedName("OperationId")
    @Expose
    private String operationId;
    @SerializedName("JSONOperationData")
    @Expose
    private JSONOperationData jSONOperationData;

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public JSONOperationData getJSONOperationData() {
        return jSONOperationData;
    }

    public void setJSONOperationData(JSONOperationData jSONOperationData) {
        this.jSONOperationData = jSONOperationData;
    }

}
