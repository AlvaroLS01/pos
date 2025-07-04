package com.comerzzia.cardoso.pos.persistence.promociones.monograficas;

import java.math.BigDecimal;

import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;

/**
 * GAP - PERSONALIZACIONES V3 - PROMOCIONES MONOGRÁFICAS
 */
public class MonograficaPromocionLineaTicket extends PromocionLineaTicket{

	private BigDecimal descuento;

	public MonograficaPromocionLineaTicket(PromocionTicket promocionTicket){
		super(promocionTicket);
	}

	public BigDecimal getDescuento(){
		return descuento;
	}

	public void setDescuento(BigDecimal descuento){
		this.descuento = descuento;
	}

	@Override
	public boolean isDescuentoMenosIngreso(){
		return false;
	}

	@Override
	public boolean isDescuentoMenosMargen(){
		return false;
	}

}
