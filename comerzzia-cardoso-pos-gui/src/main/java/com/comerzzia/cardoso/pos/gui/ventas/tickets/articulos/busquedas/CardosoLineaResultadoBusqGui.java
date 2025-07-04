package com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.busquedas;

import java.math.BigDecimal;

import com.comerzzia.cardoso.pos.persistence.articulos.buscar.CardosoArticuloBuscarBean;
import com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas.LineaResultadoBusqGui;
import com.comerzzia.pos.persistence.articulos.buscar.ArticuloBuscarBean;

/**
 * GAP - DESCUENTO TARIFA
 */
public class CardosoLineaResultadoBusqGui extends LineaResultadoBusqGui{

	private Boolean promocionado;
	private BigDecimal descuentoTarifa;

	public CardosoLineaResultadoBusqGui(ArticuloBuscarBean articuloBuscar){
		super(articuloBuscar);
		try{
			promocionado = ((CardosoArticuloBuscarBean) articuloBuscar).getPromocionado();
			descuentoTarifa = ((CardosoArticuloBuscarBean) articuloBuscar).getDescuentoTarifa();
		}
		catch(Exception e){
			promocionado = false;
			descuentoTarifa = BigDecimal.ZERO;
		}
	}

	public Boolean getPromocionado(){
		return promocionado;
	}

	public void setPromocionado(Boolean promocionado){
		this.promocionado = promocionado;
	}

	public BigDecimal getDescuentoTarifa(){
		return descuentoTarifa;
	}

	public void setDescuentoTarifa(BigDecimal descuentoTarifa){
		this.descuentoTarifa = descuentoTarifa;
	}

}
