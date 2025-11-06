package com.comerzzia.dinosol.pos.gui.ventas.tickets.pagos.vales;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.FormularioGui;

@Component
@Scope("prototype")
public class FormularioValesPagos extends FormularioGui {

	@NotEmpty(message = "Campo requerido")
	protected String codMedioPago;

	@NotEmpty(message = "Campo requerido")
	protected BigDecimal importe;

	public FormularioValesPagos() {
	}

	@Override
	public void limpiarFormulario() {
		codMedioPago = "";
		importe = BigDecimal.ZERO;
	}

	public String getCodMedioPago() {
		return codMedioPago;
	}

	public void setCodMedioPago(String codMedioPago) {
		this.codMedioPago = codMedioPago;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

}
