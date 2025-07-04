package com.comerzzia.cardoso.pos.services.promociones.tipos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.core.util.xml.XMLDocumentException;
import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.pos.persistence.promociones.detalle.PromocionDetalleBean;
import com.comerzzia.pos.persistence.promociones.detalle.PromocionDetalleKey;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.promociones.tipos.PromocionLineaDetalles;
import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.DetallePromocion;
import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.DetallePromocionNxM;
import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.LineaDetallePromocionNxM;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaCandidataTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionNxMDetalles;

@Component
@Scope("prototype")
public class CardosoPromocionNxMDetalles extends PromocionNxMDetalles {
	@Override
    public BigDecimal calcularPromocion(LineasAplicablesPromoBean lineasCondicion, LineasAplicablesPromoBean lineasAplicables) {
        // Obtenemos a qué agrupación pertenece los codArticulos de este LineasAplicables
        String codArticulo = lineasAplicables.getLineasAplicables().get(0).getArticulo().getCodArticulo();
        PromocionDetalleKey key = new PromocionDetalleKey();
        key.setCodArticulo(codArticulo);
        key.setDesglose1(lineasAplicables.getLineasAplicables().get(0).getDesglose1());
        key.setDesglose2(lineasAplicables.getLineasAplicables().get(0).getDesglose2());
        DetallePromocionNxM detalleNxM = (DetallePromocionNxM) getDetalle(detallesArticulos, key);
        if(detalleNxM != null){
            setTextoPromocion(detalleNxM.getTextoPromocion());
            // Calculamos el número de combos que podremos aplicar
            Integer cantidadN = detalleNxM.getCantidadN();
            Integer cantidadM = detalleNxM.getCantidadM();
            BigDecimal descuento = detalleNxM.getDescuento();
            boolean isPorcentaje = detalleNxM.isTipoDtoPorcentaje();
            Integer numCombos = lineasAplicables.getCantidadArticulos().divide(new BigDecimal(cantidadN), 1, RoundingMode.DOWN).intValue();
            Integer cantConDescuento = cantidadN - cantidadM;
            Integer cantSinDescuento = cantidadM;
            if(cantConDescuento == 0) {
                cantConDescuento = 1;
                cantSinDescuento = 0;
            }
            BigDecimal importeTotalAhorro = BigDecimal.ZERO;
            for (int i = 1; i <= numCombos; i++) {
                // Instanciamos la promoción del documento (cada combo tendrá una)
                PromocionTicket promocionTicket = createPromocionTicket(customerCoupon);
                // Intentamos aplicar la promoción sobre las líneas aplicables
                BigDecimal importeTotalAhorroCombo = lineasAplicables.aplicaDescuentoCandidato(promocionTicket, descuento, null, isPorcentaje, !isPorcentaje, new BigDecimal(cantSinDescuento), new BigDecimal(cantConDescuento));
                if (importeTotalAhorroCombo == null) {
                    return importeTotalAhorro;
                }
                // Si conseguimos aplicar la promoción, añadimos el combo aplicado al total
                importeTotalAhorro = importeTotalAhorro.add(importeTotalAhorroCombo);
            }
            return importeTotalAhorro;
        }else{
            return BigDecimal.ZERO;
        }
    }   
    @Override
    public void aplicarPromocion(DocumentoPromocionable documento, LineasAplicablesPromoBean lineasCondicion, LineasAplicablesPromoBean lineasAplicables) {
        //Obtenemos a qué agrupación pertenece los codArticulos de este LineasAplicables
        String codArticulo = lineasAplicables.getLineasAplicables().get(0).getArticulo().getCodArticulo();
        PromocionDetalleKey key = new PromocionDetalleKey();
        key.setCodArticulo(codArticulo);
        key.setDesglose1(lineasAplicables.getLineasAplicables().get(0).getDesglose1());
        key.setDesglose2(lineasAplicables.getLineasAplicables().get(0).getDesglose2());
        DetallePromocionNxM detalleNxM = (DetallePromocionNxM) getDetalle(detallesArticulos, key);
        if(detalleNxM != null){
            setTextoPromocion(detalleNxM.getTextoPromocion());
            // Calculamos el número de combos que podremos aplicar
            Integer cantidadN = detalleNxM.getCantidadN();
            Integer cantidadM = detalleNxM.getCantidadM();
            BigDecimal descuento = detalleNxM.getDescuento();
            boolean isPorcentaje = detalleNxM.isTipoDtoPorcentaje();
            Integer numCombos = lineasAplicables.getCantidadArticulos().divide(new BigDecimal(cantidadN), 1, RoundingMode.DOWN).intValue();
            Integer cantConDescuento = cantidadN - cantidadM;
            Integer cantSinDescuento = cantidadM;
            if(cantConDescuento == 0) {
                cantConDescuento = 1;
                cantSinDescuento = 0;
            }
            for (int i = 1; i <= numCombos; i++) {
                // Instanciamos la promoción del documento (cada combo tendrá una)
                PromocionTicket promocionTicket = createPromocionTicket(customerCoupon);
                // Intentamos aplicar la promoción sobre las líneas aplicables
                BigDecimal importeTotalAhorro = lineasAplicables.aplicaDescuento(promocionTicket, descuento, null, isPorcentaje, !isPorcentaje, new BigDecimal(cantSinDescuento), new BigDecimal(cantConDescuento));
                if (importeTotalAhorro == null){
                    return;
                }
                // Si conseguimos aplicar la promoción, añadimos el combo aplicado a las promociones del documento
                promocionTicket.setImporteTotalAhorro(importeTotalAhorro);
                documento.addPromocion(promocionTicket);
            }
        }
    }   

	@Override
	public void validar(DetallePromocionNxM detallePromocionNxM) {
		BigDecimal n = new BigDecimal(detallePromocionNxM.getCantidadN()); 
		BigDecimal m = new BigDecimal(detallePromocionNxM.getCantidadM());
		
		if (BigDecimalUtil.isMenor(n, m)) {
			throw new IllegalArgumentException("La condición N debe ser mayor que M. Actualmente, N: " + n + ", M: "+ m);
		}
		if (BigDecimalUtil.isMenorOrIgualACero(n)) {
			throw new IllegalArgumentException("La condición N debe ser distinta de cero y positiva.");
		}
		if (BigDecimalUtil.isMenorOrIgualACero(m)) {
			throw new IllegalArgumentException("La condición M debe ser distinta de cero y positiva.");
		}
	}
}
