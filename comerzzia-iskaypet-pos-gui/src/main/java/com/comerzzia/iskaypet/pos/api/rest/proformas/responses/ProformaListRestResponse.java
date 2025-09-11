package com.comerzzia.iskaypet.pos.api.rest.proformas.responses;

import com.comerzzia.iskaypet.pos.services.proformas.rest.classes.ProformaHeaderDTO;

import java.util.Date;
import java.util.List;

public class ProformaListRestResponse {

    private int status;
    private String title;
    private String message;
    private Date timestamp;
    private List<ProformaHeaderDTO> proformas;

    public ProformaListRestResponse() {
    }

    public ProformaListRestResponse(int status, String title, String message, Date timestamp, List<ProformaHeaderDTO> proformas) {
        this.status = status;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.proformas = proformas;
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

    public List<ProformaHeaderDTO> getProformas() {
        return proformas;
    }

    public void setProformas(List<ProformaHeaderDTO> proformas) {
        this.proformas = proformas;
    }
}
