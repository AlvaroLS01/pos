
package com.comerzzia.cardoso.pos.persistence.taxfree.create.request;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Document {

    @SerializedName("TaxFreeMode")
    @Expose
    private Integer taxFreeMode;

    public Integer getTaxFreeMode() {
        return taxFreeMode;
    }

    public void setTaxFreeMode(Integer taxFreeMode) {
        this.taxFreeMode = taxFreeMode;
    }

}
