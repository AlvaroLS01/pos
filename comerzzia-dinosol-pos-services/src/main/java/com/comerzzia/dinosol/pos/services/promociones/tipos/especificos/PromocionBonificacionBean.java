package com.comerzzia.dinosol.pos.services.promociones.tipos.especificos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ComparatorUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.core.util.xml.XMLDocumentException;
import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.dinosol.pos.services.promociones.filtros.DinoFiltroLineasPromocionCabecera;
import com.comerzzia.dinosol.pos.services.promociones.tipos.especificos.bonificacion.ArticuloRegaloTramoBonificacion;
import com.comerzzia.dinosol.pos.services.promociones.tipos.especificos.bonificacion.ComparatorTramoBonificacion;
import com.comerzzia.dinosol.pos.services.promociones.tipos.especificos.bonificacion.TramoBonificacion;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.FiltroLineasPromocion;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.promociones.tipos.PromocionLinea;
import com.comerzzia.pos.services.promociones.tipos.componente.CondicionPrincipalPromoBean;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaCandidataTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;

@Component
@Scope("prototype")
public class PromocionBonificacionBean extends PromocionLinea {

	protected CondicionPrincipalPromoBean condiciones;
	protected List<TramoBonificacion> tramos;

	@Override
	public void analizarLineasAplicables(DocumentoPromocionable documento) {
		log.debug("analizarLineasAplicables() - " + this);

		FiltroLineasPromocion filtroLineas = createFiltroLineasPromocion(documento);
		LineasAplicablesPromoBean lineasCondicion = filtroLineas.getNumCombosCondicion(condiciones);

		// Obtenemos las líneas de condición según el filtro configurado
		if (lineasCondicion.isEmpty() || lineasCondicion.getNumCombos() == 0) {
			log.trace(this + " analizarLineasAplicables() - La promoción no se puede aplicar por no existir las líneas que cumplan las condiciones en el documento .");
			return;
		}
		
		//Se obtienen las lineas sobre las que aplica la promoción
		LineasAplicablesPromoBean lineasAplicables = crearLineasAplicables(documento, lineasCondicion);

		PromocionLineaCandidataTicket candidata = new PromocionLineaCandidataTicket(lineasAplicables, lineasCondicion, this);
		for (LineaDocumentoPromocionable linea : lineasAplicables.getLineasAplicables()) {
			linea.addPromocionAplicable(candidata);
		}

	}

	@Override
	public BigDecimal calcularPromocion(LineasAplicablesPromoBean lineasCondicion, LineasAplicablesPromoBean lineasAplicables) {
		PromocionTicket promocionTicket = createPromocionTicket(codigoCupon);
		
		BigDecimal cantidadMaximaPromocion = BigDecimal.ZERO;
		for(LineaDocumentoPromocionable linea : lineasAplicables.getLineasAplicables()) {
			cantidadMaximaPromocion = cantidadMaximaPromocion.add(linea.getCantidad());
		}
		
		BigDecimal importeTotalAhorro= BigDecimal.ZERO;
		for(TramoBonificacion tramo : getTramosOrdenados(lineasCondicion.getLineasAplicables())) {
			if(BigDecimalUtil.isMenorOrIgualACero(cantidadMaximaPromocion)) {
				continue;
			}
			
			Map<String, BigDecimal> aplicacionesTramo = new HashMap<String, BigDecimal>();
			BigDecimal importeTotalAhorroLinea = BigDecimal.ZERO;
			importeTotalAhorro = BigDecimal.ZERO;
			Map<String, BigDecimal> ahorroEnArticulo = new HashMap<String, BigDecimal>();
			for (int i = 0; i < lineasAplicables.getLineasAplicables().size();) {
				LineaDocumentoPromocionable linea = lineasAplicables.getLineasAplicables().get(i);

				// Comprobamos si la promoción puede seguir aplicándose en esta línea según la cantidad
				if (BigDecimalUtil.isMayorOrIgual(linea.getCantidadPromocionCandidata(), linea.getCantidad())) {
					linea.recalcularImporteFinal();
					i++;
					importeTotalAhorro = importeTotalAhorro.add(importeTotalAhorroLinea);
					importeTotalAhorroLinea = BigDecimal.ZERO;
					continue;
				}

				if (!aplicacionesTramo.containsKey(linea.getCodArticulo())) {
					aplicacionesTramo.put(linea.getCodArticulo(), BigDecimal.ZERO);
				}

				// Comprobamos si podemos seguir aplicando ese tramo
				ArticuloRegaloTramoBonificacion articuloTramo = tramo.getArticulo(linea.getCodArticulo());
				if (articuloTramo==null || BigDecimalUtil.isMenorOrIgual(articuloTramo.getCantidad(), aplicacionesTramo.get(linea.getCodArticulo()))) {
					i++;
					importeTotalAhorro = importeTotalAhorro.add(importeTotalAhorroLinea);
					importeTotalAhorroLinea = BigDecimal.ZERO;
					continue;
				}
				
				BigDecimal precioSinDto = linea.getPrecioAplicacionPromocion();
				BigDecimal precioNuevo = articuloTramo.getPrecio(); 
				BigDecimal ahorro = BigDecimalUtil.isMayor(precioSinDto, precioNuevo) ? precioSinDto.subtract(precioNuevo) : precioSinDto;

				String codArt = linea.getCodArticulo();
				if(ahorroEnArticulo.get(codArt)==null){
					ahorroEnArticulo.put(codArt, BigDecimal.ZERO);
				}else{
					ahorroEnArticulo.put(codArt, ahorroEnArticulo.get(codArt).add(ahorro));
				}

				importeTotalAhorroLinea = importeTotalAhorroLinea.add(ahorro);
				PromocionLineaTicket promocionLinea = linea.getPromocionCandidata(promocionTicket.getIdPromocion());
				if (promocionLinea == null) {
					promocionLinea = new PromocionLineaTicket(promocionTicket);
					linea.addPromocionCandidata(promocionLinea);
				}

				promocionLinea.setImporteTotalDto(importeTotalAhorroLinea);
				promocionLinea.addCantidadPromocion(BigDecimal.ONE); // Se añade una promoción a la linea

				linea.addCantidadPromocionCandidata(BigDecimal.ONE);
				linea.recalcularImporteFinal();
				aplicacionesTramo.put(linea.getCodArticulo(), aplicacionesTramo.get(linea.getCodArticulo()).add(BigDecimal.ONE));

				cantidadMaximaPromocion = cantidadMaximaPromocion.subtract(linea.getCantidad());

			}
		}
		
		return importeTotalAhorro;
	}

	@Override
	public void aplicarPromocion(DocumentoPromocionable documento, LineasAplicablesPromoBean lineasCondicion, LineasAplicablesPromoBean lineasAplicables) {
		PromocionTicket promocionTicket = documento.getPromocion(getIdPromocion());
		if (promocionTicket == null) {
			promocionTicket = createPromocionTicket(codigoCupon);
		}
		
		//Si ya se ha aplicado esta promoción no se vuelve a aplicar
		if(estaAplicada(documento)){
			return;
		}
		
		BigDecimal cantidadMaximaPromocion = BigDecimal.ZERO;
		for(LineaDocumentoPromocionable linea : lineasAplicables.getLineasAplicables()) {
			cantidadMaximaPromocion = cantidadMaximaPromocion.add(linea.getCantidad());
		}
		
		BigDecimal importeTotalAhorro= BigDecimal.ZERO;
		List<TramoBonificacion> tramosOrdenados = getTramosOrdenados(lineasCondicion.getLineasAplicables());
		
		if(tramosOrdenados == null || tramosOrdenados.isEmpty()) {
			return;
		}
		
		TramoBonificacion tramo = tramosOrdenados.get(0);
		Map<String, BigDecimal> aplicacionesTramo = new HashMap<String, BigDecimal>();
		BigDecimal importeTotalAhorroLinea = BigDecimal.ZERO;
		Map<String, BigDecimal> ahorroEnArticulo = new HashMap<String, BigDecimal>();
		importeTotalAhorro = BigDecimal.ZERO;
		for (int i = 0; i < lineasAplicables.getLineasAplicables().size();) {
			LineaDocumentoPromocionable linea = lineasAplicables.getLineasAplicables().get(i);

			// Comprobamos si la promoción puede seguir aplicándose en esta línea según la cantidad
			if (BigDecimalUtil.isMayorOrIgual(linea.getCantidadPromocion(), linea.getCantidad()) || BigDecimalUtil.isMenorOrIgualACero(cantidadMaximaPromocion)) {
				linea.recalcularImporteFinal();
				i++;
				importeTotalAhorro = importeTotalAhorro.add(importeTotalAhorroLinea);
				importeTotalAhorroLinea = BigDecimal.ZERO;
				continue;
			}

			if (!aplicacionesTramo.containsKey(linea.getCodArticulo())) {
				aplicacionesTramo.put(linea.getCodArticulo(), BigDecimal.ZERO);
			}

			// Comprobamos si podemos seguir aplicando ese tramo
			ArticuloRegaloTramoBonificacion articuloTramo = tramo.getArticulo(linea.getCodArticulo());
			if (articuloTramo==null ||BigDecimalUtil.isMenorOrIgual(articuloTramo.getCantidad(), aplicacionesTramo.get(linea.getCodArticulo()))) {
				i++;
				importeTotalAhorro = importeTotalAhorro.add(importeTotalAhorroLinea);
				importeTotalAhorroLinea = BigDecimal.ZERO;
				continue;
			}
							
			BigDecimal precioSinDto = linea.getPrecioAplicacionPromocion();
			BigDecimal precioNuevo = articuloTramo.getPrecio();
			BigDecimal ahorro = BigDecimalUtil.isMayor(precioSinDto, precioNuevo) ? precioSinDto.subtract(precioNuevo) : precioSinDto;
			
			String codArt = linea.getCodArticulo();
			if(ahorroEnArticulo.get(codArt)==null){
				ahorroEnArticulo.put(codArt, BigDecimal.ZERO);
			}
			
			ahorroEnArticulo.put(codArt, ahorroEnArticulo.get(codArt).add(ahorro));
			importeTotalAhorroLinea = importeTotalAhorroLinea.add(ahorro);
			PromocionLineaTicket promocionLinea = linea.getPromocion(promocionTicket.getIdPromocion());
			if (promocionLinea == null) {
				promocionLinea = new PromocionLineaTicket(promocionTicket);
				linea.addPromocion(promocionLinea);
			}

			promocionLinea.setImporteTotalDto(importeTotalAhorroLinea);
			promocionLinea.addCantidadPromocion(BigDecimal.ONE); // Se añade una promoción a la linea

			linea.addCantidadPromocion(BigDecimal.ONE);
			linea.recalcularImporteFinal();
			aplicacionesTramo.put(linea.getCodArticulo(), aplicacionesTramo.get(codArt).add(BigDecimal.ONE));

			cantidadMaximaPromocion = cantidadMaximaPromocion.subtract(BigDecimal.ONE);
		}
		
		if(BigDecimalUtil.isMayorACero(importeTotalAhorro)){
			promocionTicket.setImporteTotalAhorro(importeTotalAhorro);
			documento.addPromocion(promocionTicket);
		}
	}


	private boolean estaAplicada(DocumentoPromocionable documento) {
		boolean res = false;
	    for(LineaDocumentoPromocionable linea:documento.getLineasDocumentoPromocionable()){
	    	if(linea.getPromocion(this.getIdPromocion())!=null){
	    		res = true;
	    		break;
	    	}
	    }
	    return res;
    }

	@Override
	public void leerDatosPromocion(byte[] datosPromocion) {
		try {
			XMLDocument xmlPromocion = new XMLDocument(datosPromocion);
			condiciones = new CondicionPrincipalPromoBean(xmlPromocion.getNodo("condicionLineas"));

			XMLDocumentNode apl = xmlPromocion.getNodo("aplicacion", true);
			if (apl != null) {
				XMLDocumentNode tramos = apl.getNodo("tramos");
				this.tramos = new ArrayList<TramoBonificacion>();
				for (XMLDocumentNode tramo : tramos.getHijos("tramo")) {
					TramoBonificacion tramoBonificacion = new TramoBonificacion(tramo);
					for (XMLDocumentNode articuloNode : tramo.getNodo("articulos").getHijos("articulo")) {
	                    ArticuloRegaloTramoBonificacion articuloRegalo = new ArticuloRegaloTramoBonificacion(articuloNode);
	                    BigDecimal precio = articuloNode.getNodo("precio").getValueAsBigDecimal();
	                    articuloRegalo.setPrecio(precio);
	                    tramoBonificacion.getArticulos().add(articuloRegalo);
	                }
	                this.tramos.add(tramoBonificacion);
				}
			}
		}
		catch (XMLDocumentException e) {
			log.error("Error al leer los datos de la promoción de tipo puntos: " + e.getMessage(), e);
		}

	}

	protected LineasAplicablesPromoBean crearLineasAplicables(DocumentoPromocionable documento, LineasAplicablesPromoBean lineasCondicion) {
		LineasAplicablesPromoBean aplicables = SpringContext.getBean(LineasAplicablesPromoBean.class);
		for(TramoBonificacion tramo:getTramosOrdenados(lineasCondicion.getLineasAplicables())){
			if (tramo != null) {
				aplicables.setFiltroPromoExclusiva(false);
				//Iteramos sobre los articulos del tramo
				for (ArticuloRegaloTramoBonificacion art : tramo.getArticulos()) {
					LineasAplicablesPromoBean lineasArt = SpringContext.getBean(LineasAplicablesPromoBean.class);
					List<LineaDocumentoPromocionable> lineas = new ArrayList<LineaDocumentoPromocionable>();
					//Buscamos las lineas de ese artículo en el documento
					for (LineaDocumentoPromocionable linea : documento.getLineasDocumentoPromocionable()) {
						if (linea.getArticulo().getCodArticulo().equals(art.getCodArticulo()) && !aplicables.getLineasAplicables().contains(linea)) {
							lineas.add(linea);
						}
					}
					lineasArt.setLineasAplicables(lineas);
					//Compruebo si tengo suficiente cantidad de articulos para aplicar el tramo en ese artículo
					if(BigDecimalUtil.isMayorOrIgual(lineasArt.getCantidadArticulos(), art.getCantidad())){
						List<LineaDocumentoPromocionable> lineasAux = new ArrayList<LineaDocumentoPromocionable>(aplicables.getLineasAplicables());
						lineasAux.addAll(lineasArt.getLineasAplicables());
						aplicables.setLineasAplicables(lineasAux);
					}
					
					
				}
			}
		}
		return aplicables;
	}
	
	@SuppressWarnings("unchecked")
	private List<TramoBonificacion> getTramosOrdenados(List<LineaDocumentoPromocionable> lineasCondicion) {
	    Collections.sort(tramos, ComparatorUtils.reversedComparator(new ComparatorTramoBonificacion()));

	    List<TramoBonificacion> tramosQueCumplen = new ArrayList<TramoBonificacion>();

	    for (TramoBonificacion tramo : tramos) {
	        Map<String, BigDecimal> articulosRegalo = tramo.getArticulosRegalo(); // Mapa art -> posible cantidad a regalar
	        BigDecimal importeLineasValidas = BigDecimal.ZERO;

	        for (LineaDocumentoPromocionable linea : lineasCondicion) {
	            String codart = linea.getCodArticulo();

	            // Si el artículo no es de regalo, se incluye en el importe de las líneas válidas
//	            if (!articulosRegalo.containsKey(codart)) {
	                importeLineasValidas = importeLineasValidas.add(linea.getImporteAplicacionPromocionConDto());
//	            }
	        }

	        // Verificar si el importe de las líneas válidas cumple con el requisito del tramo
	        if (BigDecimalUtil.isMayorOrIgual(importeLineasValidas, tramo.getImporte())) {
	            tramosQueCumplen.add(tramo);
	        }
	    }

	    return tramosQueCumplen;
	}
	
	@Override
	protected FiltroLineasPromocion createFiltroLineasPromocion(DocumentoPromocionable documento) {
		FiltroLineasPromocion filtro = new DinoFiltroLineasPromocionCabecera();
		filtro.setDocumento(documento);
		filtro.setFiltrarPromoExclusivas(false);
		return filtro;
	}
	
}
