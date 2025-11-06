package com.comerzzia.dinosol.pos.services.promociones.tipos.especificos.detalles;

public class LineaDetallePromocionRascaBoletos {

	protected final String codArticulo;
	protected final String desglose1;
	protected final String desglose2;

	public LineaDetallePromocionRascaBoletos(String codArticulo, String desglose1, String desglose2) {
		super();
		this.codArticulo = codArticulo;
		this.desglose1 = desglose1;
		this.desglose2 = desglose2;
	}

	public String getCodArticulo() {
		return codArticulo;
	}

	public String getDesglose1() {
		return desglose1;
	}

	public String getDesglose2() {
		return desglose2;
	}

}
