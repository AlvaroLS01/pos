package com.comerzzia.dinosol.pos.services.promociones.tipos.especificos;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionDescuentoLineaBean;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

@Component
@Scope("prototype")
@Primary
public class DinoPromocionDescuentoLineaBean extends PromocionDescuentoLineaBean {

	@Override
	public void aplicarPromocion(DocumentoPromocionable documento, LineasAplicablesPromoBean lineasCondicion, LineasAplicablesPromoBean lineasAplicables) {
		// Tenemos en cuenta la cantidad de artículos a los que podemos dar descuento
		BigDecimal cantidadDescuento = cantidad;
		if (BigDecimalUtil.isIgualACero(cantidadDescuento)) { // Se aplica a todos los artículos
			cantidadDescuento = lineasAplicables.getCantidadArticulos();
		}
		
//		cantidadDescuento = cantidadDescuento.subtract(getAppliedQuantity(lineasAplicables));
//        if(BigDecimalUtil.isMenorOrIgualACero(cantidadDescuento)) {
//        	return;
//        }

		Map<Integer, String> tarifasOriginales = getTarifasOriginalesLinea(lineasAplicables);
		cambiarTarifaLineasAplicables(lineasAplicables, promocionBean.getCodtarPrecios());

		for (int i = 1; i <= lineasCondicion.getNumCombos(); i++) {
			// Instanciamos la promoción del documento (cada combo tendrá una)
			PromocionTicket promocionTicket = createPromocionTicket(codigoCupon);

			// Intentamos aplicar la promoción sobre las líneas aplicables
			BigDecimal importeTotalAhorro = lineasAplicables.aplicaDescuentoCandidato(promocionTicket, precioPorcentaje, null, isTipoDtoPorcentaje(), isTipoDtoImporte(), BigDecimal.ZERO,
			        cantidadDescuento);
			// Calculamos si la promocion mejora el precio original para acabar aplicandola
			if (importeTotalAhorro == null) {
				cambiarTarifaOriginalLineasAplicables(lineasAplicables, tarifasOriginales);
				return;
			}

			// Reseteamos promociones candidatas
			for (LineaDocumentoPromocionable linea : documento.getLineasDocumentoPromocionable()) {
				linea.resetPromocionesCandidatas();
			}

			importeTotalAhorro = lineasAplicables.aplicaDescuento(promocionTicket, precioPorcentaje, null, isTipoDtoPorcentaje(), isTipoDtoImporte(), BigDecimal.ZERO, cantidadDescuento);

			// Si conseguimos aplicar la promoción, añadimos el combo aplicado a las promociones del documento
			promocionTicket.setImporteTotalAhorro(importeTotalAhorro);
			documento.addPromocion(promocionTicket);
		}
	}
	
	protected BigDecimal getAppliedQuantity(LineasAplicablesPromoBean lineasAplicables) {
		BigDecimal appliedQuantity = BigDecimal.ZERO;
		for (LineaDocumentoPromocionable line : lineasAplicables.getLineasAplicables()) {
			PromocionLineaTicket promo = line.getPromocion(getIdPromocion());
			if (promo != null) {
				appliedQuantity = appliedQuantity.add(promo.getCantidadPromocion());
			}
		}

		return appliedQuantity;
	}


}
