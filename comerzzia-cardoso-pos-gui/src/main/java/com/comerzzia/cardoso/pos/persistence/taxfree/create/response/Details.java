
package com.comerzzia.cardoso.pos.persistence.taxfree.create.response;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Details {

    @SerializedName("CreateResponseV0")
    @Expose
    private CreateResponseV0 createResponseV0;

    public CreateResponseV0 getCreateResponseV0() {
        return createResponseV0;
    }

    public void setCreateResponseV0(CreateResponseV0 createResponseV0) {
        this.createResponseV0 = createResponseV0;
    }

}
