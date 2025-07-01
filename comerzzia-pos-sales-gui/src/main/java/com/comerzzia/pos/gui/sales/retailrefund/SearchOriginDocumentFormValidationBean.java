


package com.comerzzia.pos.gui.sales.retailrefund;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.ValidationFormGui;

@Component
@Scope("prototype")
public class SearchOriginDocumentFormValidationBean extends ValidationFormGui{
    
    @NotEmpty(message = "El campo 'Código de ticket' no puede estar vacío.")
    String codOperation;
    
    @NotEmpty(message = "El campo 'Tipo de documento' no puede estar vacío.")
    @Size(max = 4, message = "El campo 'Código de documento' no puede tener más de {max} dígitos.")
    String codDoc;
    
    public SearchOriginDocumentFormValidationBean(){
        
    }
    
    public SearchOriginDocumentFormValidationBean(String codTienda, String codCaja, String codOperacion, String tipoDoc){
        
        this.codOperation = codOperacion;
        this.codDoc = tipoDoc;
    }

    public String getCodOperation() {
        return codOperation;
    }

    public void setCodOperation(String codOperacion) {
        this.codOperation = codOperacion!=null?codOperacion.trim():codOperacion;
    }

    public String getCodDoc() {
        return codDoc;
    }

    public void setCodDoc(String codDoc) {
        this.codDoc = codDoc!=null?codDoc.trim():codDoc;
    }
    
    @Override
    public void clearForm() {
        
        codOperation = "";
        codDoc = "";
    }
    
}
