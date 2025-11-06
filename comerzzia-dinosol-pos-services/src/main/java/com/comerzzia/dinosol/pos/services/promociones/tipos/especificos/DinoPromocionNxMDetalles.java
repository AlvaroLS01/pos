package com.comerzzia.dinosol.pos.services.promociones.tipos.especificos;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.LinkedList;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionNxMDetalles;
import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.DetallePromocionNxM;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaCandidataTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

@Component
@Primary
@Scope("prototype")
public class DinoPromocionNxMDetalles extends PromocionNxMDetalles {

	@Override
	public void analizarLineasAplicables(DocumentoPromocionable documento) {
		Calendar calendarHoy = Calendar.getInstance();
		calendarHoy.set(Calendar.HOUR_OF_DAY, 0);
		calendarHoy.set(Calendar.MINUTE, 0);
		calendarHoy.set(Calendar.SECOND, 0);
		calendarHoy.set(Calendar.MILLISECOND, 0);

		// Creamos un PromocionLineaCandidataTicket por cada Agrupación
		for (DetallePromocionNxM detalle : detalles) {
			LineasAplicablesPromoBean lineasAplicables = new LineasAplicablesPromoBean();
			lineasAplicables.setFiltroPromoExclusiva(true);
			lineasAplicables.setFiltroLineasCantidadDecimales(true);
			LinkedList<LineaDocumentoPromocionable> lineas = new LinkedList<LineaDocumentoPromocionable>();
			for (LineaDocumentoPromocionable linea : documento.getLineasDocumentoPromocionable()) {
				if (containsCodArticulo(detalle.getLineasAgrupacion(calendarHoy), linea.getArticulo().getCodArticulo(), linea.getDesglose1(), linea.getDesglose2())) {
					lineas.add(linea);
				}
			}
			lineasAplicables.setLineasAplicables(lineas);

			if (BigDecimalUtil.isMenor(lineasAplicables.getCantidadArticulos(), new BigDecimal(detalle.getCantidadN()))) {
				log.trace(this + " analizarLineasAplicables() - El detalle de promoción NxM no se puede aplicar porque las líneas no suman la cantidad N configurada: " + detalle.getCantidadN()
				        + " Aplicables: " + lineasAplicables.getCantidadArticulos());
				continue;
			}

			// Ordenamos las líneas aplicables por precio descendente
			lineasAplicables.ordenarLineasPrecioDesc();

			PromocionLineaCandidataTicket candidata = new PromocionLineaCandidataTicket(lineasAplicables, lineasAplicables, this);
			for (LineaDocumentoPromocionable linea : lineasAplicables.getLineasAplicables()) {
				linea.addPromocionAplicable(candidata);
			}
		}
	}

}
