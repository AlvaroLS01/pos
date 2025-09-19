package com.comerzzia.bimbaylola.pos.services.promociones.tipos;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

import com.comerzzia.pos.persistence.promociones.detalle.PromocionDetalleKey;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionNxMDetalles;
import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.DetallePromocionNxM;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
 
@Component
public class PromoNxMProrrateada extends PromocionNxMDetalles {
	
	@Override
	public void aplicarPromocion(DocumentoPromocionable documento, LineasAplicablesPromoBean lineasCondicion, LineasAplicablesPromoBean lineasAplicables) {
		log.debug("ENTRANDO EN PROMOCION NxM PRORRATEADA");
		//Obtenemos a qué agrupación pertenece los codArticulos de este LineasAplicables
    	String codArticulo = lineasAplicables.getLineasAplicables().get(0).getArticulo().getCodArticulo();
    	BigDecimal importeTotalAhorro = BigDecimal.ZERO;
    	PromocionDetalleKey key = new PromocionDetalleKey();
		key.setCodArticulo(codArticulo);
		key.setDesglose1(lineasAplicables.getLineasAplicables().get(0).getDesglose1());
		key.setDesglose2(lineasAplicables.getLineasAplicables().get(0).getDesglose2());
		DetallePromocionNxM detalleNxM = (DetallePromocionNxM) getDetalle(detallesArticulos, key);
		
		PromocionTicket promocionTicket = new PromocionTicket();
		if(detalleNxM != null){
			setTextoPromocion(detalleNxM.getTextoPromocion());
	    	// Calculamos el número de combos que podremos aplicar
	        Integer cantidadN = detalleNxM.getCantidadN();
	        Integer cantidadM = detalleNxM.getCantidadM();
	        BigDecimal descuento = detalleNxM.getDescuento();
			Integer numCombos = lineasAplicables.getCantidadArticulos().divide(new BigDecimal(cantidadN), 1, RoundingMode.DOWN).intValue();				
			Integer cantConDescuento = cantidadN - cantidadM;
			Integer cantSinDescuento = cantidadM;
			
	        for (int i = 1; i <= numCombos; i++) {
	            // Instanciamos la promoción del documento (cada combo tendrá una)
	            promocionTicket = createPromocionTicket(codigoCupon);
	            
	            // Intentamos aplicar la promoción sobre las líneas aplicables
				BigDecimal importe = lineasAplicables.aplicaDescuento(promocionTicket, descuento, null, true, false, new BigDecimal(cantSinDescuento), new BigDecimal(cantConDescuento));
				if (importe == null){
	                return;
	            }
				
				importeTotalAhorro = importeTotalAhorro.add(importe);
	            // Si conseguimos aplicar la promoción, añadimos el combo aplicado a las promociones del documento
	            promocionTicket.setImporteTotalAhorro(importeTotalAhorro);
	            documento.addPromocion(promocionTicket);
	        }
	        
	        BigDecimal importeTotalLineasOriginal = BigDecimal.ZERO;
			BigDecimal cantidadAplicada = new BigDecimal(cantidadN);
			BigDecimal importeTotalDto = BigDecimal.ZERO;
			BigDecimal importeAhorroAcumuladoLineas = BigDecimal.ZERO;
			int numLineas = 0;
			for(LineaDocumentoPromocionable linea : lineasAplicables.getLineasAplicables()) {
				PromocionLineaTicket promocion = linea.getPromocion(getIdPromocion());
				if(promocion != null) {
					LineaTicket lineaTicket = (LineaTicket) linea;
					importeTotalDto = importeTotalDto.add(promocion.getImporteTotalDtoMenosMargen());
					numLineas = numLineas + 1;
					importeTotalLineasOriginal = importeTotalLineasOriginal.add(lineaTicket.getImporteTotalSinDto());
				}
			}
	        
			BigDecimal cantidadAplicadaLinea = cantidadAplicada.divide(new BigDecimal(numLineas), 2, RoundingMode.HALF_UP);
			
			BigDecimal importeTotalLineasModificado = BigDecimal.ZERO;
			for(LineaDocumentoPromocionable linea : lineasAplicables.getLineasAplicables()) {
				PromocionLineaTicket promocion = linea.getPromocion(getIdPromocion());
				if(promocion != null) {
					
					LineaTicket lineaTicket = (LineaTicket) linea;
					
					log.debug("aplicarPromocion() - Calculando porcentaje sobre el total sin promocion aplicada");
		        	BigDecimal porcentajeSobreTotalSinPromo = (lineaTicket.getImporteTotalSinDto().multiply(new BigDecimal(100))).divide(importeTotalLineasOriginal, 2, RoundingMode.HALF_UP);
		        	
		        	log.debug("aplicarPromocion() - Calculando descuento correspondiente a la linea sobre el importe total ahorro");
		        	BigDecimal importeAhorroLinea = importeTotalDto.multiply(porcentajeSobreTotalSinPromo).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
					
		        	importeAhorroAcumuladoLineas = importeAhorroAcumuladoLineas.add(importeAhorroLinea);
					promocion.setCantidadPromocionAplicada(cantidadAplicadaLinea);
					promocion.setImporteTotalDtoMenosMargen(importeAhorroLinea);
					linea.recalcularImporteFinal();
					importeTotalLineasModificado = importeTotalLineasModificado.add(linea.getImporteAplicacionPromocionConDto());
				}
			}
	        
			BigDecimal diferenciaImportes = importeTotalAhorro.subtract(importeAhorroAcumuladoLineas);
			BigDecimal diferenciaImportesLinea = diferenciaImportes.divide(new BigDecimal(numCombos), 2, RoundingMode.HALF_UP);
			int numCombosArreglados = 0;
			if(!BigDecimalUtil.isIgualACero(diferenciaImportes)) {
				int i = 0;
				for(LineaDocumentoPromocionable linea : lineasAplicables.getLineasAplicables()) {
					PromocionLineaTicket promocion = linea.getPromocion(getIdPromocion());
					if(promocion != null && i%cantidadN == 0) {
						promocion.setImporteTotalDtoMenosMargen(promocion.getImporteTotalDtoMenosMargen().add(diferenciaImportesLinea));
						numCombosArreglados++;
						if(numCombosArreglados == numCombos) {
							break;
						}
					}
					i++;
				}
			}
	        
		}
	}

}

