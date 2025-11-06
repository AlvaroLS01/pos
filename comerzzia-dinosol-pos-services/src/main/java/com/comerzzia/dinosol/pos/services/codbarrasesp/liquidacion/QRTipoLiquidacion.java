package com.comerzzia.dinosol.pos.services.codbarrasesp.liquidacion;

public enum QRTipoLiquidacion { // DIN-112
	
	QR_LIQUIDACION_DESCUENTO("QR LIQUIDACION DESCUENTO", "07", "1"),
	QR_LIQUIDACION_CAMBIO_PRECIO("QR LIQUIDACION CAMBIO PRECIO", "07", "0");
	
	public final String label;
	public final String motivo;
	public final String tipo;

	private QRTipoLiquidacion(String label, String motivo, String tipo) {
		this.label = label;
		this.motivo = motivo;
		this.tipo = tipo;
	}

}
