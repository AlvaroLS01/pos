package com.comerzzia.bimbaylola.pos.services.promociones.tipos;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionDescuentoLineaBean;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

@Component
@Scope("prototype")
@Primary
public class ByLPromocionDescuentoLineaBean extends PromocionDescuentoLineaBean {
	
	@Override
	public BigDecimal calcularPromocion(LineasAplicablesPromoBean lineasCondicion, LineasAplicablesPromoBean lineasAplicables) {
		// Tenemos en cuenta la cantidad de artículos a los que podemos dar descuento
		BigDecimal cantidadDescuento = cantidad;
		if (BigDecimalUtil.isIgualACero(cantidadDescuento)) { // Se aplica a todos los artículos
			cantidadDescuento = lineasAplicables.getCantidadArticulos();
		}

		Map<Integer, String> tarifasOriginales = getTarifasOriginalesLinea(lineasAplicables);

		BigDecimal totalTarifaOrigen = lineasAplicables.getImporteTotalLineasSinPromociones();
		cambiarTarifaLineasAplicables(lineasAplicables, promocionBean.getCodtarPrecios());

		BigDecimal importeTotalAhorro = BigDecimal.ZERO;
		for (int i = 1; i <= lineasCondicion.getNumCombos(); i++) {
			// Instanciamos la promoción del documento (cada combo tendrá una)
			PromocionTicket promocionTicket = createPromocionTicket(codigoCupon);

			// Intentamos aplicar la promoción sobre las líneas aplicables
			BigDecimal importeTotalAhorroCombo = lineasAplicables.aplicaDescuentoCandidato(promocionTicket, precioPorcentaje, null, isTipoDtoPorcentaje(), isTipoDtoImporte(), BigDecimal.ZERO,
			        cantidadDescuento);
			if (importeTotalAhorroCombo == null) {
				cambiarTarifaOriginalLineasAplicables(lineasAplicables, tarifasOriginales);
				return importeTotalAhorro;
			}
			
			BigDecimal totalTarifaPromocion = BigDecimal.ZERO;
			for(LineaDocumentoPromocionable linea : lineasAplicables.getLineasAplicables()) {
				totalTarifaPromocion = totalTarifaPromocion.add(linea.getImporteAplicacionPromocionConDto());
			}
			
			// Si conseguimos aplicar la promoción, añadimos el combo aplicado al total
			importeTotalAhorro = importeTotalAhorro.add(importeTotalAhorroCombo).add(totalTarifaOrigen).subtract(totalTarifaPromocion);
		}

		cambiarTarifaOriginalLineasAplicables(lineasAplicables, tarifasOriginales);

		return importeTotalAhorro;
	}

	@Override
	public void aplicarPromocion(DocumentoPromocionable documento, LineasAplicablesPromoBean lineasCondicion, LineasAplicablesPromoBean lineasAplicables) {
		// Tenemos en cuenta la cantidad de artículos a los que podemos dar descuento
		BigDecimal cantidadDescuento = cantidad;
		if (BigDecimalUtil.isIgualACero(cantidadDescuento)) { // Se aplica a todos los artículos
			cantidadDescuento = lineasAplicables.getCantidadArticulos();
		}

		Map<Integer, String> tarifasOriginales = getTarifasOriginalesLinea(lineasAplicables);
		BigDecimal totalTarifaOrigen = lineasAplicables.getImporteTotalLineasSinPromociones();
		cambiarTarifaLineasAplicables(lineasAplicables, promocionBean.getCodtarPrecios());

		for (int i = 1; i <= lineasCondicion.getNumCombos(); i++) {
			// Instanciamos la promoción del documento (cada combo tendrá una)
			PromocionTicket promocionTicket = createPromocionTicket(codigoCupon);

			// Intentamos aplicar la promoción sobre las líneas aplicables
			BigDecimal importeTotalAhorro = lineasAplicables.aplicaDescuentoCandidato(promocionTicket, precioPorcentaje, null, isTipoDtoPorcentaje(), isTipoDtoImporte(), BigDecimal.ZERO,
			        cantidadDescuento);
			// Calculamos si la promocion mejora el precio original para acabar aplicandola
			if (importeTotalAhorro == null || BigDecimalUtil.isMayorOrIgual(lineasAplicables.getImporteTotalLineasSinPromociones().subtract(importeTotalAhorro), totalTarifaOrigen)) {
				cambiarTarifaOriginalLineasAplicables(lineasAplicables, tarifasOriginales);
				return;
			}

			// Reseteamos promociones candidatas
			for (LineaDocumentoPromocionable linea : documento.getLineasDocumentoPromocionable()) {
				linea.resetPromocionesCandidatas();
			}
			cambiarTarifaLineasAplicables(lineasAplicables, promocionBean.getCodtarPrecios());

			importeTotalAhorro = lineasAplicables.aplicaDescuento(promocionTicket, precioPorcentaje, null, isTipoDtoPorcentaje(), isTipoDtoImporte(), BigDecimal.ZERO, cantidadDescuento);

			// Si conseguimos aplicar la promoción, añadimos el combo aplicado a las promociones del documento
			promocionTicket.setImporteTotalAhorro(importeTotalAhorro);
			documento.addPromocion(promocionTicket);
		}
	}

}
