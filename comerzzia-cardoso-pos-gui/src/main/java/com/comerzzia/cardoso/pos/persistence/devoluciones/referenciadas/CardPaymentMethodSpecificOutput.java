
package com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas;

import java.util.List;

import javax.annotation.Generated;

import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.refund.response.Card;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.refund.response.Token;
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
    @SerializedName("cardType")
    @Expose
    private String cardType;
    @SerializedName("card")
    @Expose
    private Card card;
    @SerializedName("acquirerData")
    @Expose
    private AcquirerData acquirerData;
    @SerializedName("tokens")
    @Expose
    private List<Token> tokens;
    @SerializedName("emvData")
    @Expose
    private EmvData emvData;

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

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
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

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public EmvData getEmvData() {
        return emvData;
    }

    public void setEmvData(EmvData emvData) {
        this.emvData = emvData;
    }

}
