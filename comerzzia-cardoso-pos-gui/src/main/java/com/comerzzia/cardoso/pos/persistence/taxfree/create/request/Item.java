
package com.comerzzia.cardoso.pos.persistence.taxfree.create.request;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Item {

	@SerializedName("BaseTotal")
    @Expose
    private Object baseTotal;
    @SerializedName("Family")
    @Expose
    private Object family;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Qty")
    @Expose
    private Integer qty;
    @SerializedName("Reference")
    @Expose
    private Object reference;
    @SerializedName("SalesTotal")
    @Expose
    private Double salesTotal;
    @SerializedName("Serial")
    @Expose
    private String serial;

    public Object getBaseTotal() {
		return baseTotal;
	}

	public void setBaseTotal(Object baseTotal) {
		this.baseTotal = baseTotal;
	}

	public Object getFamily() {
        return family;
    }

    public void setFamily(Object family) {
        this.family = family;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Object getReference() {
        return reference;
    }

    public void setReference(Object reference) {
        this.reference = reference;
    }

    public Double getSalesTotal() {
        return salesTotal;
    }

    public void setSalesTotal(Double salesTotal) {
        this.salesTotal = salesTotal;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

}
