package com.comerzzia.cardoso.pos.gui.ventas.devoluciones.referenciadas;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.FormularioGui;

@Component
@Scope("prototype")
public class FormularioDevolucionReferenciada extends FormularioGui {

	@NotEmpty(message = "Debe introducir el paymentId.")
	String paymentId;

	@NotEmpty(message = "Debe introducir el código del terminal.")
	String terminal;

	@NotEmpty(message = "Debe introducir el código de comercio.")
	String comercio;

	@NotEmpty(message = "Debe introducir el importe.")
	String importe;

	public FormularioDevolucionReferenciada() {

	}

	public FormularioDevolucionReferenciada(String paymentId, String terminal, String comercio, String importe) {

		this.paymentId = paymentId;
		this.terminal = terminal;
		this.comercio = comercio;
		this.importe = importe;
	}

	@Override
	public void limpiarFormulario() {
		paymentId = "";
		terminal = "";
		comercio = "";
		importe = "";
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public String getComercio() {
		return comercio;
	}

	public void setComercio(String comercio) {
		this.comercio = comercio;
	}

	public String getImporte() {
		return importe;
	}

	public void setImporte(String importe) {
		this.importe = importe;
	}

}
