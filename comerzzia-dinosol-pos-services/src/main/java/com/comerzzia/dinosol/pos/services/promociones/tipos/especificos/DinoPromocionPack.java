package com.comerzzia.dinosol.pos.services.promociones.tipos.especificos;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.services.promociones.filtros.DinoFiltroLineasPackPromocion;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.FiltroLineasPackPromocion;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionPackBean;
import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.pack.AgrupacionPack;
import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.pack.CombinacionAgrupacionPack;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.math.Combinaciones;

@Component
@Scope("prototype")
@Primary
public class DinoPromocionPack extends PromocionPackBean {

	@Override
	protected List<CombinacionAgrupacionPack> obtenerMejorCombinacion(Set<List<CombinacionAgrupacionPack>> combinaciones, boolean calcularPromocion) {
		List<CombinacionAgrupacionPack> mejorCombinacion = null;
		BigDecimal peorDescuento = BigDecimal.ZERO;

		for (List<CombinacionAgrupacionPack> combinacionesAgrupacion : combinaciones) {
			if(!esAplicable(combinacionesAgrupacion, calcularPromocion)) {
				continue;
			}
			BigDecimal descuento = getDescuentoCombinacion(combinacionesAgrupacion);
			if(BigDecimalUtil.isIgualACero(peorDescuento) || BigDecimalUtil.isMenor(descuento, peorDescuento)) {
				mejorCombinacion = combinacionesAgrupacion;
				peorDescuento = descuento;
			}
			
		}
		return mejorCombinacion;
	}
	
	@Override
	protected FiltroLineasPackPromocion createFiltroLineasPromocionPack(DocumentoPromocionable documento) {
		FiltroLineasPackPromocion filtro = new DinoFiltroLineasPackPromocion();
		filtro.setDocumento(documento);
		filtro.setFiltrarPromoExclusivas(false);
		return filtro;
	}
	
	@Override
	public void aplicarPromocion(DocumentoPromocionable documento, LineasAplicablesPromoBean lineasCondicion, LineasAplicablesPromoBean lineasAplicables) {
		log.trace("aplicarPromocion() - " + this);

		List<AgrupacionPack> agrupacionesPack = getAgrupacionesAplicables(documento);

		if (agrupacionesPack.isEmpty() || agrupacionesPack.size() < getAplicacion().getGrupos().size()) {
			return;
		}

		int numCombos = getNumCombos(agrupacionesPack);

		List<List<CombinacionAgrupacionPack>> combinacionesAgrupaciones = getCombinacionesAgrupaciones(agrupacionesPack);
		Set<List<CombinacionAgrupacionPack>> combinaciones = Combinaciones.getCombinaciones(combinacionesAgrupaciones);

		eliminarCombinacionesConRepeticion(combinaciones);

		BigDecimal importeTotalAhorro = BigDecimal.ZERO;

		for (int i = 0; i < numCombos; i++) {
			List<CombinacionAgrupacionPack> mejorCombinacion = obtenerMejorCombinacion(combinaciones, false);

			if(mejorCombinacion != null) {
				BigDecimal descuento = getDescuentoCombinacion(mejorCombinacion);
				importeTotalAhorro = importeTotalAhorro.add(descuento);
				PromocionTicket promocionTicket = getPromocionTicket(documento);
	
				aplicarPromocion(mejorCombinacion, promocionTicket);
			}
		}

//		if(BigDecimalUtil.isMayorACero(importeTotalAhorro)) {
			PromocionTicket promocionTicket = getPromocionTicket(documento);
			promocionTicket.setImporteTotalAhorro(importeTotalAhorro);
			documento.addPromocion(promocionTicket);
//		}
	}

}
