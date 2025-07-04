
package com.comerzzia.cardoso.pos.persistence.taxfree.itemfamily.response;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class GetCustFamilyResponse {

    @SerializedName("Key")
    @Expose
    private Integer key;
    @SerializedName("Value")
    @Expose
    private Value value;

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

}
