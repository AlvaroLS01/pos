package com.comerzzia.iskaypet.pos.services.promociones.tipos.especificos;

import java.math.BigDecimal;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.DetallePromocionDescuento;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

@Component
@Primary
@Scope("prototype")
public class IskaypetPromocionDescuentoDetalles extends com.comerzzia.pos.services.promociones.tipos.especificos.PromocionDescuentoDetalles {
	
	// Sobreescribimos para setear el texto de promoción
	@Override
	public void aplicarPromocion(DocumentoPromocionable documento, LineasAplicablesPromoBean lineasCondicion, LineasAplicablesPromoBean lineasAplicables) {
		PromocionTicket promocionTicket = documento.getPromocion(getIdPromocion());
		if (promocionTicket == null) {
			promocionTicket = createPromocionTicket(customerCoupon);
		}

		BigDecimal cantidadAplicada = BigDecimal.ZERO;
		BigDecimal importeTotalAhorro = BigDecimal.ZERO;
		for (int i = 0; i < lineasAplicables.getLineasAplicables().size();) {
			LineaDocumentoPromocionable linea = lineasAplicables.getLineasAplicables().get(i);
			// Obtenemos detalle aplicable para este artículo (sólo puede haber uno en cada promoción unitaria)
			DetallePromocionDescuento detalle = getDetallePromocionDescuento(linea.getCodArticulo(), linea.getDesglose1(), linea.getDesglose2());
			setTextoPromocion(detalle.getTextoPromocion());

			// Comprobamos si hay cantidad de artículos suficientes
			if (BigDecimalUtil.isMenorOrIgualACero(lineasAplicables.getCantidadArticulos().subtract(cantidadAplicada))) {
				linea.recalcularImporteFinal();
				break;
			}

			// Comprobamos si la promoción puede seguir aplicándose en esta línea según la cantidad
			if (BigDecimalUtil.isMayorOrIgual(linea.getCantidadPromocion(), linea.getCantidad())) {
				linea.recalcularImporteFinal();
				i++;
				continue; // nueva iteración del bucle
			}

			BigDecimal aumento = BigDecimal.ONE;
			BigDecimal cantidadRestante = linea.getCantidad().subtract(linea.getCantidadPromocion());
			if (BigDecimalUtil.isMenor(cantidadRestante, BigDecimal.ONE)) {
				aumento = cantidadRestante;
			}

			BigDecimal ahorro = BigDecimal.ZERO;
			if(detalle.isTipoDtoPorcentaje()) {
				ahorro = BigDecimalUtil.porcentaje(linea.getPrecioAplicacionPromocion(), detalle.getDescuento());
			}else if(detalle.isTipoDtoImporte()) {
				ahorro = detalle.getDescuento();
			}
			ahorro = ahorro.multiply(aumento);
			importeTotalAhorro = importeTotalAhorro.add(BigDecimalUtil.redondear(ahorro));

			PromocionLineaTicket promocionLinea = linea.getPromocion(promocionTicket.getIdPromocion());
			if (promocionLinea == null) {
				promocionLinea = new PromocionLineaTicket(promocionTicket);
				linea.addPromocion(promocionLinea);
			}

			promocionLinea.addImporteTotalDto(ahorro);

			promocionLinea.addCantidadPromocion(aumento);
			linea.addCantidadPromocion(aumento);
			cantidadAplicada = cantidadAplicada.add(aumento);
		}

		if (BigDecimalUtil.isMayorACero(cantidadAplicada)) {
			promocionTicket.setImporteTotalAhorro(importeTotalAhorro);
			documento.addPromocion(promocionTicket);
		}
	}

}
