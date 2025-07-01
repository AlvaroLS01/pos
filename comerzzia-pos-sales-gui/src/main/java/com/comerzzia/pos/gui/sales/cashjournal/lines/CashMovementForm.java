


package com.comerzzia.pos.gui.sales.cashjournal.lines;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalConcept;
import com.comerzzia.pos.core.gui.validation.ValidationFormGui;
import com.comerzzia.pos.core.gui.validation.validators.number.IsNumeric;
import com.comerzzia.pos.util.format.FormatUtils;

@Component
@Scope("prototype")
public class CashMovementForm extends ValidationFormGui{

    @Size(min=0, max = 60, message = "El documento no puede superar los {max} caracteres")
    protected String document;
    
    @NotNull (message ="Campo requerido")
    protected CashJournalConcept concept;
    
    @IsNumeric(isAmount = true, message = "El importe introducido no es válido")
    @NotEmpty (message ="Campo requerido")
    protected String amount;
    
    @Size(min=0, max = 255, message = "La descripción del concepto no puede superar los {max} caracteres")
    @NotEmpty (message ="Campo requerido")
    protected String description;
    
    public CashMovementForm() {

    }
    
    public CashMovementForm(String documento, String importe, CashJournalConcept concepto) {
        this.document = documento;
        this.amount = importe;
        this.concept = concepto;
        this.description = concepto.getCashJournalConceptDes();
    }
    
    public String getDocument() {
        return document;
    }

    public void setDocument(String documento) {
        this.document = documento;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
    
    @Override
    public void clearForm() {
        document = "";
        amount = "";
        concept = null;
        description = "";
    }

    public CashJournalConcept getConcept() {
        return concept;
    }

    public void setConcept(CashJournalConcept concept) {
        this.concept = concept;
    }
    
    public BigDecimal getAmountAsBigDecimal() {
        return FormatUtils.getInstance().parseBigDecimal(amount);
    }

	public String getDescription() {
		return description;
	}

	public void setDescription(String descripcion) {
		this.description = descripcion;
	}    
    
}
