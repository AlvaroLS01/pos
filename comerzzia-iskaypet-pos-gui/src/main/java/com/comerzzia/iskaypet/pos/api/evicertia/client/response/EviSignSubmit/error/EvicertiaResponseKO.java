
package com.comerzzia.iskaypet.pos.api.evicertia.client.response.EviSignSubmit.error;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "responseStatus"
})
@Generated("jsonschema2pojo")
public class EvicertiaResponseKO {

    @JsonProperty("responseStatus")
    private ResponseStatus responseStatus;

    @JsonProperty("responseStatus")
    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    @JsonProperty("responseStatus")
    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

}
