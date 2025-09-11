
package com.comerzzia.iskaypet.pos.api.evicertia.client.response.EviSignSubmit;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Generated("jsonschema2pojo")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"lookupKey", "subject", "document", "signingParties", "interestedParties", "attachments", "options", "issuer"})
public class EvinSingSubmitDTO {

    // Variables
    @JsonProperty("lookupKey")
    private String lookupKey;
    @JsonProperty("subject")
    private String subject;
    @JsonProperty("document")
    private String document;
    @JsonProperty("signingParties")
    private List<SigningParty> signingParties;
    @JsonProperty("interestedParties")
    private List<InterestedParty> interestedParties;
    @JsonProperty("attachments")
    private List<Attachment> attachments;
    @JsonProperty("options")
    private Options options;
    @JsonProperty("issuer")
    private String issuer;
    @JsonProperty("additionalProperties")
    private Map<String, Object> additionalProperties;

    // Constructores
    public EvinSingSubmitDTO() {
    }

    public EvinSingSubmitDTO(String lookupKey, String subject, String document, List<SigningParty> signingParties,
                             List<InterestedParty> interestedParties, List<Attachment> attachments, Options options,
                             String issuer, Map<String, Object> additionalProperties) {
        this.lookupKey = lookupKey;
        this.subject = subject;
        this.document = document;
        this.signingParties = signingParties;
        this.interestedParties = interestedParties;
        this.attachments = attachments;
        this.options = options;
        this.issuer = issuer;
        this.additionalProperties = additionalProperties;
    }

    // Getters and Setters
    public String getLookupKey() {
        return lookupKey;
    }

    public void setLookupKey(String lookupKey) {
        this.lookupKey = lookupKey;
    }


    public String getSubject() {
        return subject;
    }


    public void setSubject(String subject) {
        this.subject = subject;
    }


    public String getDocument() {
        return document;
    }


    public void setDocument(String document) {
        this.document = document;
    }

    public List<SigningParty> getSigningParties() {
        return signingParties;
    }

    public void setSigningParties(List<SigningParty> signingParties) {
        this.signingParties = signingParties;
    }

    public void addSigningParty(SigningParty signingParty) {
        if (this.signingParties == null) {
            this.signingParties = new ArrayList<SigningParty>();
        }
        this.signingParties.add(signingParty);
    }

    public List<InterestedParty> getInterestedParties() {
        return interestedParties;
    }

    public void setInterestedParties(List<InterestedParty> interestedParties) {
        this.interestedParties = interestedParties;
    }

    public void addInterestedParty(InterestedParty interestedParty) {
        if (this.interestedParties == null) {
            this.interestedParties = new ArrayList<InterestedParty>();
        }
        this.interestedParties.add(interestedParty);
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public void addAttachment(Attachment attachment) {
        if (this.attachments == null) {
            this.attachments = new ArrayList<Attachment>();
        }
        this.attachments.add(attachment);
    }

    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }


    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public void addAdditionalProperty(String name, Object value) {
        if (this.additionalProperties == null) {
            this.additionalProperties = new LinkedHashMap<String, Object>();
        }
        this.additionalProperties.put(name, value);
    }

}
