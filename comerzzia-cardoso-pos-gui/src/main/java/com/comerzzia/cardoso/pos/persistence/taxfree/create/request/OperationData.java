
package com.comerzzia.cardoso.pos.persistence.taxfree.create.request;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class OperationData {

    @SerializedName("Document")
    @Expose
    private Document document;
    @SerializedName("Invoice")
    @Expose
    private Invoice invoice;
    @SerializedName("Store")
    @Expose
    private Store store;
    @SerializedName("Tourist")
    @Expose
    private Tourist tourist;

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Tourist getTourist() {
        return tourist;
    }

    public void setTourist(Tourist tourist) {
        this.tourist = tourist;
    }

}
