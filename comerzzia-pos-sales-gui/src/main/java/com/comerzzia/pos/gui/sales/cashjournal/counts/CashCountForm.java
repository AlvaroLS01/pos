
package com.comerzzia.pos.gui.sales.cashjournal.counts;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.ValidationFormGui;

@Component
@Scope("prototype")
public class CashCountForm extends ValidationFormGui {

	@NotEmpty (message ="Campo requerido")
	private String amount;
	
	public CashCountForm(){
		
	}
	
	public String getAmount() {
        return amount;
    }

    public void setAmount(String importe) {
        this.amount = importe;
    }
	
	@Override
	public void clearForm() {
		
		amount = "";
	}

}
