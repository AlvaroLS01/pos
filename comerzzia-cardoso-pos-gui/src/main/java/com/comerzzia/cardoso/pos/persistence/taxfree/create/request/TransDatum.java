
package com.comerzzia.cardoso.pos.persistence.taxfree.create.request;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class TransDatum {

    @SerializedName("Items")
    @Expose
    private List<Item> items;
    @SerializedName("TaxValue")
    @Expose
    private Integer taxValue;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Integer getTaxValue() {
        return taxValue;
    }

    public void setTaxValue(Integer taxValue) {
        this.taxValue = taxValue;
    }

}
