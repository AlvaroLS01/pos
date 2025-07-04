package com.comerzzia.cardoso.pos.services.promociones.filtro;

import java.math.BigDecimal;

import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;

public class CardosoLineasAplicablesPromoBean extends LineasAplicablesPromoBean {
	
	private VariablesServices variablesService = SpringContext.getBean(VariablesServices.class);
	
	private final String tratarLineasDevolucionStr = variablesService.getVariableAsString("PROMOCIONES.TRATAR_LINEAS_DEVOLUCION");
	
	@Override
	public BigDecimal aplicaDescuento(PromocionTicket promocionTicket, BigDecimal descuento, BigDecimal precioUnidadRegalada, Boolean dtoPorcentaje, Boolean dtoImporte, BigDecimal cantidadSinDto, BigDecimal cantidadConDto) {
		if(promocionTicket.getIdTipoPromocion().equals(2L) && BigDecimalUtil.isMenorACero(getCantidadArticulos())){	
			if(tratarLineasDevolucionStr.equals("S")){
				return aplicaDescuentoCardoso(promocionTicket, descuento, dtoPorcentaje, cantidadSinDto, cantidadConDto);
			}
			else{
				return null;
			}
		}else{
			return aplicaDescuentoPersonalizado(promocionTicket, descuento, dtoPorcentaje, cantidadSinDto, cantidadConDto);
		}
	}
	
	public BigDecimal aplicaDescuentoCardoso(PromocionTicket promocionTicket, BigDecimal descuento, boolean dtoPorcentaje, BigDecimal cantidadSinDto, BigDecimal cantidadConDto) {
		BigDecimal cantidadAplicada = BigDecimal.ZERO;
        BigDecimal cantidadSinAplicar = BigDecimal.ZERO;
        BigDecimal importeAhorroAplicado = BigDecimal.ZERO;
        for (int i = 0; i < lineasAplicables.size();) {
            LineaDocumentoPromocionable linea = lineasAplicables.get(i);

            //Comprobamos si hay cantidad de artículos suficientes
			if (BigDecimalUtil.isMenorOrIgualACero(getCantidadArticulos().abs().subtract(cantidadAplicada))) {
				break;
			}
            
            // Comprobamos si la promoción puede seguir aplicándose en esta línea
            if (BigDecimalUtil.isMayorOrIgual(linea.getCantidadPromocion().abs(), linea.getCantidad().abs())){
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
            if (BigDecimalUtil.isMayor(linea.getCantidadPromocion().add(aumento), linea.getCantidad().abs())) {
            	aumento =  linea.getCantidad().abs().subtract(new BigDecimal(linea.getCantidad().toBigInteger()).abs());
            }
            
            promocionCandidata.addCantidadPromocion(aumento);
            linea.addCantidadPromocion(aumento);

            // Primero contamos todas las líneas que tenemos que dejar sin aplicar descuento (Para NxM)
            if (BigDecimalUtil.isMenor(cantidadSinAplicar, cantidadSinDto)){
                cantidadSinAplicar = cantidadSinAplicar.add(aumento); // aumentamos cantidad de promoción
                if (promocionLinea == null){
                    linea.addPromocion(promocionCandidata);
                }
                continue; // nueva iteración del bucle
            }
            
            // Calculamos el importe de descuento a aplicar
            BigDecimal ahorroAplicable;
            BigDecimal importeSinDto = linea.getPrecioAplicacionPromocion().multiply(aumento); 
            
            if (dtoPorcentaje){ // El descuento será por porcentaje, calculamos el importe aplicable 
                ahorroAplicable = BigDecimalUtil.porcentaje(importeSinDto, descuento);
            }
            else{
                ahorroAplicable = importeSinDto.subtract(descuento);
            }
            if (BigDecimalUtil.isMenorACero(ahorroAplicable)){
            	ahorroAplicable = BigDecimal.ZERO;
            }
            // Aplicamos promoción sobre la línea y acumulamos ahorro
            importeAhorroAplicado = importeAhorroAplicado.add(BigDecimalUtil.redondear(ahorroAplicable));
            promocionCandidata.addImporteTotalDto(ahorroAplicable.negate());
            promocionCandidata.addCantidadPromocionAplicada(aumento);
            cantidadAplicada = cantidadAplicada.add(aumento);
            if (promocionLinea == null){
                linea.addPromocion(promocionCandidata);
            }
            linea.recalcularImporteFinal();

            // Comprobamos si ya hemos aplicado el descuento la cantidad de veces indicada
            if (BigDecimalUtil.isIgual(cantidadAplicada, cantidadConDto)){
                return importeAhorroAplicado;
            }
        }
        if (BigDecimalUtil.isMenorACero(cantidadAplicada)){
            return importeAhorroAplicado;
        }
        return null;
	}

	 public BigDecimal aplicaDescuentoPersonalizado(PromocionTicket promocionTicket, BigDecimal descuento, boolean dtoPorcentaje, BigDecimal cantidadSinDto, BigDecimal cantidadConDto){
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
	                }
	                continue; // nueva iteración del bucle
	            }
	            
	            // Calculamos el importe de descuento a aplicar
	            BigDecimal ahorroAplicable;
	            BigDecimal importeSinDto = linea.getPrecioAplicacionPromocion().multiply(aumento); 
	            
	            if (dtoPorcentaje){ // El descuento será por porcentaje, calculamos el importe aplicable 
	                ahorroAplicable = BigDecimalUtil.porcentaje(importeSinDto, descuento);
	            }
	            else{
	                ahorroAplicable = importeSinDto.subtract(descuento);
	            }
	            if (BigDecimalUtil.isMenorACero(ahorroAplicable)){
	            	ahorroAplicable = BigDecimal.ZERO;
	            }
	            // Aplicamos promoción sobre la línea y acumulamos ahorro
	            importeAhorroAplicado = importeAhorroAplicado.add(BigDecimalUtil.redondear(ahorroAplicable));
	            promocionCandidata.addImporteTotalDto(ahorroAplicable);
	            promocionCandidata.addCantidadPromocionAplicada(aumento);
	            cantidadAplicada = cantidadAplicada.add(aumento);
	            if (promocionLinea == null){
	                linea.addPromocion(promocionCandidata);
	            }
	            linea.recalcularImporteFinal();

	            // Comprobamos si ya hemos aplicado el descuento la cantidad de veces indicada
	            if (BigDecimalUtil.isIgual(cantidadAplicada, cantidadConDto)){
	                return importeAhorroAplicado;
	            }
	        }
	        if (BigDecimalUtil.isMenorACero(cantidadAplicada)){
	            return importeAhorroAplicado;
	        }
	        return null;
	    }
}
