


package com.comerzzia.pos.gui.sales.document;

import java.util.Date;

import com.comerzzia.core.facade.model.DocTypeDetail;
import com.comerzzia.omnichannel.facade.model.documents.TicketDocument;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.util.config.SpringContext;


public class DocumentManagementDto {
    
    // Datos que se muestran
    protected String till;
    protected Date date;
    protected long documentNumber;
    protected String docTypeCode;
    protected String locatorCode;
    protected Long docTypeId;
    protected String docTypeDes;
    
    // Datos que vamos a usar para pasarselos al detalle
    protected String documentUid;
    
    protected byte[] documentXML;
    
    public DocumentManagementDto(TicketDocument document){
        
        this.till = document.getTillCode();
        this.date =  document.getCreationDate();
        this.documentNumber = document.getDocumentNumber();
        this.documentUid = document.getDocumentUid();
        this.locatorCode = document.getLocatorCode();
        
        DocTypeDetail docType = SpringContext.getBean(Session.class).getApplicationSession().getDocTypeByDocTypeId(document.getDocTypeId());
        this.docTypeDes = docType.getDocTypeDes();
        this.docTypeCode = docType.getDocTypeCode();
        this.docTypeId = docType.getDocTypeId();
    }

	/**
     * @return the caja
     */
    public String getTill() {
        return till;
    }

    /**
     * @param caja the caja to set
     */
    public void setTill(String till) {
        this.till = till;
    }

    /**
     * @return the fecha
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the idTicket
     */
    public long getDocumentNumber() {
        return documentNumber;
    }

    /**
     * @param idTicket the idTicket to set
     */
    public void setDocumentNumber(long documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentUid() {
        return documentUid;
    }

    public void setDocumentUid(String documentUid) {
        this.documentUid = documentUid;
    }

    public String getDocTypeDes() {
        return docTypeDes;
    }

    public void setDocTypeDes(String desDoc) {
        this.docTypeDes = desDoc;
    }

	public byte[] getDocumentXML() {
		return documentXML;
	}

	public String getDocTypeCode() {
		return docTypeCode;
	}

	public void setDocTypeCode(String docTypeCode) {
		this.docTypeCode = docTypeCode;
	}
    
	public Long getDocTypeId() {
		return docTypeId;
	}

	public void setDocTypeId(Long docTypeId) {
		this.docTypeId = docTypeId;
	}

	public String getLocatorCode() {
		return locatorCode;
	}

	public void setLocatorCode(String locatorId) {
		this.locatorCode = locatorId;
	}
	
	
}
