
package com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class AcquirerData {

    @SerializedName("acquirerMerchantID")
    @Expose
    private String acquirerMerchantID;
    @SerializedName("acquirerTerminalID")
    @Expose
    private String acquirerTerminalID;

    public String getAcquirerMerchantID() {
        return acquirerMerchantID;
    }

    public void setAcquirerMerchantID(String acquirerMerchantID) {
        this.acquirerMerchantID = acquirerMerchantID;
    }

    public String getAcquirerTerminalID() {
        return acquirerTerminalID;
    }

    public void setAcquirerTerminalID(String acquirerTerminalID) {
        this.acquirerTerminalID = acquirerTerminalID;
    }

}
