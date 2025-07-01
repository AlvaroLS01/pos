

package com.comerzzia.pos.gui.sales.document;

import java.util.Date;

import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.ValidationFormGui;
import com.comerzzia.pos.core.gui.validation.validators.date.IsValidDate;
import com.comerzzia.pos.core.gui.validation.validators.number.IsNumeric;
import com.comerzzia.pos.util.format.FormatUtils;

@Component
@Scope("prototype")
public class DocumentManagementForm extends ValidationFormGui {

    @Size(min = 0, max = 4, message = "La longitud del código no puede tener más de {max} dígitos.")
    private String tillCode;

    @Size(min = 0, max = 10, message = "La longitud del código no puede tener más de {max} dígitos.")
    @IsNumeric(decimals = 0, message = "El código del ticket no es válido.")
    private String documentNumber;

    @NotEmpty(message = "La fecha no puede ser nula")
    @IsValidDate(past = true, future = true, message = "La fecha introducida no tiene un formato correcto.")
    private String date;
    
    @Size(min = 0, max = 10, message = "La longitud del código no puede tener más de {max} dígitos.")
    private String docTypeCode;

    public DocumentManagementForm() {
    }

    public DocumentManagementForm(String tillCode, String documentNumber, String date, String docTypeCode) {

        this.tillCode = tillCode;
        this.documentNumber = documentNumber;
        this.date = date;
        this.docTypeCode = docTypeCode;
    }

    public String getTillCode() {
        return tillCode;
    }

    public void setTillCode(String tillCode) {
        this.tillCode = tillCode!=null?(StringUtils.isNotBlank(tillCode)?tillCode.trim():null):tillCode;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber!=null?(StringUtils.isNotBlank(documentNumber.trim())?documentNumber.trim():null):documentNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date!=null?(StringUtils.isNotBlank(date.trim())?date.trim():null):date;
    }

    public String getDocTypeCode() {
        return docTypeCode;
    }

    public void setDocTypeCode(String docTypeCode) {
        this.docTypeCode = docTypeCode!=null?StringUtils.isNotBlank(docTypeCode.trim())?docTypeCode.trim():null:docTypeCode;
    }

    @Override
    public void clearForm() {

        tillCode = "";
        documentNumber = "";
        date = "";
        docTypeCode = "";
    }
    
    public Date getDateAsDate() {
        Date res = null;
        if (StringUtils.isNotBlank(date)){
            res =  FormatUtils.getInstance().parseDate(date);
        }
        return res;
    }
    
    public Long getDocumentNumberAsLong() {
        Long res = null;
        if (StringUtils.isNotBlank(documentNumber)){
            res =  FormatUtils.getInstance().parseLong(documentNumber);
            
        }
        return res;
    }
}
