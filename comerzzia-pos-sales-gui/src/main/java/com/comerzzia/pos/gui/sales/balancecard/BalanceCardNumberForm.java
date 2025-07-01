package com.comerzzia.pos.gui.sales.balancecard;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.ValidationFormGui;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Scope("prototype")
@NoArgsConstructor
public class BalanceCardNumberForm extends ValidationFormGui {

	@NotEmpty(message = "Campo requerido")
	@Getter
	@Setter
	protected String balanceCardNumber;

	@Override
	public void clearForm() {
		balanceCardNumber = "";
	}

}
