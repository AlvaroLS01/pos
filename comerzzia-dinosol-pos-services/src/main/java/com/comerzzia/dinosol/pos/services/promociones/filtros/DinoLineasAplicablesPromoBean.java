package com.comerzzia.dinosol.pos.services.promociones.filtros;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

@Component
@Scope("prototype")
@Primary
public class DinoLineasAplicablesPromoBean extends LineasAplicablesPromoBean {

	private Logger log = Logger.getLogger(DinoLineasAplicablesPromoBean.class);

	@Override
	public void ordenarLineasPrecioDesc() {
		Comparator<LineaDocumentoPromocionable> comparator = new Comparator<LineaDocumentoPromocionable>(){

			@Override
			public int compare(LineaDocumentoPromocionable o1, LineaDocumentoPromocionable o2) {
				return o1.getPrecioAplicacionPromocion().compareTo(o2.getPrecioAplicacionPromocion());
			}
		};
		Collections.sort(lineasAplicables, comparator);

		for (LineaDocumentoPromocionable linea : lineasAplicables) {
			log.trace(linea);
		}
	}

	@Override
	public BigDecimal aplicaDescuento(PromocionTicket promocionTicket, BigDecimal descuento, BigDecimal precioUnidadRegalada, Boolean dtoPorcentaje, Boolean dtoImporte, BigDecimal cantidadSinDto,
	        BigDecimal cantidadConDto) {
		BigDecimal cantidadAplicada = BigDecimal.ZERO;
		BigDecimal importeAhorroAplicado = BigDecimal.ZERO;
		for (int i = 0; i < lineasAplicables.size();) {
			LineaDocumentoPromocionable linea = lineasAplicables.get(i);

			// Comprobamos si hay cantidad de artículos suficientes
			if (BigDecimalUtil.isMenorOrIgualACero(getCantidadArticulos().subtract(cantidadAplicada))) {
				break;
			}

			// Comprobamos si la promoción puede seguir aplicándose en esta línea
			BigDecimal cantidadLinea = linea.getCantidad().abs();			
			
			if (BigDecimalUtil.isMayorOrIgual(linea.getCantidadPromocion(), cantidadLinea)) {
				i++; // pasamos a la siguiente línea
				continue; // nueva iteración del bucle
			}

			// Obtenemos la promoción aplicada sobre la línea
			PromocionLineaTicket promocionLinea = linea.getPromocion(promocionTicket.getIdPromocion());
			PromocionLineaTicket promocionCandidata = promocionLinea;
			if (promocionCandidata == null) {
				promocionCandidata = new PromocionLineaTicket(promocionTicket);
			}

			BigDecimal aumento = BigDecimal.ONE;
			BigDecimal cantidadRestante = cantidadConDto.subtract(cantidadAplicada);
			if (BigDecimalUtil.isMenor(cantidadRestante, BigDecimal.ONE)) {
				aumento = cantidadRestante;
			}
			if (BigDecimalUtil.isMayor(linea.getCantidadPromocion().add(aumento), cantidadLinea)) {
				aumento = cantidadLinea.subtract(new BigDecimal(cantidadLinea.toBigInteger()));
			}

			promocionCandidata.addCantidadPromocion(aumento);
			linea.addCantidadPromocion(aumento);

			// Calculamos el importe de descuento a aplicar
			BigDecimal importeSinDto = linea.getPrecioAplicacionPromocion().multiply(aumento);
			BigDecimal ahorroAplicable = calcularAhorroAplicable(importeSinDto, dtoPorcentaje, precioUnidadRegalada, dtoImporte, descuento, aumento);
			
			if(BigDecimalUtil.isMenorACero(linea.getCantidad())) {
				ahorroAplicable = ahorroAplicable.negate();
			}

			// Aplicamos promoción sobre la línea y acumulamos ahorro
			importeAhorroAplicado = importeAhorroAplicado.add(BigDecimalUtil.redondear(ahorroAplicable));
			promocionCandidata.addImporteTotalDto(ahorroAplicable);
			promocionCandidata.addCantidadPromocionAplicada(aumento);
			cantidadAplicada = cantidadAplicada.add(aumento);
			if (promocionLinea == null) {
				linea.addPromocion(promocionCandidata);
			}
			linea.recalcularImporteFinal();

			// Comprobamos si ya hemos aplicado el descuento la cantidad de veces indicada
			if (BigDecimalUtil.isIgual(cantidadAplicada, cantidadConDto)) {
				return importeAhorroAplicado;
			}
		}
		if (BigDecimalUtil.isMayorACero(cantidadAplicada)) {
			return importeAhorroAplicado;
		}
		return null;
	}
	
	public void ordenarLineasPrecioAsc(){       
        Comparator<LineaDocumentoPromocionable> comparator = new Comparator<LineaDocumentoPromocionable>() {
            @Override
            public int compare(LineaDocumentoPromocionable o1, LineaDocumentoPromocionable o2) {
                return o1.getPrecioAplicacionPromocion().compareTo(o2.getPrecioAplicacionPromocion());
            }
        };
        Collections.sort(lineasAplicables, comparator.reversed());
    }
	
	@Override
	public void setLineasAplicables(List<LineaDocumentoPromocionable> lineasAplicables) {
		reset();
		this.lineasAplicables.clear();
		
		eliminarLineasAnuladas(lineasAplicables);
		
		for (LineaDocumentoPromocionable linea : lineasAplicables) {
			if (filtroPromoExclusiva && linea.tienePromocionLineaExclusiva()) {
				continue;
			}
			if (!linea.isAdmitePromociones()) {
				continue;
			}
			if (linea.tieneDescuentoManual()) {
				continue;
			}
			if (linea.tieneCambioPrecioManual()) {
				continue;
			}
			if (filtroLineasCantidadDecimales && tieneCantidadDecimales(linea)) {
				continue;
			}
			this.lineasAplicables.add(linea);
			cantidadArticulos = cantidadArticulos.add(linea.getCantidad().abs());
			importeLineasConDto = importeLineasConDto.add(linea.getImporteAplicacionPromocionConDto());
		}
	}

	private void eliminarLineasAnuladas(List<LineaDocumentoPromocionable> lineasAplicables) {
		Map<String, BigDecimal> mapaCantidades = new HashMap<String, BigDecimal>();
		for (LineaDocumentoPromocionable linea : lineasAplicables) {
			String codArticulo = linea.getCodArticulo();
			BigDecimal cantidad = linea.getCantidad();
			if(mapaCantidades.containsKey(codArticulo)) {
				cantidad = cantidad.add(mapaCantidades.get(codArticulo));
			}
			mapaCantidades.put(codArticulo, cantidad);
		}
		
		for(String codArticulo : mapaCantidades.keySet()) {
			BigDecimal cantidad = mapaCantidades.get(codArticulo);
			
			if(BigDecimalUtil.isIgualACero(cantidad)) {
				Iterator<LineaDocumentoPromocionable> it = lineasAplicables.iterator();
				while(it.hasNext()) {
					LineaDocumentoPromocionable linea = it.next();
					String codArticuloLinea = linea.getCodArticulo();
					if(codArticuloLinea.equals(codArticulo)) {
						it.remove();
					}
				}
			}
		}
	}
	
	@Override
	public BigDecimal aplicaDescuentoCandidato(PromocionTicket promocionTicket, BigDecimal descuento, BigDecimal precioUnidadRegalada, boolean dtoPorcentaje, boolean dtoImporte,
	        BigDecimal cantidadSinDto, BigDecimal cantidadConDto) {
		BigDecimal cantidadAplicada = BigDecimal.ZERO;
		BigDecimal cantidadSinAplicar = BigDecimal.ZERO;
		BigDecimal importeAhorroAplicado = BigDecimal.ZERO;
		for (int i = 0; i < lineasAplicables.size();) {
			LineaDocumentoPromocionable linea = lineasAplicables.get(i);

			// Comprobamos si hay cantidad de artículos suficientes
			if (BigDecimalUtil.isMenorOrIgualACero(getCantidadArticulos().subtract(cantidadAplicada))) {
				break;
			}

			// Comprobamos si la promoción puede seguir aplicándose en esta línea
			BigDecimal cantidadLinea = linea.getCantidad().abs();

			if (BigDecimalUtil.isMayorOrIgual(linea.getCantidadPromocionCandidata(), cantidadLinea)) {
				i++; // pasamos a la siguiente línea
				continue; // nueva iteración del bucle
			}

			// Obtenemos la promoción aplicada sobre la línea
			PromocionLineaTicket promocionLinea = linea.getPromocionCandidata(promocionTicket.getIdPromocion());
			PromocionLineaTicket promocionCandidata = promocionLinea;
			if (promocionCandidata == null) {
				promocionCandidata = new PromocionLineaTicket(promocionTicket);
			}

			BigDecimal aumento = BigDecimal.ONE;
			BigDecimal cantidadRestante = cantidadConDto.subtract(cantidadAplicada);
			if (BigDecimalUtil.isMenor(cantidadRestante, BigDecimal.ONE)) {
				aumento = cantidadRestante;
			}
			if (BigDecimalUtil.isMayor(linea.getCantidadPromocionCandidata().add(aumento), cantidadLinea)) {
				aumento = cantidadLinea.subtract(new BigDecimal(cantidadLinea.toBigInteger()));
			}

			promocionCandidata.addCantidadPromocion(aumento);
			linea.addCantidadPromocionCandidata(aumento);

			// Primero contamos todas las líneas que tenemos que dejar sin aplicar descuento (Para NxM)
			if (BigDecimalUtil.isMenor(cantidadSinAplicar, cantidadSinDto)) {
				cantidadSinAplicar = cantidadSinAplicar.add(aumento); // aumentamos cantidad de promoción
				if (promocionLinea == null) {
					linea.addPromocionCandidata(promocionCandidata);
				}
				continue; // nueva iteración del bucle
			}

			// Calculamos el importe de descuento a aplicar
			BigDecimal importeSinDto = linea.getPrecioAplicacionPromocion();
			BigDecimal ahorroAplicable = calcularAhorroAplicable(importeSinDto, dtoPorcentaje, precioUnidadRegalada, dtoImporte, descuento, aumento);

			// Aplicamos promoción sobre la línea y acumulamos ahorro
			importeAhorroAplicado = importeAhorroAplicado.add(BigDecimalUtil.redondear(ahorroAplicable));
			promocionCandidata.addImporteTotalDto(ahorroAplicable);
			promocionCandidata.addCantidadPromocionAplicada(aumento);
			cantidadAplicada = cantidadAplicada.add(aumento);
			if (promocionLinea == null) {
				linea.addPromocionCandidata(promocionCandidata);
			}

			// Comprobamos si ya hemos aplicado el descuento la cantidad de veces indicada
			if (BigDecimalUtil.isIgual(cantidadAplicada, cantidadConDto)) {
				return importeAhorroAplicado;
			}
		}
		if (BigDecimalUtil.isMayorACero(cantidadAplicada)) {
			return importeAhorroAplicado;
		}
		return null;
	}

}
