package com.comerzzia.dinosol.pos.services.promociones.filtros;

import java.math.BigDecimal;
import java.util.List;

import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;


public class DinoLineasAplicablesEditadasManualmentePromoBean extends LineasAplicablesPromoBean {
	
	@Override
	public void setLineasAplicables(List<LineaDocumentoPromocionable> lineasAplicables) {
        reset();
        this.lineasAplicables.clear();
        for (LineaDocumentoPromocionable linea : lineasAplicables) {
            if (filtroPromoExclusiva && linea.tienePromocionLineaExclusiva()){
                continue;
            }
            if (filtroLineasCantidadDecimales && tieneCantidadDecimales(linea)) {
            	continue;
            }
            this.lineasAplicables.add(linea);
            cantidadArticulos = cantidadArticulos.add(linea.getCantidad());
            importeLineasConDto = importeLineasConDto.add(linea.getImporteAplicacionPromocionConDto());
        }
	}

	public BigDecimal aplicaDescuentoPorcentajeGeneralSobrePVP(PromocionTicket promocionTicket, BigDecimal porcentaje) {
		// Recorremos las líneas aplicables
		BigDecimal importeAcumuladoDescuento = BigDecimal.ZERO;
		for (LineaDocumentoPromocionable lineaTicket : lineasAplicables) {
			PromocionLineaTicket promocionLinea = new PromocionLineaTicket(promocionTicket);
			BigDecimal importeDescuento = null;
			
			BigDecimal precioPromocionSinDto = ((LineaTicket) lineaTicket).getPrecioPromocionSinDto();
			BigDecimal importeTotalLinea = null;
			if(precioPromocionSinDto != null) {
				importeTotalLinea = precioPromocionSinDto.multiply(lineaTicket.getCantidad());
			}
			else {
				importeTotalLinea = ((LineaTicket) lineaTicket).getImporteTotalSinDto();
			}

			importeDescuento = BigDecimalUtil.porcentaje(importeTotalLinea, porcentaje);
			importeAcumuladoDescuento = importeAcumuladoDescuento.add(BigDecimalUtil.redondear(importeDescuento));
			promocionLinea.setImporteTotalDto(importeDescuento);
			promocionLinea.setCantidadPromocion(lineaTicket.getCantidad());
			lineaTicket.addPromocion(promocionLinea);
			lineaTicket.recalcularImporteFinal();
		}

		// El descuento se aplica sobre el total (promociones de cabecera). Sobre la líneas únicamente se repercute.
		// Si el prorrateo no se ajusta al descuento total, ajustamos algún decimal en la primera línea
		if (BigDecimalUtil.isMayorACero(importeAcumuladoDescuento) && !BigDecimalUtil.isIgual(importeAcumuladoDescuento, BigDecimalUtil.redondear(promocionTicket.getImporteTotalAhorro()))) {
			BigDecimal diferencia = BigDecimalUtil.redondear(promocionTicket.getImporteTotalAhorro()).subtract(importeAcumuladoDescuento);
			for (LineaDocumentoPromocionable lineaTicket : lineasAplicables) {
				IPromocionLineaTicket promocionLinea = lineaTicket.getPromocion(promocionTicket.getIdPromocion());
				if (((LineaTicket) lineaTicket).getImporteTotalConDto().compareTo((BigDecimalUtil.redondear(diferencia))) >= 0) {
					promocionLinea.addImporteTotalDto(diferencia);
					lineaTicket.recalcularImporteFinal();
					break;
				}
			}
		}
		return importeAcumuladoDescuento;
	}
	
}
