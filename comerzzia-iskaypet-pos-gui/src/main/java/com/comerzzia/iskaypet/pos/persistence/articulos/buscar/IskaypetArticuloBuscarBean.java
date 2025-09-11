package com.comerzzia.iskaypet.pos.persistence.articulos.buscar;

import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.persistence.articulos.buscar.ArticuloBuscarBean;
import com.comerzzia.pos.persistence.articulos.codBarras.ArticuloCodBarraBean;
import com.comerzzia.pos.persistence.articulos.tarifas.TarifaDetalleBean;

public class IskaypetArticuloBuscarBean extends ArticuloBuscarBean{

	private String ean;

	public IskaypetArticuloBuscarBean(){
	}

	public IskaypetArticuloBuscarBean(ArticuloBean articulo, ArticuloCodBarraBean codigoBarras, TarifaDetalleBean tarifa){
		super(articulo, codigoBarras, tarifa);
		this.ean = codigoBarras.getCodigoBarras();
	}

	public String getEan(){
		return ean;
	}

	public void setEan(String ean){
		this.ean = ean;
	}

}
