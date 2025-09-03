package com.comerzzia.iskaypet.pos.services.promociones.tipos.especificos;

import java.math.BigDecimal;
import java.util.LinkedList;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.iskaypet.pos.services.promociones.filtros.IskaypetLineasAplicablesPromoBean;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.escalado.DetallePromocionEscalado;
import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.escalado.TramoEscalado;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaCandidataTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

/*
 * GAP97 ISK-238 - APLICACIÓN DE PROMOCIONES EN ARTÍCULOS EDITADOS
 */
@Component
@Primary
@Scope("prototype")
public class IskaypetPromocionEscaladoDetalles extends com.comerzzia.pos.services.promociones.tipos.especificos.PromocionEscaladoDetalles {
	
	@Override
	public void analizarLineasAplicables(DocumentoPromocionable documento) {
		//GAP 97 ISK-238 - Cambiamos clase estándar por personalizada
		LineasAplicablesPromoBean lineasAplicables = new IskaypetLineasAplicablesPromoBean();
		/* Inicio estándar */
		lineasAplicables.setFiltroPromoExclusiva(true);

		LinkedList<LineaDocumentoPromocionable> lineas = new LinkedList<LineaDocumentoPromocionable>();
		for (LineaDocumentoPromocionable linea : documento.getLineasDocumentoPromocionable()) {
			DetallePromocionEscalado detallePromocion = getDetallePromocionEscalado(linea.getCodArticulo(), linea.getDesglose1(), linea.getDesglose2());
			BigDecimal cantidadArticuloTicket = getCantidadArticuloTicket(documento, linea.getCodArticulo());
			
			if(detallePromocion != null) {
				if(detallePromocion.getTramos() != null) {
					for(TramoEscalado tramoEscalado : detallePromocion.getTramos()) {
						if(comprobarTramo(cantidadArticuloTicket, tramoEscalado)) {
							lineas.add(linea);
							break;
						}
					}
				}
			}
		}
		lineasAplicables.setLineasAplicables(lineas);

		PromocionLineaCandidataTicket candidata = new PromocionLineaCandidataTicket(lineasAplicables, lineasAplicables, this);
		for (LineaDocumentoPromocionable linea : lineasAplicables.getLineasAplicables()) {
			linea.addPromocionAplicable(candidata);
		}
		/* Fin estándar */
	}
	
	// Sobreescribimos para setear el texto de promoción
	@Override
	public void aplicarPromocion(DocumentoPromocionable documento, LineasAplicablesPromoBean lineasCondicion, LineasAplicablesPromoBean lineasAplicables) {
		PromocionTicket promocionTicket = documento.getPromocion(getIdPromocion());
		if (promocionTicket == null) {
			promocionTicket = createPromocionTicket(customerCoupon);
		}

		int cantidadAplicada = 0;
		BigDecimal importeTotalAhorro = BigDecimal.ZERO;
		for (int i = 0; i < lineasAplicables.getLineasAplicables().size();) {
			LineaDocumentoPromocionable linea = lineasAplicables.getLineasAplicables().get(i);

			// Obtenemos detalle aplicable para este artículo (sólo puede haber uno en cada promoción unitaria)
			DetallePromocionEscalado detallePromocion = getDetallePromocionEscalado(linea.getCodArticulo(), linea.getDesglose1(), linea.getDesglose2());
			setTextoPromocion(detallePromocion.getTextoPromocion());
			
			if(detallePromocion != null) {
				BigDecimal cantidadArticuloTicket = getCantidadArticuloTicket(lineasAplicables, linea.getCodArticulo());
				
				BigDecimal valor = null;
				for(TramoEscalado tramoEscalado : detallePromocion.getTramos()) {
					if(comprobarTramo(cantidadArticuloTicket, tramoEscalado)) {
						valor = tramoEscalado.getValor();
						break;
					}
				}
	
				// Comprobamos si la línea ya tiene una promoción y si el precio de la promoción es menor que el de la de
				// escalado
				if(detallePromocion.isTipoDtoNuevoPrecio()) {
					BigDecimal precioPromocionTotalSinDto = ((LineaTicket) linea).getPrecioPromocionTotalSinDto();
					if (precioPromocionTotalSinDto != null && BigDecimalUtil.isMenor(precioPromocionTotalSinDto, valor)) {
						i++;
						continue;
					}
				}
	
				// Comprobamos si la promoción puede seguir aplicándose en esta línea según la cantidad
				if (BigDecimalUtil.isMayorOrIgual(linea.getCantidadPromocion(), linea.getCantidad())) {
					linea.recalcularImporteFinal();
					i++;
					continue;
				}
	
				BigDecimal importeTotalAhorroLinea = calcularImporteTotalAhorroLinea(linea, detallePromocion, valor);
				importeTotalAhorro = importeTotalAhorro.add(importeTotalAhorroLinea);
	
				PromocionLineaTicket promocionLinea = linea.getPromocion(promocionTicket.getIdPromocion());
				if (promocionLinea == null) {
					promocionLinea = new PromocionLineaTicket(promocionTicket);
					linea.addPromocion(promocionLinea);
				}
	
				promocionLinea.setImporteTotalDto(importeTotalAhorroLinea);
				promocionLinea.addCantidadPromocion(linea.getCantidad());
				linea.addCantidadPromocion(linea.getCantidad());
				linea.recalcularImporteFinal();
				cantidadAplicada++;
			}
		}

		if (cantidadAplicada > 0) {
			promocionTicket.setImporteTotalAhorro(importeTotalAhorro);
			documento.addPromocion(promocionTicket);
		}
	}

}
