
package com.comerzzia.cardoso.pos.persistence.taxfree.barcodecreation.response;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Details {

    @SerializedName("Field1")
    @Expose
    private String field1;
    @SerializedName("Field2")
    @Expose
    private String field2;
    @SerializedName("Field3")
    @Expose
    private Integer field3;

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public Integer getField3() {
        return field3;
    }

    public void setField3(Integer field3) {
        this.field3 = field3;
    }

}
