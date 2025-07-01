

package com.comerzzia.pos.gui.sales.cashjournal.close;

import java.util.Date;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.ValidationFormGui;
import com.comerzzia.pos.core.gui.validation.validators.date.IsValidDate;
import com.comerzzia.pos.util.format.FormatUtils;

@Component
@Scope("prototype")
public class CloseCashJournalForm extends ValidationFormGui {

    @NotEmpty(message = "Ha de indicar una fecha de cierre")
    @IsValidDate(past = true, future = true, message = "La fecha de cierre introducida no es válida", isDateTime = false)
    protected String closingDateStr;

    protected Date closingDate;
    
    @Override
    public void clearForm() {
        closingDateStr = "";
    }

    public void setClosingDateStr(String closingDateStr) {
        this.closingDateStr = closingDateStr;
    }

	public Date getClosingDate() {
		if(closingDate == null){
			return FormatUtils.getInstance().parseDate(closingDateStr);
		}else{
			return closingDate;
		}
	}

	public void setClosingDate(Date closingDate) {
		this.closingDate = closingDate;
	}

    
    
}
