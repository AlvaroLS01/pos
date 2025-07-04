
package com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas;

import javax.annotation.Generated;

import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.approve.response.References;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class PaymentOutput {

    @SerializedName("amountOfMoney")
    @Expose
    private AmountOfMoney amountOfMoney;
    @SerializedName("references")
    @Expose
    private References references;
    @SerializedName("paymentMethod")
    @Expose
    private String paymentMethod;
    @SerializedName("cardPaymentMethodSpecificOutput")
    @Expose
    private CardPaymentMethodSpecificOutput cardPaymentMethodSpecificOutput;

    public AmountOfMoney getAmountOfMoney() {
        return amountOfMoney;
    }

    public void setAmountOfMoney(AmountOfMoney amountOfMoney) {
        this.amountOfMoney = amountOfMoney;
    }

    public References getReferences() {
        return references;
    }

    public void setReferences(References references) {
        this.references = references;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public CardPaymentMethodSpecificOutput getCardPaymentMethodSpecificOutput() {
        return cardPaymentMethodSpecificOutput;
    }

    public void setCardPaymentMethodSpecificOutput(CardPaymentMethodSpecificOutput cardPaymentMethodSpecificOutput) {
        this.cardPaymentMethodSpecificOutput = cardPaymentMethodSpecificOutput;
    }

}
