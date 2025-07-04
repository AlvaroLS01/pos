
package com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.approve.response.error;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class References {

    @SerializedName("merchantOrderId")
    @Expose
    private String merchantOrderId;
    @SerializedName("merchantReference")
    @Expose
    private String merchantReference;

    public String getMerchantOrderId() {
        return merchantOrderId;
    }

    public void setMerchantOrderId(String merchantOrderId) {
        this.merchantOrderId = merchantOrderId;
    }

    public String getMerchantReference() {
        return merchantReference;
    }

    public void setMerchantReference(String merchantReference) {
        this.merchantReference = merchantReference;
    }

}
