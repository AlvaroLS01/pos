
package com.comerzzia.cardoso.pos.persistence.taxfree.barcodecreation.request;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class JSONOperationData {

    @SerializedName("DataRequest")
    @Expose
    private String dataRequest;
    @SerializedName("CustAccount")
    @Expose
    private String custAccount;
    @SerializedName("Barcode")
    @Expose
    private String barcode;
    @SerializedName("Parameters")
    @Expose
    private String parameters;

    public String getDataRequest() {
        return dataRequest;
    }

    public void setDataRequest(String dataRequest) {
        this.dataRequest = dataRequest;
    }

    public String getCustAccount() {
        return custAccount;
    }

    public void setCustAccount(String custAccount) {
        this.custAccount = custAccount;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

}
