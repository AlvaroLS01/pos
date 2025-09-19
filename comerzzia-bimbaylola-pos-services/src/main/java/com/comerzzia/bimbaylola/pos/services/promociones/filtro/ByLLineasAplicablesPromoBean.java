package com.comerzzia.bimbaylola.pos.services.promociones.filtro;

import java.math.BigDecimal;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

@Component
@Primary
@Scope("prototype")
public class ByLLineasAplicablesPromoBean extends LineasAplicablesPromoBean {
	
	@Override
	public BigDecimal aplicaDescuentoCandidato(PromocionTicket promocionTicket, BigDecimal descuento, BigDecimal precioUnidadRegalada, boolean dtoPorcentaje, boolean dtoImporte,
	        BigDecimal cantidadSinDto, BigDecimal cantidadConDto) {
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
            if (BigDecimalUtil.isMayorOrIgual(linea.getCantidadPromocionCandidata(), linea.getCantidad())){
                i++; // pasamos a la siguiente línea
                continue; // nueva iteración del bucle
            }

            // Obtenemos la promoción aplicada sobre la línea
            PromocionLineaTicket promocionLinea = linea.getPromocionCandidata(promocionTicket.getIdPromocion());
            PromocionLineaTicket promocionCandidata = promocionLinea;
            if (promocionCandidata == null){
                promocionCandidata = new PromocionLineaTicket(promocionTicket);
            }
            
            BigDecimal aumento = BigDecimal.ONE;
            BigDecimal cantidadRestante = cantidadConDto.subtract(cantidadAplicada);
            if (BigDecimalUtil.isMenor(cantidadRestante, BigDecimal.ONE)) {
            	aumento = cantidadRestante;
            }
            if (BigDecimalUtil.isMayor(linea.getCantidadPromocionCandidata().add(aumento), linea.getCantidad())) {
            	aumento =  linea.getCantidad().subtract(new BigDecimal(linea.getCantidad().toBigInteger()));
            }
            
            promocionCandidata.addCantidadPromocion(aumento);
            linea.addCantidadPromocionCandidata(aumento);

            // Primero contamos todas las líneas que tenemos que dejar sin aplicar descuento (Para NxM)
            if (BigDecimalUtil.isMenor(cantidadSinAplicar, cantidadSinDto)){
                cantidadSinAplicar = cantidadSinAplicar.add(aumento); // aumentamos cantidad de promoción
                if (promocionLinea == null){
                    linea.addPromocionCandidata(promocionCandidata);
                }
                continue; // nueva iteración del bucle
            }
            
            // Calculamos el importe de descuento a aplicar
            BigDecimal importeSinDto = linea.getPrecioAplicacionPromocion();
            BigDecimal ahorroAplicable = calcularAhorroAplicable(importeSinDto, dtoPorcentaje, precioUnidadRegalada, dtoImporte, descuento, aumento);
            
            BigDecimal importeTrasDto = importeSinDto.subtract(ahorroAplicable);
            BigDecimal precioOriginal = ((LineaTicket) linea).getTarifaOriginal().getPrecioTotal();
			if(BigDecimalUtil.isMenor(precioOriginal, importeTrasDto)) {
            	linea.cambiarTarifaOriginal();
            	i++;
            	continue;
            }
            
            // Aplicamos promoción sobre la línea y acumulamos ahorro
            importeAhorroAplicado = importeAhorroAplicado.add(BigDecimalUtil.redondear(ahorroAplicable));
            promocionCandidata.addImporteTotalDto(ahorroAplicable);
            promocionCandidata.addCantidadPromocionAplicada(aumento);
            cantidadAplicada = cantidadAplicada.add(aumento);
            if (promocionLinea == null){
                linea.addPromocionCandidata(promocionCandidata);
            }

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

	@Override
	public BigDecimal aplicaDescuento(PromocionTicket promocionTicket, BigDecimal descuento, BigDecimal precioUnidadRegalada, Boolean dtoPorcentaje, Boolean dtoImporte, BigDecimal cantidadSinDto,
	        BigDecimal cantidadConDto) {
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
            BigDecimal importeSinDto = linea.getPrecioAplicacionPromocion().multiply(aumento); 
            BigDecimal ahorroAplicable = calcularAhorroAplicable(importeSinDto, dtoPorcentaje, precioUnidadRegalada, dtoImporte, descuento, aumento);
            
            BigDecimal importeTrasDto = importeSinDto.subtract(ahorroAplicable);
            BigDecimal precioOriginal = ((LineaTicket) linea).getTarifaOriginal().getPrecioTotal();
			if(BigDecimalUtil.isMenor(precioOriginal, importeTrasDto)) {
            	linea.cambiarTarifaOriginal();
            	i++;
            	continue;
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
        if (BigDecimalUtil.isMayorACero(cantidadAplicada)){
            return importeAhorroAplicado;
        }
        return null;
	}

}
