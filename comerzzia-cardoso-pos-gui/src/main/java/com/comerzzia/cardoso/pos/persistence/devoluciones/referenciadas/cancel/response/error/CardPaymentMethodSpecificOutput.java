
package com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.cancel.response.error;

import javax.annotation.Generated;

import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.AcquirerData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class CardPaymentMethodSpecificOutput {

    @SerializedName("paymentProductId")
    @Expose
    private Integer paymentProductId;
    @SerializedName("transactionTimestamp")
    @Expose
    private String transactionTimestamp;
    @SerializedName("authorisationCode")
    @Expose
    private String authorisationCode;
    @SerializedName("card")
    @Expose
    private Card card;
    @SerializedName("acquirerData")
    @Expose
    private AcquirerData acquirerData;

    public Integer getPaymentProductId() {
        return paymentProductId;
    }

    public void setPaymentProductId(Integer paymentProductId) {
        this.paymentProductId = paymentProductId;
    }

    public String getTransactionTimestamp() {
        return transactionTimestamp;
    }

    public void setTransactionTimestamp(String transactionTimestamp) {
        this.transactionTimestamp = transactionTimestamp;
    }

    public String getAuthorisationCode() {
        return authorisationCode;
    }

    public void setAuthorisationCode(String authorisationCode) {
        this.authorisationCode = authorisationCode;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public AcquirerData getAcquirerData() {
        return acquirerData;
    }

    public void setAcquirerData(AcquirerData acquirerData) {
        this.acquirerData = acquirerData;
    }

}
