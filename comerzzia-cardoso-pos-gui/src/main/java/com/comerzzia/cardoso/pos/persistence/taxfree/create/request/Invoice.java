
package com.comerzzia.cardoso.pos.persistence.taxfree.create.request;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Invoice {

    @SerializedName("TransData")
    @Expose
    private List<TransDatum> transData;
    @SerializedName("invoiceId")
    @Expose
    private String invoiceId;

    public List<TransDatum> getTransData() {
        return transData;
    }

    public void setTransData(List<TransDatum> transData) {
        this.transData = transData;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

}
