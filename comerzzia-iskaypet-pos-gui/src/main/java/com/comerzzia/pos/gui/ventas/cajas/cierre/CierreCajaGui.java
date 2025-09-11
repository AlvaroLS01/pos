/**
 * ComerZZia 3.0
 *
 * Copyright (c) 2008-2015 Comerzzia, S.L.  All Rights Reserved.
 *
 * THIS WORK IS  SUBJECT  TO  SPAIN  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 * TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 * EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 *
 * CONSULT THE END USER LICENSE AGREEMENT FOR INFORMATION ON ADDITIONAL
 * RESTRICTIONS.
 */


package com.comerzzia.pos.gui.ventas.cajas.cierre;

import java.math.BigDecimal;

import com.comerzzia.pos.persistence.cajas.acumulados.CajaLineaAcumuladoBean;
import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoBean;
import com.comerzzia.pos.persistence.cajas.recuentos.CajaLineaRecuentoBean;


public class CierreCajaGui {
     protected CajaLineaAcumuladoBean cajaLineaAcumulado;

    public CierreCajaGui(CajaLineaAcumuladoBean cajaLineaAcumulado) {
        this.cajaLineaAcumulado = cajaLineaAcumulado;
    }

    public void addMovimiento(CajaMovimientoBean movimiento) {
        cajaLineaAcumulado.addMovimiento(movimiento);
    }

    public void addLineaRecuento(CajaLineaRecuentoBean lineaRecuento) {
        cajaLineaAcumulado.addLineaRecuento(lineaRecuento);
    }

    public String getMedioPago() {
        return  (cajaLineaAcumulado!=null ? cajaLineaAcumulado.getMedioPago().getDesMedioPago() : null);
    }

    public BigDecimal getEntrada() {
        return (cajaLineaAcumulado!=null ? cajaLineaAcumulado.getEntrada() : null);
    }

    public BigDecimal getSalida() {
        return (cajaLineaAcumulado!=null ? cajaLineaAcumulado.getSalida() : null);
    }

    public BigDecimal getRecuento() {
        return (cajaLineaAcumulado!=null ? cajaLineaAcumulado.getRecuento() : null);
    }

    public BigDecimal getTotal() {
        return (cajaLineaAcumulado!=null ? cajaLineaAcumulado.getTotal() : null);
    }

    public BigDecimal getDescuadre() {
        return (cajaLineaAcumulado!=null ? cajaLineaAcumulado.getDescuadre() : null);
    }

     
     
}
