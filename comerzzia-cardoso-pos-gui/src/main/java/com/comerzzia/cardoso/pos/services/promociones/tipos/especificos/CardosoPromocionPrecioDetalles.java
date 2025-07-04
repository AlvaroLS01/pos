package com.comerzzia.cardoso.pos.services.promociones.tipos.especificos;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.DetallePromocionPrecio;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

/**
 *	GAP XX - INCIDENCIA DE PROBLEMAS EN NEGACIÓN DE LINEAS CON PROMOCIÓN DE PRECIO
 */
@Primary
@Component
@Scope("prototype")
public class CardosoPromocionPrecioDetalles extends com.comerzzia.pos.services.promociones.tipos.especificos.PromocionPrecioDetalles{

	@Override
	public void aplicarPromocion(DocumentoPromocionable documento, LineasAplicablesPromoBean lineasCondicion, LineasAplicablesPromoBean lineasAplicables){
		PromocionTicket promocionTicket = documento.getPromocion(getIdPromocion());
		if(promocionTicket == null){
			promocionTicket = createPromocionTicket(customerCoupon);
		}

		// GAP XX - INCIDENCIA DE PROBLEMAS EN NEGACIÓN DE LINEAS CON PROMOCIÓN DE PRECIO
		// Sacamos un mapa por codArticulo-desglose1-desglose2 y cantidad de las lineas aplicadas.
		Map<String, BigDecimal> mapArticulosLineasTicket = new HashMap<String, BigDecimal>();
		if(lineasAplicables != null && lineasAplicables.getLineasAplicables() != null && !lineasAplicables.getLineasAplicables().isEmpty()){
			for(LineaDocumentoPromocionable lineaPromocionable : lineasAplicables.getLineasAplicables()){
				String keyArticulos = lineaPromocionable.getCodArticulo() + "-" + lineaPromocionable.getDesglose1() + "-" + lineaPromocionable.getDesglose2();
				if(mapArticulosLineasTicket.containsKey(keyArticulos)){
					mapArticulosLineasTicket.put(keyArticulos, mapArticulosLineasTicket.get(keyArticulos).add(lineaPromocionable.getCantidad()));
				}
				else{
					mapArticulosLineasTicket.put(keyArticulos, lineaPromocionable.getCantidad());
				}
			}
		}
		
		BigDecimal cantidadAplicada = BigDecimal.ZERO;
		BigDecimal importeTotalAhorro = BigDecimal.ZERO;
		
		for (int i = 0; i < lineasAplicables.getLineasAplicables().size();) {
			LineaDocumentoPromocionable linea = lineasAplicables.getLineasAplicables().get(i);
			// Obtenemos detalle aplicable para este artículo (sólo puede haber uno en cada promoción unitaria)
			DetallePromocionPrecio detalle = getDetallePromocionPrecio(linea.getCodArticulo(), linea.getDesglose1(), linea.getDesglose2());
			setTextoPromocion(detalle.getTextoPromocion());
			promocionTicket.setTextoPromocion(getTextoPromocion());

			// GAP XX - INCIDENCIA DE PROBLEMAS EN NEGACIÓN DE LINEAS CON PROMOCIÓN DE PRECIO
			// Debemos seguir iterando en caso de encontrar lineas de cantidad menor o igual que cero, por las negaciones.
			String keyMapLineaAplicable = linea.getCodArticulo() + "-" + linea.getDesglose1() + "-" + linea.getDesglose2();
			if(BigDecimalUtil.isMenorOrIgualACero(mapArticulosLineasTicket.get(keyMapLineaAplicable))) {
				i++;
				continue;
			}
			//Comprobamos si hay cantidad de artículos suficientes
			//if (BigDecimalUtil.isMenorOrIgualACero(lineasAplicables.getCantidadArticulos().subtract(cantidadAplicada))) {
				//break;
			//}

			// Comprobamos si la promoción puede seguir aplicándose en esta línea según la cantidad
			if (BigDecimalUtil.isMayorOrIgual(linea.getCantidadPromocion(), linea.getCantidad())) {
				i++;
				continue;
			}
			
			BigDecimal aumento = BigDecimal.ONE;
            BigDecimal cantidadRestante = linea.getCantidad().subtract(linea.getCantidadPromocion());
            if (BigDecimalUtil.isMenor(cantidadRestante, BigDecimal.ONE)) {
            	aumento = cantidadRestante;
            }

			BigDecimal ahorro = BigDecimal.ZERO;
			BigDecimal precioDetalle = linea.isPrecioIncluyeImpuestos()? detalle.getPrecioTotal() : detalle.getPrecioVenta();
			ahorro = linea.getPrecioAplicacionPromocion().multiply(aumento).subtract(precioDetalle.multiply(aumento));
			importeTotalAhorro = importeTotalAhorro.add(BigDecimalUtil.redondear(ahorro));
			
			// Calculamos si la promocion mejora el precio original para acabar aplicandola
			if (BigDecimalUtil.isMenor(ahorro, BigDecimal.ZERO)) {
				i++;
				continue;
			}

			PromocionLineaTicket promocionLinea = linea.getPromocion(promocionTicket.getIdPromocion());
			if (promocionLinea == null) {
				promocionLinea = new PromocionLineaTicket(promocionTicket);
				linea.addPromocion(promocionLinea);
			}

			promocionLinea.addImporteTotalDto(ahorro);
			promocionLinea.addCantidadPromocion(aumento);
			linea.addCantidadPromocion(aumento);
			linea.recalcularImporteFinal();
			cantidadAplicada = cantidadAplicada.add(aumento);
			
			// GAP XX - INCIDENCIA DE PROBLEMAS EN NEGACIÓN DE LINEAS CON PROMOCIÓN DE PRECIO
			// Quitamos del mapa las cantidades para las comprobaciones posteriores.
			if(mapArticulosLineasTicket.containsKey(keyMapLineaAplicable)){
				mapArticulosLineasTicket.put(keyMapLineaAplicable, mapArticulosLineasTicket.get(keyMapLineaAplicable).subtract(aumento));
			}
		}

		if (BigDecimalUtil.isMayorACero(cantidadAplicada)) {
			promocionTicket.setImporteTotalAhorro(importeTotalAhorro);
			documento.addPromocion(promocionTicket);
		}	
		
	}

}
