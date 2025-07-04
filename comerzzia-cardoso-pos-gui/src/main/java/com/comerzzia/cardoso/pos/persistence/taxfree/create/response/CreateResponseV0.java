
package com.comerzzia.cardoso.pos.persistence.taxfree.create.response;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class CreateResponseV0 {

    @SerializedName("BarCode")
    @Expose
    private String barCode;
    @SerializedName("Base")
    @Expose
    private Double base;
    @SerializedName("Refund")
    @Expose
    private Double refund;
    @SerializedName("Tax")
    @Expose
    private Double tax;
    @SerializedName("Total")
    @Expose
    private Double total;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Double getBase() {
        return base;
    }

    public void setBase(Double base) {
        this.base = base;
    }

    public Double getRefund() {
        return refund;
    }

    public void setRefund(Double refund) {
        this.refund = refund;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}
}
