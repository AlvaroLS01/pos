
package com.comerzzia.cardoso.pos.persistence.taxfree.itemfamily.response;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Details {

    @SerializedName("GetCustFamilyResponse")
    @Expose
    private List<GetCustFamilyResponse> getCustFamilyResponse;

    public List<GetCustFamilyResponse> getGetCustFamilyResponse() {
        return getCustFamilyResponse;
    }

    public void setGetCustFamilyResponse(List<GetCustFamilyResponse> getCustFamilyResponse) {
        this.getCustFamilyResponse = getCustFamilyResponse;
    }

}
