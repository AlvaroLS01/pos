package com.comerzzia.iskaypet.pos.services.promociones.filtros;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.iskaypet.pos.services.promociones.tipos.especificos.IskaypetPromocionPuntosBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionImpuestos;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionDescuentoLineaBean;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

/**
 * GAP97 ISK-238 - APLICACIÓN DE PROMOCIONES EN ARTÍCULOS EDITADOS
 */
@Primary
@Component
@Scope("prototype")
public class IskaypetLineasAplicablesPromoBean extends com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean{

	public static final String TIPO_DTO_IVA = "IVA";
	
	@Autowired
	private VariablesServices variablesServices;
	@Autowired
	private SesionImpuestos sesionImpuestos;
	@Autowired
	private Sesion sesion;
	
	/*
	 * GAP97 ISK-238 - Se permiten promociones en artículos con descuento y cambio precio manuales,
	 * por lo demás, todo copiado de estándar
	 */
	@Override
	public void setLineasAplicables(List<LineaDocumentoPromocionable> lineasAplicables) {
        reset();
        this.lineasAplicables.clear();
        for (LineaDocumentoPromocionable linea : lineasAplicables) {
            if (filtroPromoExclusiva && linea.tienePromocionLineaExclusiva()){
                continue;
            }
            if (!linea.isAdmitePromociones()) {
                continue;
            }
//            if (linea.tieneDescuentoManual()){
//            	continue;
//            }
//            if (linea.tieneCambioPrecioManual()){ 
//            	continue;
//            }
            this.lineasAplicables.add(linea);
            cantidadArticulos = cantidadArticulos.add(linea.getCantidad());
            importeLineasConDto = importeLineasConDto.add(linea.getImporteAplicacionPromocionConDto());
        }
    }
	
	@SuppressWarnings("unchecked")
	@Override
	public BigDecimal aplicaDescuento(PromocionTicket promocionTicket, BigDecimal descuento, BigDecimal precioUnidadRegalada, Boolean dtoPorcentaje, Boolean dtoImporte, BigDecimal cantidadSinDto,
	        BigDecimal cantidadConDto){
		if(promocionTicket.getPromocion() instanceof PromocionDescuentoLineaBean){
			PromocionDescuentoLineaBean promoIVA = (PromocionDescuentoLineaBean) promocionTicket.getPromocion();
			if(TIPO_DTO_IVA.equalsIgnoreCase(promoIVA.getTipoDescuento())){
				return aplicaDescuentoIskaypet(promocionTicket, descuento, null, false, false, cantidadSinDto, cantidadConDto);	
			}
		}
		
		
		//GAP 173 [ISK-363] Se personaliza sin llamar al super ya que necesitamos las lineas sobre las que aplica la promocion
		if (promocionTicket.getAdicionales() == null) {
    		promocionTicket.setAdicionales(new HashMap<String, Object>());
    	}
    	
    	Map<String, Object> adicionales = promocionTicket.getAdicionales();
		
		BigDecimal cantidadAplicada = BigDecimal.ZERO;
        BigDecimal cantidadSinAplicar = BigDecimal.ZERO;
        BigDecimal importeAhorroAplicado = BigDecimal.ZERO;
        for (int i = 0; i < lineasAplicables.size();) {
            LineaDocumentoPromocionable linea = lineasAplicables.get(i);

            //Comprobamos si hay cantidad de artículos suficientes
			if (BigDecimalUtil.isMenorOrIgualACero(getCantidadArticulos().subtract(cantidadAplicada))) {
				break;
			}
            
            // Comprobamos si la promoción puede seguir aplicándose en esta línea
            if (BigDecimalUtil.isMayorOrIgual(linea.getCantidadPromocion(), linea.getCantidad())){
                i++; // pasamos a la siguiente línea
                continue; // nueva iteración del bucle
            }

            // Obtenemos la promoción aplicada sobre la línea
            PromocionLineaTicket promocionLinea = linea.getPromocion(promocionTicket.getIdPromocion());
            PromocionLineaTicket promocionCandidata = promocionLinea;
            if (promocionCandidata == null){
                promocionCandidata = new PromocionLineaTicket(promocionTicket);
            }
            
            BigDecimal aumento = BigDecimal.ONE;
            BigDecimal cantidadRestante = cantidadConDto.subtract(cantidadAplicada);
            if (BigDecimalUtil.isMenor(cantidadRestante, BigDecimal.ONE)) {
            	aumento = cantidadRestante;
            }
            if (BigDecimalUtil.isMayor(linea.getCantidadPromocion().add(aumento), linea.getCantidad())) {
            	aumento =  linea.getCantidad().subtract(new BigDecimal(linea.getCantidad().toBigInteger()));
            }
            
            promocionCandidata.addCantidadPromocion(aumento);
            linea.addCantidadPromocion(aumento);

            // Primero contamos todas las líneas que tenemos que dejar sin aplicar descuento (Para NxM)
            if (BigDecimalUtil.isMenor(cantidadSinAplicar, cantidadSinDto)){
                cantidadSinAplicar = cantidadSinAplicar.add(aumento); // aumentamos cantidad de promoción
                if (promocionLinea == null){
                    linea.addPromocion(promocionCandidata);
                    //GAP 173 [ISK-363]
                    adicionales.put("linea" + linea.getIdLinea(), linea.getIdLinea());
                }
                continue; // nueva iteración del bucle
            }
            
            // Calculamos el importe de descuento a aplicar
            BigDecimal importeSinDto = linea.getPrecioAplicacionPromocion().multiply(aumento); 
            BigDecimal ahorroAplicable = calcularAhorroAplicable(importeSinDto, dtoPorcentaje, precioUnidadRegalada, dtoImporte, descuento, aumento);
            
           
            // Aplicamos promoción sobre la línea y acumulamos ahorro
            importeAhorroAplicado = importeAhorroAplicado.add(BigDecimalUtil.redondear(ahorroAplicable));
            promocionCandidata.addImporteTotalDto(ahorroAplicable);
            promocionCandidata.addCantidadPromocionAplicada(aumento);
            cantidadAplicada = cantidadAplicada.add(aumento);
            
        	if (promocionTicket.getAdicionales() == null) {
        		promocionTicket.setAdicionales(new HashMap<String, Object>());
        	}
        	
        	
            
            if (promocionLinea == null){
            	
            	linea.addPromocion(promocionCandidata);
            	//GAP 173 [ISK-363]
            	adicionales.put("linea" + linea.getIdLinea(), linea.getIdLinea());
                
            }
            linea.recalcularImporteFinal();

            // Comprobamos si ya hemos aplicado el descuento la cantidad de veces indicada
            if (BigDecimalUtil.isIgual(cantidadAplicada, cantidadConDto)){
                return importeAhorroAplicado;
            }
        }
        if (BigDecimalUtil.isMayorACero(cantidadAplicada)){
            return importeAhorroAplicado;
        }
        return null;
	}

	public BigDecimal aplicaDescuentoIskaypet(PromocionTicket promocionTicket, BigDecimal descuento, BigDecimal precioUnidadRegalada, Boolean dtoPorcentaje, Boolean dtoImporte, BigDecimal cantidadSinDto,
	        BigDecimal cantidadConDto){
		BigDecimal cantidadAplicada = BigDecimal.ZERO; 
		BigDecimal cantidadSinAplicar = BigDecimal.ZERO;
		BigDecimal importeAhorroAplicado = BigDecimal.ZERO;

		/* tipo descuento IVA */
		boolean descuentoIVA = !dtoPorcentaje && !dtoImporte;
		
		for(int i = 0; i < lineasAplicables.size();){
			LineaDocumentoPromocionable linea = lineasAplicables.get(i);

			// Comprobamos si hay cantidad de artículos suficientes
			if(BigDecimalUtil.isMenorOrIgualACero(getCantidadArticulos().subtract(cantidadAplicada))){
				break;
			}

			// Comprobamos si la promoción puede seguir aplicándose en esta línea
			if(BigDecimalUtil.isMayorOrIgual(linea.getCantidadPromocion(), linea.getCantidad())){
				i++; // pasamos a la siguiente línea
				continue; // nueva iteración del bucle
			}

			// Obtenemos la promoción aplicada sobre la línea
			PromocionLineaTicket promocionLinea = linea.getPromocion(promocionTicket.getIdPromocion());
			PromocionLineaTicket promocionCandidata = promocionLinea;
			if(promocionCandidata == null){
				promocionCandidata = new PromocionLineaTicket(promocionTicket);
			}

			BigDecimal aumento = BigDecimal.ONE;
			BigDecimal cantidadRestante = cantidadConDto.subtract(cantidadAplicada);
			if(BigDecimalUtil.isMenor(cantidadRestante, BigDecimal.ONE)){
				aumento = cantidadRestante;
			}
			if(BigDecimalUtil.isMayor(linea.getCantidadPromocion().add(aumento), linea.getCantidad())){
				aumento = linea.getCantidad().subtract(new BigDecimal(linea.getCantidad().toBigInteger()));
			}

			promocionCandidata.addCantidadPromocion(aumento);
			linea.addCantidadPromocion(aumento);

			// Primero contamos todas las líneas que tenemos que dejar sin aplicar descuento (Para NxM)
			if(BigDecimalUtil.isMenor(cantidadSinAplicar, cantidadSinDto)){
				cantidadSinAplicar = cantidadSinAplicar.add(aumento); // aumentamos cantidad de promoción
				if(promocionLinea == null){
					linea.addPromocion(promocionCandidata);
				}
				continue; // nueva iteración del bucle
			}

			// Calculamos el importe de descuento a aplicar
			//BigDecimal importeSinDto = linea.getPrecioAplicacionPromocion().multiply(aumento);

			BigDecimal ahorroAplicable = BigDecimal.ZERO;

			/* Tipo descuento IVA */
			if(descuentoIVA){
				// calculamos el descuento de la linea
				//BigDecimal importeDescuentoIVA = ((LineaTicket) linea).getImpuestos();
				//ahorroAplicable = calcularAhorroAplicable(importeSinDto, dtoPorcentaje, precioUnidadRegalada, true, importeDescuentoIVA, aumento);
				ahorroAplicable = BigDecimalUtil.redondear(sesionImpuestos.getImpuestos(((LineaTicket) linea).getCodImpuesto(), sesion.getAplicacion().getTienda().getCliente().getIdTratImpuestos(), ((LineaTicket) linea).getPrecioSinDto()));
			}
//			else{
//				ahorroAplicable = calcularAhorroAplicable(importeSinDto, dtoPorcentaje, precioUnidadRegalada, dtoImporte, descuento, aumento);
//			}
			// BigDecimal ahorroAplicable = calcularAhorroAplicable(importeSinDto, dtoPorcentaje, precioUnidadRegalada,
			// dtoImporte, descuento, aumento);

			// Aplicamos promoción sobre la línea y acumulamos ahorro
			importeAhorroAplicado = importeAhorroAplicado.add(BigDecimalUtil.redondear(ahorroAplicable));
			promocionCandidata.addImporteTotalDto(ahorroAplicable);
			promocionCandidata.addCantidadPromocionAplicada(aumento);
			cantidadAplicada = cantidadAplicada.add(aumento);
			if(promocionLinea == null){
				linea.addPromocion(promocionCandidata);
			}
			linea.recalcularImporteFinal();

			// Comprobamos si ya hemos aplicado el descuento la cantidad de veces indicada
			if(BigDecimalUtil.isIgual(cantidadAplicada, cantidadConDto)){
				return importeAhorroAplicado;
			}
		}
		if(BigDecimalUtil.isMayorACero(cantidadAplicada)){
			return importeAhorroAplicado;
		}
		return null;
	}
	
	@Override
	public BigDecimal aplicaDescuentoCandidato(PromocionTicket promocionTicket, BigDecimal descuento, BigDecimal precioUnidadRegalada, boolean dtoPorcentaje, boolean dtoImporte,
	        BigDecimal cantidadSinDto, BigDecimal cantidadConDto){
		
		if(promocionTicket.getPromocion() instanceof PromocionDescuentoLineaBean){
			PromocionDescuentoLineaBean promoIVA = (PromocionDescuentoLineaBean) promocionTicket.getPromocion();
			if(TIPO_DTO_IVA.equalsIgnoreCase(promoIVA.getTipoDescuento())){
				return aplicaDescuentoCandidatoIskaypet(promocionTicket, descuento, null, false, false, cantidadSinDto, cantidadConDto);
			}
		}
		
		return super.aplicaDescuentoCandidato(promocionTicket, descuento, precioUnidadRegalada, dtoPorcentaje, dtoImporte, cantidadSinDto, cantidadConDto);
	}

	public BigDecimal aplicaDescuentoCandidatoIskaypet(PromocionTicket promocionTicket, BigDecimal descuento, BigDecimal precioUnidadRegalada, boolean dtoPorcentaje, boolean dtoImporte,
	        BigDecimal cantidadSinDto, BigDecimal cantidadConDto){
		BigDecimal cantidadAplicada = BigDecimal.ZERO;
		BigDecimal cantidadSinAplicar = BigDecimal.ZERO;
		BigDecimal importeAhorroAplicado = BigDecimal.ZERO;
		
		/* tipo descuento IVA */
		boolean descuentoIVA = !dtoPorcentaje && !dtoImporte;
		
		for(int i = 0; i < lineasAplicables.size();){
			LineaDocumentoPromocionable linea = lineasAplicables.get(i);

			// Comprobamos si hay cantidad de artículos suficientes
			if(BigDecimalUtil.isMenorOrIgualACero(getCantidadArticulos().subtract(cantidadAplicada))){
				break;
			}

			// Comprobamos si la promoción puede seguir aplicándose en esta línea
			if(BigDecimalUtil.isMayorOrIgual(linea.getCantidadPromocionCandidata(), linea.getCantidad())){
				i++; // pasamos a la siguiente línea
				continue; // nueva iteración del bucle
			}

			// Obtenemos la promoción aplicada sobre la línea
			PromocionLineaTicket promocionLinea = linea.getPromocionCandidata(promocionTicket.getIdPromocion());
			PromocionLineaTicket promocionCandidata = promocionLinea;
			if(promocionCandidata == null){
				promocionCandidata = new PromocionLineaTicket(promocionTicket);
			}

			BigDecimal aumento = BigDecimal.ONE;
			BigDecimal cantidadRestante = cantidadConDto.subtract(cantidadAplicada);
			if(BigDecimalUtil.isMenor(cantidadRestante, BigDecimal.ONE)){
				aumento = cantidadRestante;
			}
			if(BigDecimalUtil.isMayor(linea.getCantidadPromocionCandidata().add(aumento), linea.getCantidad())){
				aumento = linea.getCantidad().subtract(new BigDecimal(linea.getCantidad().toBigInteger()));
			}

			promocionCandidata.addCantidadPromocion(aumento);
			linea.addCantidadPromocionCandidata(aumento);

			// Primero contamos todas las líneas que tenemos que dejar sin aplicar descuento (Para NxM)
			if(BigDecimalUtil.isMenor(cantidadSinAplicar, cantidadSinDto)){
				cantidadSinAplicar = cantidadSinAplicar.add(aumento); // aumentamos cantidad de promoción
				if(promocionLinea == null){
					linea.addPromocionCandidata(promocionCandidata);
				}
				continue; // nueva iteración del bucle
			}

			//BigDecimal importeSinDto = linea.getPrecioAplicacionPromocion();
			/* Tipo descuento IVA */
			BigDecimal ahorroAplicable = BigDecimal.ZERO;
			if(descuentoIVA){
				// calculamos el descuento de la linea
				//BigDecimal importeDescuentoIVA = ((LineaTicket) linea).getImpuestos();
	            //ahorroAplicable = calcularAhorroAplicable(importeSinDto, dtoPorcentaje, precioUnidadRegalada, true, importeDescuentoIVA, aumento);
	            ahorroAplicable = BigDecimalUtil.redondear(sesionImpuestos.getImpuestos(((LineaTicket) linea).getCodImpuesto(), sesion.getAplicacion().getTienda().getCliente().getIdTratImpuestos(), ((LineaTicket) linea).getPrecioSinDto()));
			}
//			else{
//	            ahorroAplicable = calcularAhorroAplicable(importeSinDto, dtoPorcentaje, precioUnidadRegalada, dtoImporte, descuento, aumento);
//			}

			// Aplicamos promoción sobre la línea y acumulamos ahorro
			importeAhorroAplicado = importeAhorroAplicado.add(BigDecimalUtil.redondear(ahorroAplicable));
			promocionCandidata.addImporteTotalDto(ahorroAplicable);
			promocionCandidata.addCantidadPromocionAplicada(aumento);
			cantidadAplicada = cantidadAplicada.add(aumento);
			if(promocionLinea == null){
				linea.addPromocionCandidata(promocionCandidata);
			}

			// Comprobamos si ya hemos aplicado el descuento la cantidad de veces indicada
			if(BigDecimalUtil.isIgual(cantidadAplicada, cantidadConDto)){
				return importeAhorroAplicado;
			}
		}
		if(BigDecimalUtil.isMayorACero(cantidadAplicada)){
			return importeAhorroAplicado;
		}
		return null;
	}

	@Override
	public void aplicaPromocionPuntos(PromocionTicket promocionTicket, BigDecimal puntosEuros) {
		// Recorremos las líneas aplicables
		BigDecimal puntosLineasAcumulados = BigDecimal.ZERO;
		
		//esta variable nos indicará los punto que quedan por repartir.
		BigDecimal puntosTotales = promocionTicket.getPuntos();	
		
		if(BigDecimalUtil.isMayorOrIgualACero(puntosTotales)) {
			/*for (LineaDocumentoPromocionable lineaTicket : lineasAplicables) {
				PromocionLineaTicket promocionLinea = new PromocionLineaTicket(promocionTicket);
	
				BigDecimal puntosLinea = lineaTicket.getImporteAplicacionPromocionConDto().divide(puntosEuros, 2);
				
				//si es menor o igual cero, lo establecemos a 0
				if(BigDecimalUtil.isMayorACero(puntosLinea)) {
					//si los puntos calculados para la línea superan el total, establecemos el total como los puntos de la línea.
					if(BigDecimalUtil.isMayor(puntosLinea, puntosTotales)) {
						puntosLinea = puntosTotales;
					}
				}
				else {
					puntosLinea = BigDecimal.ZERO;
				}
				
				//asignamos los datos a la línea de venta
				promocionLinea.setPuntos(puntosLinea);
				promocionLinea.setCantidadPromocion(lineaTicket.getCantidad());
				lineaTicket.addPromocion(promocionLinea);
				
				//establecmeos los acumulados
				puntosLineasAcumulados = puntosLineasAcumulados.add(puntosLinea);
				puntosTotales =puntosTotales.subtract(puntosLinea);
				
				//si no quedan puntos a repartir nos salimos de bucle
				if(BigDecimalUtil.isMenorOrIgualACero(puntosTotales)) {
					puntosTotales = BigDecimal.ZERO;
				}
			}*/
			
			//calculamos el importe total
			BigDecimal importeTotalLineas = BigDecimal.ZERO;
			BigDecimal cien = new BigDecimal(100);
			
			for (LineaDocumentoPromocionable lineaTicket : lineasAplicables) {
				
				importeTotalLineas = importeTotalLineas.add(lineaTicket.getImporteAplicacionPromocionConDto());
			}
			
			//hacemos el reparto según elpeso de cada línea en la venta			
			for (LineaDocumentoPromocionable lineaTicket : lineasAplicables) {
				PromocionLineaTicket promocionLinea = new PromocionLineaTicket(promocionTicket);
	
				//calculamos el peso de la línea según el total para obtener los puntos
				BigDecimal peso = lineaTicket.getImporteAplicacionPromocionConDto().multiply(cien).divide(importeTotalLineas, 2, RoundingMode.HALF_UP);	
				BigDecimal puntosLinea = BigDecimalUtil.redondear(puntosTotales.multiply(peso.divide(cien)));
				
				//asignamos los datos a la línea de venta
				promocionLinea.setPuntos(puntosLinea);
				promocionLinea.setCantidadPromocion(lineaTicket.getCantidad());
				lineaTicket.addPromocion(promocionLinea);
				
				//establecmeos los acumulados
				puntosLineasAcumulados = puntosLineasAcumulados.add(puntosLinea);
			}
	
			//En caso caso de ser necesario realizamo el ajuste.
			puntosTotales = promocionTicket.getPuntos();
			// Si el prorrateo no se ajusta al los puntos totales, ajustamos equitativamente todas las lineas
			if (!BigDecimalUtil.isIgual(puntosLineasAcumulados,puntosTotales)) {
				BigDecimal diferencia = puntosTotales.subtract(puntosLineasAcumulados);
				
				//si la diferencia es  mayor a 0
				if(BigDecimalUtil.isMayorACero(diferencia)) {
					LineaDocumentoPromocionable lineaTicket = lineasAplicables.get(lineasAplicables.size()-1);
					IPromocionLineaTicket promocionLinea = lineaTicket.getPromocion(promocionTicket.getIdPromocion());
					promocionLinea.setPuntos(promocionLinea.getPuntos().add(diferencia));
				}
				else { //en el caso de que la diferencia sea menor a 0
					for (int i = lineasAplicables.size()-1; i >0; i--) {
						LineaDocumentoPromocionable lineaTicket = lineasAplicables.get(i);
						IPromocionLineaTicket promocionLinea = lineaTicket.getPromocion(promocionTicket.getIdPromocion());
						
						if(BigDecimalUtil.isMayorOrIgual(promocionLinea.getPuntos(),diferencia.abs())) {
							promocionLinea.setPuntos(promocionLinea.getPuntos().add(diferencia));
							diferencia = BigDecimal.ZERO;
						}
						else {
							diferencia = diferencia.add(promocionLinea.getPuntos());
							promocionLinea.setPuntos(BigDecimal.ZERO);					
						}
						
						if(BigDecimalUtil.isMenorOrIgualACero(diferencia)) {
							break;
						}
					}
				}	
			}
		}

	}
    public Boolean getVariableProcesoRedondeoPuntos(){
    	log.debug("getVariableProcesoRedondeoPuntos() - GAP45.1.- Variable para control de proceso de redondeo de puntos....");
        return variablesServices.getVariableAsBoolean(IskaypetPromocionPuntosBean.VARIABLE_APLICA_REDONDEO_PUNTOS, false);
    }
    
}
