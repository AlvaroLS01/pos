package com.comerzzia.bimbaylola.pos.services.cajas;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.comerzzia.bimbaylola.pos.persistence.cajas.recuentos.RecuentoBean;

/**
 * Agrupa las líneas de efectivo (codMedioPago "0000") de un recuento antes de guardarlo
 */
public final class ByLRecuentoEfectivoAgrupador {

	private static final String COD_MEDIO_PAGO_EFECTIVO = "0000";

	private ByLRecuentoEfectivoAgrupador() {

	}

	public static List<RecuentoBean> agruparLineas(List<RecuentoBean> recuentos) {
		if (recuentos == null || recuentos.isEmpty()) {
			return recuentos == null ? Collections.emptyList() : recuentos;
		}

		List<RecuentoBean> resultado = new ArrayList<>();

		BigDecimal totalEfectivo = BigDecimal.ZERO;
		BigDecimal totalEfectivoAbono = BigDecimal.ZERO;
		boolean tieneImporteAbono = false;
		RecuentoBean primeraLineaEfectivo = null;
		int posicionLineaEfectivo = -1;

		for (RecuentoBean recuento : recuentos) {
			if (COD_MEDIO_PAGO_EFECTIVO.equals(recuento.getCodMedioPago())) {
				if (primeraLineaEfectivo == null) {
					primeraLineaEfectivo = recuento;
					posicionLineaEfectivo = resultado.size();
				}

				BigDecimal cantidad = recuento.getCantidad() != null ? BigDecimal.valueOf(recuento.getCantidad().longValue()) : BigDecimal.ZERO;
				BigDecimal valor = recuento.getValor() != null ? recuento.getValor() : BigDecimal.ZERO;
				totalEfectivo = totalEfectivo.add(valor.multiply(cantidad));

				BigDecimal valorAbono = recuento.getValorAbono();
				if (valorAbono != null) {
					totalEfectivoAbono = totalEfectivoAbono.add(valorAbono.multiply(cantidad));
					tieneImporteAbono = true;
				}
			}
			else {
				resultado.add(recuento);
			}
		}

		if (primeraLineaEfectivo != null) {
			RecuentoBean lineaEfectivoAgrupada = new RecuentoBean();
			lineaEfectivoAgrupada.setUidActividad(primeraLineaEfectivo.getUidActividad());
			lineaEfectivoAgrupada.setCodMedioPago(COD_MEDIO_PAGO_EFECTIVO);
			lineaEfectivoAgrupada.setCantidad(Integer.valueOf(1));
			lineaEfectivoAgrupada.setValor(totalEfectivo.setScale(2, RoundingMode.HALF_UP));
			if (tieneImporteAbono) {
				lineaEfectivoAgrupada.setValorAbono(totalEfectivoAbono.setScale(2, RoundingMode.HALF_UP));
			}
			lineaEfectivoAgrupada.setIdD365(primeraLineaEfectivo.getIdD365());
			lineaEfectivoAgrupada.setIdD365Abono(primeraLineaEfectivo.getIdD365Abono());

			resultado.add(posicionLineaEfectivo, lineaEfectivoAgrupada);
		}

		for (int i = 0; i < resultado.size(); i++) {
			resultado.get(i).setLinea(String.valueOf(i));
		}

		return resultado;
	}
}