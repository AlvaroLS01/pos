
package com.comerzzia.cardoso.pos.persistence.taxfree.itemfamily.response;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Value {

    @SerializedName("Family")
    @Expose
    private String family;
    @SerializedName("Tax100")
    @Expose
    private Integer tax100;

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public Integer getTax100() {
        return tax100;
    }

    public void setTax100(Integer tax100) {
        this.tax100 = tax100;
    }

}
