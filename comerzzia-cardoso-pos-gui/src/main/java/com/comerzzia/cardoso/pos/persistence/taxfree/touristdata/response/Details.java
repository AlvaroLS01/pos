
package com.comerzzia.cardoso.pos.persistence.taxfree.touristdata.response;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Details {

    @SerializedName("OpResultTouristData")
    @Expose
    private OpResultTouristData opResultTouristData;

    public OpResultTouristData getOpResultTouristData() {
        return opResultTouristData;
    }

    public void setOpResultTouristData(OpResultTouristData opResultTouristData) {
        this.opResultTouristData = opResultTouristData;
    }

}
