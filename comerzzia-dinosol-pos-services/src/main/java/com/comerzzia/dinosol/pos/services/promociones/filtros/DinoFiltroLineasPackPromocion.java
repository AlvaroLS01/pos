package com.comerzzia.dinosol.pos.services.promociones.filtros;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.FiltroLineasPackPromocion;
import com.comerzzia.pos.services.promociones.tipos.componente.GrupoComponentePromoBean;
import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.pack.AgrupacionPack;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

public class DinoFiltroLineasPackPromocion extends FiltroLineasPackPromocion {

	@Override
	protected AgrupacionPack procesarGrupo(GrupoComponentePromoBean grupo) {
		List<LineaDocumentoPromocionable> lineasAplicablesRegla = getLineasAplicablesGrupo(grupo, documento.getLineasDocumentoPromocionable());
		if (lineasAplicablesRegla.isEmpty()) {
			return null; // No continuamos procesando, porque hay una condición del pack que no se cumple
		}

		List<LineaDocumentoPromocionable> lineasRealesAplicablesRegla = eliminarLineasNegadas(lineasAplicablesRegla);

		AgrupacionPack agrupacionPack = crearAgrupacion(lineasRealesAplicablesRegla, null, null);

		return agrupacionPack;
	}

	private List<LineaDocumentoPromocionable> eliminarLineasNegadas(List<LineaDocumentoPromocionable> lineasAplicablesRegla) {
		List<LineaDocumentoPromocionable> lineasRealesAplicablesRegla = new ArrayList<LineaDocumentoPromocionable>();
		for (LineaDocumentoPromocionable linea : lineasAplicablesRegla) {
			Integer idLinea = linea.getIdLinea();
			String codArticulo = linea.getCodArticulo();
			BigDecimal precioTotalConDto = ((LineaTicket) linea).getPrecioTotalConDto();

			BigDecimal cantidad = linea.getCantidad();

			for (LineaDocumentoPromocionable lineaAux : lineasAplicablesRegla) {
				if (!idLinea.equals(lineaAux.getIdLinea()) && codArticulo.equals(lineaAux.getCodArticulo()) && precioTotalConDto.equals(((LineaTicket) lineaAux).getPrecioTotalConDto())) {
					cantidad = cantidad.add(lineaAux.getCantidad());
				}
			}

			if (BigDecimalUtil.isMayorACero(cantidad)) {
				lineasRealesAplicablesRegla.add(linea);
			}
		}
		return lineasRealesAplicablesRegla;
	}

}
