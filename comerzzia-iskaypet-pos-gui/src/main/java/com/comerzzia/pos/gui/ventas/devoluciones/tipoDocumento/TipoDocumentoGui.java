/**
 * ComerZZia 3.0
 *
 * Copyright (c) 2008-2015 Comerzzia, S.L.  All Rights Reserved.
 *
 * THIS WORK IS  SUBJECT  TO  SPAIN  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 * TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 * EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 *
 * CONSULT THE END USER LICENSE AGREEMENT FOR INFORMATION ON ADDITIONAL
 * RESTRICTIONS.
 */


package com.comerzzia.pos.gui.ventas.devoluciones.tipoDocumento;

import javafx.beans.property.SimpleStringProperty;

import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;


public class TipoDocumentoGui {
    
    private SimpleStringProperty desDoc;
    private SimpleStringProperty codDoc;
    
    private TipoDocumentoBean doc;
    
    public TipoDocumentoGui(TipoDocumentoBean doc){
        
        this.doc = doc;
        desDoc = new SimpleStringProperty(doc.getDestipodocumento());
        codDoc = new SimpleStringProperty(doc.getCodtipodocumento());
    }

    public SimpleStringProperty getDesDoc() {
        return desDoc;
    }

    public void setDesDoc(SimpleStringProperty desDoc) {
        this.desDoc = desDoc;
    }

    public SimpleStringProperty getCodDoc() {
        return codDoc;
    }

    public void setCodDoc(SimpleStringProperty codDoc) {
        this.codDoc = codDoc;
    }

    public TipoDocumentoBean getDoc() {
        return doc;
    }

}
