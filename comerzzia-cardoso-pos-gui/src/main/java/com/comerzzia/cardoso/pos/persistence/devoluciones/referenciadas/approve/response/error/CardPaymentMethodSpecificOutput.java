
package com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.approve.response.error;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class CardPaymentMethodSpecificOutput {

    @SerializedName("paymentProductId")
    @Expose
    private Integer paymentProductId;
    @SerializedName("authorisationCode")
    @Expose
    private String authorisationCode;
    @SerializedName("card")
    @Expose
    private Card card;

    public Integer getPaymentProductId() {
        return paymentProductId;
    }

    public void setPaymentProductId(Integer paymentProductId) {
        this.paymentProductId = paymentProductId;
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

}
