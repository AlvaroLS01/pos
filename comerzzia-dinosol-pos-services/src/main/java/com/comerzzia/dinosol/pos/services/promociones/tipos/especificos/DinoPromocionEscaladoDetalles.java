package com.comerzzia.dinosol.pos.services.promociones.tipos.especificos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.core.util.xml.XMLDocumentException;
import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.dinosol.pos.services.promociones.filtros.DinoLineasAplicablesPromoBean;
import com.comerzzia.pos.persistence.promociones.detalle.PromocionDetalleBean;
import com.comerzzia.pos.persistence.promociones.detalle.PromocionDetalleKey;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionEscaladoDetalles;
import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.DetallePromocion;
import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.escalado.DetallePromocionEscalado;
import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.escalado.TramoEscalado;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaCandidataTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtil;

@Component
@Primary
@Scope("prototype")
public class DinoPromocionEscaladoDetalles extends PromocionEscaladoDetalles {
	
	private Logger log = Logger.getLogger(DinoPromocionEscaladoDetalles.class);
	
	protected List<DinoDetallePromocionEscalado> detalles;
	protected Map<PromocionDetalleKey, DetallePromocion> detallesArticulos;
		
	@Autowired
	private VariablesServices variablesServices;
	
	public void setDetalles(List<PromocionDetalleBean> detallesBean) throws XMLDocumentException {
		Map<Long, DinoDetallePromocionEscalado> mapaDetalles = new HashMap<>(); // Map de IdAgrupacion -> Agrupacion (tipodDto, tramos)
		detallesArticulos = new HashMap<>(); // Map de codArt -> Agrupación
		for (PromocionDetalleBean promocionDetalleBean : detallesBean) {
			DinoDetallePromocionEscalado detalleEscalado = mapaDetalles.get(promocionDetalleBean.getIdAgrupacion());
			if (detalleEscalado == null) {
				detalleEscalado = new DinoDetallePromocionEscalado(this, promocionDetalleBean);
				if(!detalleEscalado.isXmlLeido()) {
					detalleEscalado.leerXml();
				}
				mapaDetalles.put(promocionDetalleBean.getIdAgrupacion(), detalleEscalado);
			} else {
				detalleEscalado.getLineasAgrupacion().add(createLineaPromocion(promocionDetalleBean));
			}
			PromocionDetalleKey key = new PromocionDetalleKey();
			key.setCodArticulo(promocionDetalleBean.getCodArticulo());
			key.setDesglose1(promocionDetalleBean.getDesglose1());
			key.setDesglose2(promocionDetalleBean.getDesglose2());
			detallesArticulos.put(key, detalleEscalado);
		}
		detalles = new ArrayList<>(mapaDetalles.values()); // Lista de agrupaciones
    }
	
	protected DinoLineaDetallePromocionEscalado createLineaPromocion(PromocionDetalleBean promocionDetalleBean) throws XMLDocumentException {
		String codArticulo = promocionDetalleBean.getCodArticulo();
		String desglose1 = promocionDetalleBean.getDesglose1();
		String desglose2 = promocionDetalleBean.getDesglose2();
		List<TramoEscalado> tramos = new ArrayList<TramoEscalado>();
		XMLDocument xml = new XMLDocument(promocionDetalleBean.getDatosPromocion());
		XMLDocumentNode nodoTramos = xml.getNodo("tramos");
		for (XMLDocumentNode nodoTramo : nodoTramos.getHijos()) {
			XMLDocumentNode nodoCantidadDesde = nodoTramo.getNodo("cantidadDesde", false);
			BigDecimal cantidadDesde = FormatUtil.getInstance().desformateaImporte(nodoCantidadDesde.getValue());
			XMLDocumentNode nodoCantidadHasta = nodoTramo.getNodo("cantidadHasta", false);
			BigDecimal cantidadHasta = FormatUtil.getInstance().desformateaImporte(nodoCantidadHasta.getValue());
			BigDecimal valor = FormatUtil.getInstance().desformateaImporte(nodoTramo.getNodo("valor").getValue());

			TramoEscalado tramoEscalado = new TramoEscalado();
			tramoEscalado.setCantidadDesde(cantidadDesde);
			tramoEscalado.setCantidadHasta(cantidadHasta);
			tramoEscalado.setValor(valor);
			tramos.add(tramoEscalado);
		}

		String tipoDto = xml.getNodo("tipoFiltro").getValue();
		return new DinoLineaDetallePromocionEscalado(codArticulo, desglose1, desglose2, tipoDto, tramos, promocionDetalleBean.getFechaInicio(), promocionDetalleBean.getFechaFin());
	}
	
	@Override
	public DetallePromocionEscalado getDetallePromocionEscalado(String codArticulo, String desglose1, String desglose2) {
		PromocionDetalleKey key = new PromocionDetalleKey();
		key.setCodArticulo(codArticulo);
		key.setDesglose1(desglose1);
		key.setDesglose2(desglose2);
		
		DinoDetallePromocionEscalado detalle = (DinoDetallePromocionEscalado) detallesArticulos.get(key);
		if (detalle != null && !detalle.isXmlLeido()) {
			try {
				detalle.leerXml();
			}
			catch (XMLDocumentException e) {
				log.error("createDetallePromocion() - Ha habido un error al leer el XML del detalle de la promoción: " + e.getMessage(), e);
				detalle = null;
			}
		}
		return detalle;
	}
	
	@Override
	public BigDecimal calcularPromocion(LineasAplicablesPromoBean lineasCondicion, LineasAplicablesPromoBean lineasAplicables) {
		Calendar calendarHoy = Calendar.getInstance();
        calendarHoy.set(Calendar.HOUR_OF_DAY, 0);
        calendarHoy.set(Calendar.MINUTE, 0);
        calendarHoy.set(Calendar.SECOND, 0);
        calendarHoy.set(Calendar.MILLISECOND, 0);
        // Inicializamos un mapa con las cantidades del ticket
		Map<DinoArticuloLineaDTO, BigDecimal> cantidadesDocumento = obtenerMapaCantidadesTicket(lineasAplicables);
		
		PromocionTicket promocionTicket = createPromocionTicket(codigoCupon);
		
		int cantidadAplicada = 0;
		int cantidadComprobada = 0;
		int cantidadLineaComprobada = 0;
		BigDecimal importeTotalAhorro = BigDecimal.ZERO;
		BigDecimal importeTotalAhorroLinea = BigDecimal.ZERO;
		for (int i = 0; i < lineasAplicables.getLineasAplicables().size();) {
			LineaDocumentoPromocionable linea = lineasAplicables.getLineasAplicables().get(i);

			// Obtenemos detalle aplicable para este artículo (sólo puede haber uno en cada promoción unitaria)
			DinoDetallePromocionEscalado detallePromocion = (DinoDetallePromocionEscalado) getDetallePromocionEscalado(linea.getCodArticulo(), linea.getDesglose1(), linea.getDesglose2());
			
			if(detallePromocion != null) {
				BigDecimal cantidadArticulosTicket = obtenerCantidadArticulosTicket(detallePromocion.getLineasAgrupacion(calendarHoy), cantidadesDocumento);
				
				//Calculamos la cantidad de artículos de poco valor que quedarían fuera de la promoción por sobrepasar la máxima cantidad "hasta" de la promoción
				BigDecimal cantidadHasta = detallePromocion.getTramos().get(detallePromocion.getTramos().size()-1).getCantidadHasta();
				BigDecimal cantidadPostMaximo = BigDecimalUtil.isMayor(cantidadArticulosTicket,cantidadHasta)?cantidadArticulosTicket.subtract(cantidadHasta):BigDecimal.ZERO;
				
				BigDecimal cantidadArticuloTicketPendiente = cantidadArticulosTicket.subtract(new BigDecimal(cantidadComprobada)).subtract(cantidadPostMaximo);
				
				BigDecimal valor = null;
				Boolean preMinimo = false; //Especifíca si esta linea tiene algún tramo cuya "cantidadDesde" es mayor a la cantidad de articulos pendientes de comprobar su tramo
				for(TramoEscalado tramoEscalado : detallePromocion.getTramos()) {
					if(BigDecimalUtil.isMenor(cantidadArticuloTicketPendiente, tramoEscalado.getCantidadDesde())){
						preMinimo = true;
					}
					if(comprobarTramo(cantidadArticuloTicketPendiente, tramoEscalado)) {
						valor = tramoEscalado.getValor();
						break;
					}
				}
				
				//Si no está dentro de ningún tramo porque se evalua un artículo por encima de la mayor cantidad hasta no aplicamos ninguna promoción
				if(valor==null && !preMinimo){
					cantidadComprobada++;
					cantidadLineaComprobada++;
					if(BigDecimalUtil.isIgual(linea.getCantidad(), new BigDecimal(cantidadLineaComprobada))) {
						importeTotalAhorro = importeTotalAhorro.add(importeTotalAhorroLinea);
						importeTotalAhorroLinea = BigDecimal.ZERO;
						cantidadLineaComprobada = 0;
						i++;
					}
					continue;
				}
	
				// Comprobamos si la línea ya tiene una promoción y si el precio de la promoción es menor que el de la de
				// escalado
				if(valor!=null && detallePromocion.isTipoDtoNuevoPrecio()) {
					BigDecimal precioPromocionTotalSinDto = ((LineaTicket) linea).getPrecioPromocionTotalSinDto();
					if (precioPromocionTotalSinDto != null && BigDecimalUtil.isMenor(precioPromocionTotalSinDto, valor)) {
						i++;
						importeTotalAhorro = importeTotalAhorro.add(importeTotalAhorroLinea);
						importeTotalAhorroLinea = BigDecimal.ZERO; //El importe total de ahorro en linea se pone a cero antes de pasar a la siguiente linea
						continue;
					}
				}

				
				if(valor!=null){					
					importeTotalAhorroLinea = importeTotalAhorroLinea.add(calcularImporteTotalAhorroLinea(linea, detallePromocion, valor)); //Sumamos el ahorro en el artículo al ahorro total en linea
				}
	
				PromocionLineaTicket promocionLinea = linea.getPromocion(promocionTicket.getIdPromocion());
				if (promocionLinea == null) {
					promocionLinea = new PromocionLineaTicket(promocionTicket);
					linea.addPromocionCandidata(promocionLinea);
				}
	
				promocionLinea.setImporteTotalDto(importeTotalAhorroLinea);
				promocionLinea.addCantidadPromocion(BigDecimal.ONE); //Se añade una promoción a la linea
				
				linea.addCantidadPromocionCandidata(BigDecimal.ONE);
				linea.recalcularImporteFinal();
				cantidadAplicada++;
				cantidadComprobada++;
				cantidadLineaComprobada++;
				if(BigDecimalUtil.isIgual(linea.getCantidad(), new BigDecimal(cantidadLineaComprobada))) {
					importeTotalAhorro = importeTotalAhorro.add(importeTotalAhorroLinea);
					importeTotalAhorroLinea = BigDecimal.ZERO;
					cantidadLineaComprobada = 0;
					i++;
				}
			}
		}

		if (cantidadAplicada > 0) {
			promocionTicket.setImporteTotalAhorro(importeTotalAhorro);
		}
		
		return importeTotalAhorro;
	}

	@Override
	public void aplicarPromocion(DocumentoPromocionable documento, LineasAplicablesPromoBean lineasCondicion, LineasAplicablesPromoBean lineasAplicables) {
		Calendar calendarHoy = Calendar.getInstance();
        calendarHoy.set(Calendar.HOUR_OF_DAY, 0);
        calendarHoy.set(Calendar.MINUTE, 0);
        calendarHoy.set(Calendar.SECOND, 0);
        calendarHoy.set(Calendar.MILLISECOND, 0);
        // Inicializamos un mapa con las cantidades del ticket
		Map<DinoArticuloLineaDTO, BigDecimal> cantidadesDocumento = obtenerMapaCantidadesTicket(lineasAplicables);
		
		PromocionTicket promocionTicket = documento.getPromocion(getIdPromocion());
		if (promocionTicket == null) {
			if(!detalles.isEmpty()) {
				setTextoPromocion(detalles.get(0).getTextoPromocion());
			}
			promocionTicket = createPromocionTicket(codigoCupon);
		}

		int cantidadAplicada = 0;
		int cantidadComprobada = 0;
		int cantidadLineaComprobada = 0;
		BigDecimal importeTotalAhorro = BigDecimal.ZERO;
		BigDecimal importeTotalAhorroLinea = BigDecimal.ZERO;
		HashMap<String, BigDecimal> articulosMapeados = new HashMap<>();

		for (int i = 0; i < lineasAplicables.getLineasAplicables().size();) {
 			LineaDocumentoPromocionable linea = lineasAplicables.getLineasAplicables().get(i);

			// Obtenemos detalle aplicable para este artículo (sólo puede haber uno en cada promoción unitaria)
			DinoDetallePromocionEscalado detallePromocion = (DinoDetallePromocionEscalado) getDetallePromocionEscalado(linea.getCodArticulo(), linea.getDesglose1(), linea.getDesglose2());
			
			if(detallePromocion != null) {
				BigDecimal cantidadArticulosTicket = obtenerCantidadArticulosTicket(detallePromocion.getLineasAgrupacion(calendarHoy), cantidadesDocumento);
				
				//Calculamos la cantidad de artículos de poco valor que quedarían fuera de la promoción por sobrepasar la máxima cantidad "hasta" de la promoción
				BigDecimal cantidadHasta = detallePromocion.getTramos().get(detallePromocion.getTramos().size()-1).getCantidadHasta();
				BigDecimal cantidadPostMaximo = BigDecimalUtil.isMayor(cantidadArticulosTicket,cantidadHasta)?cantidadArticulosTicket.subtract(cantidadHasta):BigDecimal.ZERO;

				BigDecimal cantidadArticuloTicketPendiente = cantidadArticulosTicket.subtract(new BigDecimal(cantidadComprobada)).subtract(cantidadPostMaximo);
				
				BigDecimal valor = null;
				Boolean preMinimo = false; //Especifíca si esta linea tiene algún tramo cuya "cantidadDesde" es mayor a la cantidad de articulos pendientes de comprobar su tramo
				for(TramoEscalado tramoEscalado : detallePromocion.getTramos()) {
					if(BigDecimalUtil.isMenor(cantidadArticuloTicketPendiente, tramoEscalado.getCantidadDesde())){
						preMinimo = true;
					}
					if(comprobarTramo(cantidadArticuloTicketPendiente, tramoEscalado)) {
						valor = tramoEscalado.getValor();
						break;
					}
				}
				
				//Si no está dentro de ningún tramo porque se evalua un artículo por encima de la mayor cantidad hasta no aplicamos ninguna promoción
				if(valor==null && !preMinimo){
					cantidadComprobada++;
					cantidadLineaComprobada++;
					if(BigDecimalUtil.isIgual(linea.getCantidad(), new BigDecimal(cantidadLineaComprobada))) {
						importeTotalAhorroLinea = BigDecimal.ZERO;
						cantidadLineaComprobada = 0;
						i++;
					}
					continue;
				}
	
				// Comprobamos si la línea ya tiene una promoción y si el precio de la promoción es menor que el de la de
				// escalado
				if(valor!=null && detallePromocion.isTipoDtoNuevoPrecio()) {
					BigDecimal precioPromocionTotalSinDto = ((LineaTicket) linea).getPrecioPromocionTotalSinDto();
					if (precioPromocionTotalSinDto != null && BigDecimalUtil.isMenor(precioPromocionTotalSinDto, valor)) {
						cantidadLineaComprobada = 0;
						i++;
						importeTotalAhorro = importeTotalAhorro.add(importeTotalAhorroLinea);
						importeTotalAhorroLinea = BigDecimal.ZERO; //El importe total de ahorro en linea se pone a cero antes de pasar a la siguiente linea
						continue;
					}
				}
				
				if(valor!=null){					
					importeTotalAhorroLinea = importeTotalAhorroLinea.add(calcularImporteTotalAhorroLinea(linea, detallePromocion, valor)); //Sumamos el ahorro en el artículo al ahorro total en linea
				}
	
				PromocionLineaTicket promocionLinea = linea.getPromocion(promocionTicket.getIdPromocion());
				if (promocionLinea == null) {
					promocionLinea = new PromocionLineaTicket(promocionTicket);
					linea.addPromocion(promocionLinea);
				}
				
				if(linea.getCantidad().compareTo(BigDecimal.ONE) > 0 ) {
					if( (new BigDecimal(cantidadAplicada).add(linea.getCantidad())).compareTo(cantidadHasta) > 0 ) {
						importeTotalAhorroLinea = importeTotalAhorroLinea.multiply(cantidadHasta.subtract(new BigDecimal(cantidadAplicada)));
					}else {
						importeTotalAhorroLinea = importeTotalAhorroLinea.multiply(linea.getCantidad().compareTo(cantidadHasta)<=0 ? linea.getCantidad() : cantidadHasta);												
					}
				}
				
				promocionLinea.setImporteTotalDto(importeTotalAhorroLinea);
				promocionLinea.addCantidadPromocion(linea.getCantidad().compareTo(BigDecimal.ONE) > 0 ? linea.getCantidad():BigDecimal.ONE); //Se añade una promoción a la linea
				
				linea.addCantidadPromocion(linea.getCantidad().compareTo(BigDecimal.ONE) > 0 ? linea.getCantidad():BigDecimal.ONE);
				linea.recalcularImporteFinal();
				
				cantidadAplicada = cantidadComprobada + linea.getCantidad().intValue();
				cantidadComprobada = cantidadComprobada + linea.getCantidad().intValue();
				cantidadLineaComprobada = cantidadComprobada + linea.getCantidad().intValue();

				if(BigDecimalUtil.isIgual(linea.getCantidad(), new BigDecimal(cantidadLineaComprobada))) {
					importeTotalAhorro = importeTotalAhorro.add(importeTotalAhorroLinea);
					cantidadLineaComprobada = 0;
				}
				importeTotalAhorroLinea = BigDecimal.ZERO;
				i++;
			}
		}

		if (cantidadAplicada > 0) {
			promocionTicket.setImporteTotalAhorro(importeTotalAhorro);
			documento.addPromocion(promocionTicket);
		}
	}
	
	@Override
	public void analizarLineasAplicables(DocumentoPromocionable documento) {
		Calendar calendarHoy = Calendar.getInstance();
        calendarHoy.set(Calendar.HOUR_OF_DAY, 0);
        calendarHoy.set(Calendar.MINUTE, 0);
        calendarHoy.set(Calendar.SECOND, 0);
        calendarHoy.set(Calendar.MILLISECOND, 0);
        // Inicializamos un mapa con las cantidades del ticket
		Map<DinoArticuloLineaDTO, BigDecimal> cantidadesDocumento = obtenerMapaCantidadesTicket(documento);
		// Creamos un PromocionLineaCandidataTicket por cada Agrupación
		for (DinoDetallePromocionEscalado detalle : detalles) {
			DinoLineasAplicablesPromoBean lineasAplicables = new DinoLineasAplicablesPromoBean();
			lineasAplicables.setFiltroPromoExclusiva(true);
	
			LinkedList<LineaDocumentoPromocionable> lineas = new LinkedList<LineaDocumentoPromocionable>();
			List<DinoLineaDetallePromocionEscalado> lineasAgrupacion = detalle.getLineasAgrupacion(calendarHoy);
			for (LineaDocumentoPromocionable linea : documento.getLineasDocumentoPromocionable()) {
				if(containsCodArticulo(lineasAgrupacion, linea.getCodArticulo(), linea.getDesglose1(), linea.getDesglose2())) {
					BigDecimal cantidadArticulosTicket = obtenerCantidadArticulosTicket(lineasAgrupacion, cantidadesDocumento);
				
					if(detalle != null) {
						if(detalle.getTramos() != null) {
							for(TramoEscalado tramoEscalado : detalle.getTramos()) {
								if(BigDecimalUtil.isMayorOrIgual(cantidadArticulosTicket, tramoEscalado.getCantidadDesde())) {
									lineas.add(linea);
									break;
								}
							}
						}
					}
				}
			}
			lineasAplicables.setLineasAplicables(lineas);
			
			// Ordenamos las líneas aplicables por precio descendente
	        lineasAplicables.ordenarLineasPrecioDesc();
	
			PromocionLineaCandidataTicket candidata = new PromocionLineaCandidataTicket(lineasAplicables, lineasAplicables, this);
			for (LineaDocumentoPromocionable linea : lineasAplicables.getLineasAplicables()) {
				linea.addPromocionAplicable(candidata);
			}
		}
	}

	protected boolean containsCodArticulo(List<DinoLineaDetallePromocionEscalado> lineas, String codArticulo, String desglose1, String desglose2) {
		String variableDesglose1 = variablesServices.getVariableAsString(VariablesServices.ARTICULO_DESGLOSE1_TITULO);
		String variableDesglose2 = variablesServices.getVariableAsString(VariablesServices.ARTICULO_DESGLOSE2_TITULO);
		
		for (DinoLineaDetallePromocionEscalado lineaDetallePromocionEscalado : lineas) {
			if (lineaDetallePromocionEscalado.getCodArticulo().equals(codArticulo) &&
				(StringUtils.isBlank(variableDesglose1) || (StringUtils.isBlank(desglose1) || lineaDetallePromocionEscalado.getDesglose1().equals("*")) || lineaDetallePromocionEscalado.getDesglose1().equals(desglose1)) &&
				(StringUtils.isBlank(variableDesglose2) || (StringUtils.isBlank(desglose2) || lineaDetallePromocionEscalado.getDesglose2().equals("*")) || lineaDetallePromocionEscalado.getDesglose2().equals(desglose2))	)  {
				return true;
			}
		}
		return false;
	}
	
	protected Map<DinoArticuloLineaDTO, BigDecimal> obtenerMapaCantidadesTicket(DocumentoPromocionable documento){
		Map<DinoArticuloLineaDTO, BigDecimal> cantidades = new HashMap<DinoArticuloLineaDTO, BigDecimal>();
		for(LineaDocumentoPromocionable lineaDoc : documento.getLineasDocumentoPromocionable()) {
			DinoArticuloLineaDTO articulo = new DinoArticuloLineaDTO(lineaDoc.getCodArticulo(), lineaDoc.getDesglose1(), lineaDoc.getDesglose2());
			BigDecimal cantidad = cantidades.get(articulo);
			if(cantidad == null) {
				cantidad = lineaDoc.getCantidad();
			}else {
				cantidad = cantidad.add(lineaDoc.getCantidad());
			}
			cantidades.put(articulo, cantidad);
		}
		return cantidades;
	}
	
	protected Map<DinoArticuloLineaDTO, BigDecimal> obtenerMapaCantidadesTicket(LineasAplicablesPromoBean lineasAplicables){
		Map<DinoArticuloLineaDTO, BigDecimal> cantidades = new HashMap<DinoArticuloLineaDTO, BigDecimal>();
		for(LineaDocumentoPromocionable lineaDoc : lineasAplicables.getLineasAplicables()) {
			DinoArticuloLineaDTO articulo = new DinoArticuloLineaDTO(lineaDoc.getCodArticulo(), lineaDoc.getDesglose1(), lineaDoc.getDesglose2());
			BigDecimal cantidad = cantidades.get(articulo);
			if(cantidad == null) {
				cantidad = lineaDoc.getCantidad();
			}else {
				cantidad = cantidad.add(lineaDoc.getCantidad());
			}
			cantidades.put(articulo, cantidad);
		}
		return cantidades;
	}
	
	protected BigDecimal obtenerCantidadArticulosTicket(List<DinoLineaDetallePromocionEscalado> lineasAgrupacion, Map<DinoArticuloLineaDTO, BigDecimal> cantidadesTicket) {
		BigDecimal cantidadArticulosTicket = BigDecimal.ZERO;
		for(DinoLineaDetallePromocionEscalado lineaDetalle : lineasAgrupacion) {
			DinoArticuloLineaDTO articulo = new DinoArticuloLineaDTO(lineaDetalle.getCodArticulo(), lineaDetalle.getDesglose1(), lineaDetalle.getDesglose2());
			BigDecimal cantidadArticuloTicket = cantidadesTicket.get(articulo);
			if(cantidadArticuloTicket != null) {
				cantidadArticulosTicket = cantidadArticulosTicket.add(cantidadArticuloTicket);
			}
		}
		return cantidadArticulosTicket;
	}
	
	@Override
	protected BigDecimal calcularImporteTotalAhorroLinea(LineaDocumentoPromocionable linea, DetallePromocionEscalado detallePromocion, BigDecimal valor) {
		LineaTicket lineaTicket = (LineaTicket) linea;
		
		BigDecimal importeTotalAhorro = BigDecimal.ZERO;
			
		if(detallePromocion.isTipoDtoDescuento()) {
			importeTotalAhorro = BigDecimalUtil.porcentaje(lineaTicket.getPrecioAplicacionPromocion(), valor);
		}
		else if(detallePromocion.isTipoDtoImporteDto()) {
			importeTotalAhorro = valor;
		}
		else if(detallePromocion.isTipoDtoNuevoPrecio()) {
			importeTotalAhorro = lineaTicket.getPrecioAplicacionPromocion().subtract(valor);
		}
			
	    return BigDecimalUtil.redondear(importeTotalAhorro, 2);
    }
	
	@Override
	protected boolean comprobarTramo(BigDecimal cantidadArticuloTicket, TramoEscalado tramoEscalado) {
		BigDecimal cantidadDesde = BigDecimalUtil.isMenorOrIgualACero(tramoEscalado.getCantidadDesde())?BigDecimal.ONE:tramoEscalado.getCantidadDesde();
	    return BigDecimalUtil.isMayorOrIgual(cantidadArticuloTicket, cantidadDesde) && BigDecimalUtil.isMenorOrIgual(cantidadArticuloTicket, tramoEscalado.getCantidadHasta());
    }
	
}
