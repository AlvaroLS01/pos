package com.comerzzia.iskaypet.pos.api.rest.proformas.responses;


import com.comerzzia.iskaypet.pos.services.proformas.rest.classes.ProformaDTO;

import java.util.Date;


public class ProformaRestResponse {

    private int status;
    private String title;
    private String message;
    private Date timestamp;
    ProformaDTO proforma;


    public ProformaRestResponse() {
    }

    public ProformaRestResponse(int status, String title, String message, Date timestamp, ProformaDTO proforma) {
        this.status = status;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.proforma = proforma;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public ProformaDTO getProforma() {
        return proforma;
    }

    public void setProforma(ProformaDTO proforma) {
        this.proforma = proforma;
    }
}
