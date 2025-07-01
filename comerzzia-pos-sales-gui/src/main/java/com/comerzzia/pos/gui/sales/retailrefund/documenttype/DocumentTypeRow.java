


package com.comerzzia.pos.gui.sales.retailrefund.documenttype;

import com.comerzzia.core.facade.model.DocTypeDetail;
import com.comerzzia.pos.core.gui.helper.HelperRow;

import javafx.beans.property.SimpleStringProperty;


public class DocumentTypeRow extends HelperRow<DocTypeDetail>{
    
    
    public DocumentTypeRow(DocTypeDetail doc){
        object = doc;
        helperDesc = new SimpleStringProperty(doc.getDocTypeDes());
        helperCode = new SimpleStringProperty(doc.getDocTypeCode());
    }

}
