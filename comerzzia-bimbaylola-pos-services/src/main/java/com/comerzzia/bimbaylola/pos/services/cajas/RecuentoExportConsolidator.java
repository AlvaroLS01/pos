package com.comerzzia.bimbaylola.pos.services.cajas;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.comerzzia.bimbaylola.pos.persistence.cajas.recuentos.RecuentoBean;

/**
 * Utility class that consolidates the cash lines (codMedioPago "0000") of a
 * recount before exporting it.
 */
public final class RecuentoExportConsolidator {

    private static final String COD_MEDIO_PAGO_EFECTIVO = "0000";

    private RecuentoExportConsolidator() {
        // Utility class
    }

    public static List<RecuentoBean> consolidate(List<RecuentoBean> recuentos) {
        if (recuentos == null || recuentos.isEmpty()) {
            return recuentos == null ? Collections.emptyList() : recuentos;
        }

        List<RecuentoBean> resultado = new ArrayList<>();

        BigDecimal totalEfectivo = BigDecimal.ZERO;
        BigDecimal totalEfectivoAbono = BigDecimal.ZERO;
        boolean totalEfectivoAbonoInicializado = false;
        RecuentoBean plantillaEfectivo = null;
        int indiceInsercionEfectivo = -1;

        for (RecuentoBean recuento : recuentos) {
            if (COD_MEDIO_PAGO_EFECTIVO.equals(recuento.getCodMedioPago())) {
                if (plantillaEfectivo == null) {
                    plantillaEfectivo = recuento;
                    indiceInsercionEfectivo = resultado.size();
                }

                BigDecimal cantidad = recuento.getCantidad() != null
                        ? BigDecimal.valueOf(recuento.getCantidad().longValue())
                        : BigDecimal.ZERO;
                BigDecimal valor = recuento.getValor() != null ? recuento.getValor() : BigDecimal.ZERO;
                totalEfectivo = totalEfectivo.add(valor.multiply(cantidad));

                BigDecimal valorAbono = recuento.getValorAbono();
                if (valorAbono != null) {
                    totalEfectivoAbono = totalEfectivoAbono.add(valorAbono.multiply(cantidad));
                    totalEfectivoAbonoInicializado = true;
                }
            } else {
                resultado.add(recuento);
            }
        }

        if (plantillaEfectivo != null) {
            RecuentoBean efectivoConsolidado = new RecuentoBean();
            // We reuse the uidActividad from the first cash line to keep the same
            // activity identifier expected by the downstream contracts.
            efectivoConsolidado.setUidActividad(plantillaEfectivo.getUidActividad());
            efectivoConsolidado.setCodMedioPago(COD_MEDIO_PAGO_EFECTIVO);
            efectivoConsolidado.setCantidad(Integer.valueOf(1));
            efectivoConsolidado.setValor(totalEfectivo.setScale(2, RoundingMode.HALF_UP));
            if (totalEfectivoAbonoInicializado) {
                efectivoConsolidado.setValorAbono(totalEfectivoAbono.setScale(2, RoundingMode.HALF_UP));
            }
            efectivoConsolidado.setIdD365(plantillaEfectivo.getIdD365());
            efectivoConsolidado.setIdD365Abono(plantillaEfectivo.getIdD365Abono());

            resultado.add(indiceInsercionEfectivo, efectivoConsolidado);
        }

        for (int i = 0; i < resultado.size(); i++) {
            resultado.get(i).setLinea(String.valueOf(i));
        }

        return resultado;
    }
}
