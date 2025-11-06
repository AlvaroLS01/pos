package com.comerzzia.dinosol.pos.services.promociones.tipos.especificos;

import java.math.BigDecimal;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionPrecioDetalles;
import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.DetallePromocionPrecio;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

@Component
@Scope("prototype")
@Primary
public class DinoPromocionPrecioDetalles extends PromocionPrecioDetalles {

	@Override
	public void aplicaPromocionPrecioLinea(DocumentoPromocionable documento, LineaDocumentoPromocionable linea) {
		// Obtenemos detalle aplicable para este artículo (sólo puede haber uno en cada promoción unitaria)
		DetallePromocionPrecio detalle = getDetallePromocionPrecio(linea.getCodArticulo(), linea.getDesglose1(), linea.getDesglose2());
		if (detalle == null) {
			return;
		}

		BigDecimal precioDetalle = linea.isPrecioIncluyeImpuestos() ? detalle.getPrecioTotal() : detalle.getPrecioVenta();
		BigDecimal ahorro = linea.getPrecioAplicacionPromocion().subtract(precioDetalle);
		ahorro = ahorro.multiply(linea.getCantidad());

		// Calculamos si la promocion mejora el precio original para acabar aplicandola
		if (BigDecimalUtil.isMenor(ahorro.abs(), BigDecimal.ZERO)) {
			((LineaTicket) linea).getTarifa().setPrecioTotal(precioDetalle);
			((LineaTicket) linea).recalcularPreciosImportes();
			ahorro = BigDecimal.ZERO;
		}

		setTextoPromocion(detalle.getTextoPromocion());

		PromocionTicket promocionTicket = documento.getPromocion(getIdPromocion());
		if (promocionTicket == null) {
			promocionTicket = createPromocionTicket(codigoCupon);
			documento.addPromocion(promocionTicket);
		}
		PromocionLineaTicket promocionLinea = linea.getPromocion(promocionTicket.getIdPromocion());
		if (promocionLinea == null) {
			promocionLinea = new PromocionLineaTicket(promocionTicket);
			linea.addPromocion(promocionLinea);
		}

		promocionLinea.addImporteTotalDto(ahorro);
		promocionLinea.setCantidadPromocion(linea.getCantidad());
		if(!promocionBean.getTipoDto().equals(TIPO_DTO_A_FUTURO)) {
			linea.setPrecioPromocionSinDto(detalle.getPrecioVenta());
			linea.setPrecioPromocionTotalSinDto(detalle.getPrecioTotal());
			linea.recalcularImporteFinal();
		}
		promocionTicket.setImporteTotalAhorro(BigDecimalUtil.redondear(ahorro).add(promocionTicket.getImporteTotalAhorro()));
	}

}
