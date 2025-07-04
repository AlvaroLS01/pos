
package com.comerzzia.cardoso.pos.persistence.taxfree.cancellation.response;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class TaxfreeCancellationResponse {

    @SerializedName("ErrorCode")
    @Expose
    private String errorCode;
    @SerializedName("HasError")
    @Expose
    private String hasError;
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("TransDate")
    @Expose
    private String transDate;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getHasError() {
        return hasError;
    }

    public void setHasError(String hasError) {
        this.hasError = hasError;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

}
