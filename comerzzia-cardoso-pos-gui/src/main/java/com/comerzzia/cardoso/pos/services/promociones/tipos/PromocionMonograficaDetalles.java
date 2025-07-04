package com.comerzzia.cardoso.pos.services.promociones.tipos;

import java.math.BigDecimal;

import com.comerzzia.cardoso.pos.persistence.promociones.monograficas.MonograficaPromocionLineaTicket;
import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.pos.persistence.promociones.PromocionBean;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionPrecioDetalles;
import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.DetallePromocionPrecio;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

@SuppressWarnings("rawtypes")
public class PromocionMonograficaDetalles extends PromocionPrecioDetalles {

	private BigDecimal descuento;

//	public PromocionMonograficaDetalles(PromocionBean promocion) {
//		super(promocion);
//	}
//
//	@Override
//	public void aplicarPromocion(TicketVenta ticket, LineasAplicablesPromoBean lineasCondicion, LineasAplicablesPromoBean lineasAplicables) {
//		PromocionTicket promocionTicket = ticket.getPromocion(getIdPromocion());
//		if (promocionTicket == null) {
//			promocionTicket = createPromocionTicket(customerCoupon);
//		}
//
//		int cantidadAplicada = 0;
//		BigDecimal importeTotalAhorro = BigDecimal.ZERO;
//		for (int i = 0; i < lineasAplicables.getLineasAplicables().size();) {
//			LineaTicketAbstract linea = lineasAplicables.getLineasAplicables().get(i);
//			// Obtenemos detalle aplicable para este artículo (sólo puede haber uno en cada promoción unitaria)
//			DetallePromocionPrecio detalle = detalles.get(linea.getCodArticulo());
//			setTextoPromocion(detalle.getTextoPromocion());
//
//			// Comprobamos si la promoción puede seguir aplicándose en esta línea según la cantidad
//			if (BigDecimalUtil.isMayorOrIgual(linea.getCantidadPromocion(), linea.getCantidad())) {
//				i++;
//				continue; // nueva iteración del bucle
//			}
//
//			BigDecimal aumento = BigDecimal.ONE;
//			BigDecimal cantidadRestante = linea.getCantidad().subtract(linea.getCantidadPromocion());
//			if (BigDecimalUtil.isMenor(cantidadRestante, BigDecimal.ONE)) {
//				aumento = cantidadRestante;
//			}
//
//			BigDecimal ahorro = BigDecimal.ZERO;
//			BigDecimal precioDetalle = linea.isPrecioIncluyeImpuestos()? detalle.getPrecioTotal() : detalle.getPrecioVenta();
//			ahorro = linea.getPrecioTotalTarifaOrigen().multiply(aumento).subtract(precioDetalle.multiply(aumento));
//			importeTotalAhorro = importeTotalAhorro.add(ahorro);
//
//			MonograficaPromocionLineaTicket promocionLinea = (MonograficaPromocionLineaTicket) linea.getPromocion(promocionTicket.getIdPromocion());
//			if (promocionLinea == null) {
//				promocionLinea = new MonograficaPromocionLineaTicket(promocionTicket);
//				linea.addPromocion(promocionLinea);
//			}
//			
//			modificarDescuentoPromocion(linea, promocionLinea, detalle);
//
//			promocionLinea.addImporteTotalDto(ahorro);
//			promocionLinea.addCantidadPromocion(aumento);
//			linea.addCantidadPromocion(aumento);
//			linea.recalcularImporteFinal();
//			cantidadAplicada++;
//		}
//
//		if (cantidadAplicada > 0) {
//			promocionTicket.setImporteTotalAhorro(importeTotalAhorro);
//			ticket.addPromocion(promocionTicket);
//		}
//	}
//
//	private void modificarDescuentoPromocion(LineaTicketAbstract linea, MonograficaPromocionLineaTicket promocionLinea, DetallePromocionPrecio detalle) {
//		try {
//	        XMLDocument xml = new XMLDocument(detalle.getDetalle().getDatosPromocion());
//	        BigDecimal descuento = xml.getNodo("Descuento").getValueAsBigDecimal();
//	        promocionLinea.setDescuento(descuento);
//	        if(linea instanceof ICardosoLinea) {
//	        	((ICardosoLinea) linea).setPorcentajeDtoMonografica(descuento);
//	        }
//        }
//        catch (Exception e) {
//        	log.error("modificarDescuentoPromocion() - Ha habido un error al buscar el descuento de la promocion: " + e.getMessage(), e);
//        }
//    }
//
//	public BigDecimal getDescuento() {
//		return descuento;
//	}

}
