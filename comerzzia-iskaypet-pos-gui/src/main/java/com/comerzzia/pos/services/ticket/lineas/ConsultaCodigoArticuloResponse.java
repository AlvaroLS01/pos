package com.comerzzia.pos.services.ticket.lineas;

import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.persistence.articulos.codBarras.ArticuloCodBarraBean;

public class ConsultaCodigoArticuloResponse {
	
	private ArticuloCodBarraBean codigoBarras;
	private ArticuloBean articulo;
	private String desglose1;
	private String desglose2;

	public ConsultaCodigoArticuloResponse(ArticuloCodBarraBean codigoBarras, ArticuloBean articulo, String desglose1, String desglose2) {
		this.codigoBarras = codigoBarras;
		this.articulo = articulo;
		this.desglose1 = desglose1;
		this.desglose2 = desglose2;
	}

	public ArticuloCodBarraBean getCodigoBarras() {
		return codigoBarras;
	}

	public ArticuloBean getArticulo() {
		return articulo;
	}

	public String getDesglose1() {
		return desglose1;
	}

	public String getDesglose2() {
		return desglose2;
	}

	public boolean hasCodigoBarras() {
		return codigoBarras != null;
	}

}