package com.comerzzia.iskaypet.pos.gui.ventas.cajas.contadora;

public enum ContadoraTipoPagosEnum {

	EFECTIVO("0000", "Efectivo", "0", 0),
	TARJETA("0010", "Tarjeta", "0,00", 1),
	TARJETA_GPRS("0011", "Tarjeta GPRS", "0,00", 1),
	VALE_DEVOLUCION("0012", "Vale", "0,00", 1),
	GLOVO("0013", "Glovo", "0,00", 1),
	UBER("0014", "Uber", "0,00", 1),
	VALE("0020", "Vale", "0,00", 1),
	DESCUENTO_PROMOCIONAL("0090", "Descuento Promocional", "0,00", 1),
	TRANSFERENCIA("0110", "Transferencia", "0,00", 1),
	FINANCIACION("0140", "Financiación", "0,00", 1),
	FRAKMENTA("0200", "Frakmenta", "0,00", 1),
	PAYGOLD("0210", "Paygold", "0,00", 1),
	GLOBAL_PAYMENTS("0220", "Global Payments", "0,00", 1),
	PAGO_REMOTO("9000", "Pago Remoto", "0,00", 1),
	TOTAL(null, "Total", "0,00", 1),
	TOTAL_EFECTIVO(null, "Total efectivo", "0,00", 1);

	private String codPago;

	private String text;
	private String defaultValue;
	private Integer defaultCantidad;

	ContadoraTipoPagosEnum(String codPago, String text, String defaultValue, Integer defaultCantidad) {
		this.codPago = codPago;
		this.text = text;
		this.defaultValue = defaultValue;
		this.defaultCantidad = defaultCantidad;
	}

	public String getCodPago() {
		return codPago;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public Integer getDefaultCantidad() {
		return defaultCantidad;
	}

	public String getText() {
		return text;
	}
}
