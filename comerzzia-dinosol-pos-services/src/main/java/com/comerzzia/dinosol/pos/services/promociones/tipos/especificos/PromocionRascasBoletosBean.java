package com.comerzzia.dinosol.pos.services.promociones.tipos.especificos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.core.util.xml.XMLDocumentException;
import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.dinosol.pos.services.promociones.filtros.DinoFiltroLineasPromocionCabecera;
import com.comerzzia.dinosol.pos.services.promociones.tipos.especificos.detalles.DetallePromocionRascaBoletos;
import com.comerzzia.dinosol.pos.services.promociones.tipos.especificos.detalles.LineaDetallePromocionRascaBoletos;
import com.comerzzia.pos.persistence.promociones.detalle.PromocionDetalleBean;
import com.comerzzia.pos.persistence.promociones.detalle.PromocionDetalleKey;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.PromocionesService;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.promociones.filtro.FiltroLineasPromocion;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.promociones.tipos.PromocionCabecera;
import com.comerzzia.pos.services.promociones.tipos.componente.CondicionPrincipalPromoBean;
import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.DetallePromocion;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

@Component
@Scope("prototype")
public class PromocionRascasBoletosBean extends PromocionCabecera {

	protected CondicionPrincipalPromoBean condiciones;
	protected Map<PromocionDetalleKey, DetallePromocion> detallesArticulos;

	protected BigDecimal importeTramo;
	protected BigDecimal puntosTramo;
	protected List<DetallePromocionRascaBoletos> detalles;
	protected Boolean aplicacionMultiple;
	
	private Long idSorteo;
	
	@Autowired
	protected PromocionesService promocionesService;

	@Override
	public void leerDatosPromocion(byte[] datosPromocion) {
		try {
			XMLDocument xmlPromocion = new XMLDocument(datosPromocion);
			condiciones = new CondicionPrincipalPromoBean(xmlPromocion.getNodo("condicionLineas"));
			setDetalles();

			XMLDocumentNode apl = xmlPromocion.getNodo("aplicacion", true);
			if (apl != null) {
				XMLDocumentNode tramos = apl.getNodo("tramo");
				setPuntosTramo(tramos.getNodo("puntosTramo").getValueAsBigDecimal());
				setImporteTramo(tramos.getNodo("importeTramo").getValueAsBigDecimal());
				setTextoPromocion(tramos.getNodo("texto_promo").getValue());
				XMLDocumentNode nodo = apl.getNodo("aplicacionmultiplepuntosextra",true);
				setAplicacionMultiple(nodo!=null && "S".equals(nodo.getValue()));
			}
			
			leerExtensiones(datosPromocion);
			
			if(extensiones.containsKey("SORTEO")) {
				String sorteoString = extensiones.get("SORTEO");
				idSorteo = new Long(sorteoString);
			}
		}
		catch (XMLDocumentException e) {
			log.error("Error al leer los datos de la promoción de tipo puntos: " + e.getMessage(), e);
		}
        catch (PromocionesServiceException e) {
        	throw new RuntimeException(e);
        }
	}


	public void setDetalles() throws XMLDocumentException, PromocionesServiceException {
		List<PromocionDetalleBean> detallesBean = promocionesService.consultarDetallesPromocion(getIdPromocion());
		
		Map<Long, DetallePromocionRascaBoletos> mapaDetalles = new HashMap<>(); // Map de IdAgrupacion -> Agrupacion
		detallesArticulos = new HashMap<>(); // Map de codArt -> Agrupación
		for (PromocionDetalleBean promocionDetalleBean : detallesBean) {
			DetallePromocionRascaBoletos detalle = mapaDetalles.get(promocionDetalleBean.getIdAgrupacion());
			if (detalle == null) {
				detalle = new DetallePromocionRascaBoletos(this, promocionDetalleBean);
				mapaDetalles.put(promocionDetalleBean.getIdAgrupacion(), detalle);
			}
			else {
				detalle.getLineasAgrupacion().add(createLineaPromocion(promocionDetalleBean));
			}
			PromocionDetalleKey key = new PromocionDetalleKey();
			key.setCodArticulo(promocionDetalleBean.getCodArticulo());
			key.setDesglose1(promocionDetalleBean.getDesglose1());
			key.setDesglose2(promocionDetalleBean.getDesglose2());
			detallesArticulos.put(key, detalle);
		}
		detalles = new ArrayList<>(mapaDetalles.values()); // Lista de agrupaciones
	}

	public DetallePromocionRascaBoletos getDetallePromocionRascaBoletos(String codArticulo, String desglose1, String desglose2) {
		PromocionDetalleKey key = new PromocionDetalleKey();
		key.setCodArticulo(codArticulo);
		key.setDesglose1(desglose1);
		key.setDesglose2(desglose2);
		DetallePromocionRascaBoletos det = (DetallePromocionRascaBoletos) getDetalle(detallesArticulos, key);
		if (det != null && !det.isXmlLeido()) {
			try {
				det.leerXml();
			}
			catch (XMLDocumentException e) {
				log.error("createDetallePromocion() - Ha habido un error al leer el XML del detalle de la promoción: " + e.getMessage(), e);
				det = null;
			}
		}
		return det;
	}

	public DetallePromocion getDetalle(Map<PromocionDetalleKey, DetallePromocion> detalles, PromocionDetalleKey key) {
		// Buscamos la promoción con desgloses más restrictivos, siendo la promoción con desgloses "*" la menos restrictiva
		// Se itera por cada caso puesto que las promociones no vienen ordenadas por desgloses
		for (PromocionDetalleKey detalleKey : detalles.keySet()) {
			if (detalleKey.getCodArticulo().equals(key.getCodArticulo()) && key.getDesglose1().equals(detalleKey.getDesglose1()) && key.getDesglose2().equals(detalleKey.getDesglose2())) {
				return detalles.get(detalleKey);
			}
		}
		for (PromocionDetalleKey detalleKey : detalles.keySet()) {
			if (detalleKey.getCodArticulo().equals(key.getCodArticulo()) && "*".equals(detalleKey.getDesglose1()) && key.getDesglose2().equals(detalleKey.getDesglose2())) {
				return detalles.get(detalleKey);
			}
		}
		for (PromocionDetalleKey detalleKey : detalles.keySet()) {
			if (detalleKey.getCodArticulo().equals(key.getCodArticulo()) && key.getDesglose1().equals(detalleKey.getDesglose1()) && "*".equals(detalleKey.getDesglose2())) {
				return detalles.get(detalleKey);
			}
		}
		for (PromocionDetalleKey detalleKey : detalles.keySet()) {
			if (detalleKey.getCodArticulo().equals(key.getCodArticulo()) && "*".equals(detalleKey.getDesglose1()) && "*".equals(detalleKey.getDesglose2())) {
				return detalles.get(detalleKey);
			}
		}
		return null;
	}

	// Aplicación al documento final según las condiciones y aplicación de cabecera
	@Override
	public boolean aplicarPromocion(DocumentoPromocionable documento) {
		log.debug("aplicarPromocion() - " + this);
		List<LineaDocumentoPromocionable> lineasDetalle = documento.getLineasDocumentoPromocionable();
		Integer puntosCabecera = 0;
		Integer puntosDetalle = 0;
		
		// Obtenemos las líneas aplicables según el filtro configurado
		FiltroLineasPromocion filtroLineas = createFiltroLineasPromocion(documento);
		filtroLineas.setFiltrarPromoExclusivas(false); // Da igual que las líneas tengan una promoción exclusiva
		LineasAplicablesPromoBean lineasAplicables = filtroLineas.getNumCombosCondicion(condiciones);

		//Si no superamos el primer tramo no aplicamos puntos ni sobre detalles ni sobre importe
		if(BigDecimalUtil.isMenor(lineasAplicables.getImporteLineasConDto(), importeTramo)){
			log.debug(this + " aplicarPromocion() - La promoción no se puede aplicar porque no se supera el importe del primer tramo. ");
			return false;
		}
		
		puntosDetalle = calculaPuntosEnDetalle(lineasDetalle);
		
		if (puntosTramo == null || puntosTramo.equals(BigDecimal.ZERO)) {
			log.debug(this + " aplicarPromocion() - La promoción por tramos no se puede aplicar porque los puntos tramo configurados no lo permiten: " + puntosTramo);
		}else if(lineasAplicables.isEmpty()){			
			log.debug(this + " aplicarPromocion() - La promoción no se puede aplicar por no existir líneas aplicables en el documento .");
		}else{
			puntosCabecera = (lineasAplicables.getImporteLineasConDto().divide(importeTramo,0, BigDecimal.ROUND_DOWN)).multiply(puntosTramo).intValue() ;
		}
		
		// Aplicamos la promoción sobre el documento
		Integer totptos = puntosDetalle+puntosCabecera;

		if (totptos == 0) {
			log.debug(this + " aplicarPromocion() - La promoción no se puede aplicar porque los puntos obtenidos son cero. ");
			return false;
		}
		PromocionTicket promocionTicket = createPromocionTicket(codigoCupon);
		String res = promocionTicket.getTextoPromocion();
		res = res.replaceAll("#PUNTOSEXT#", puntosDetalle.toString());
		res = res.replaceAll("#PUNTOSNOR#", puntosCabecera.toString());
		res = res.replaceAll("#TOTPTOS#", totptos.toString());
		promocionTicket.setTextoPromocion(res);
		Map<String, Object> adicionales = new HashMap<String, Object>();
		adicionales.put("puntos_cabecera", puntosCabecera.toString());
		adicionales.put("puntos_lineas", puntosDetalle.toString());
		
		if(idSorteo != null) {
			adicionales.put("id_sorteo", idSorteo);
		}
		
		promocionTicket.setAdicionales(adicionales);
		documento.addPromocion(promocionTicket);

		return true;
	}

	private Integer calculaPuntosEnDetalle(List<LineaDocumentoPromocionable> lineasDetalle) {
		log.debug(this + " calculaPuntosEnDetalle() - Se calculan los puntos generados por los detalles del ticket.");
		List<String> agrupacionesAplicadas = new ArrayList<String>(); // Lista de agrupaciones ya aplicadas
		Integer puntosDetalle = 0;
		for (int i = 0; i < lineasDetalle.size();) {
			LineaDocumentoPromocionable linea = lineasDetalle.get(i);
			// Obtenemos a qué agrupación pertenece los codArticulos de este LineasAplicables
			String codArticulo = linea.getArticulo().getCodArticulo();
			String desglose1 = linea.getDesglose1();
			String desglose2 = linea.getDesglose2();
			DetallePromocionRascaBoletos detalle = getDetallePromocionRascaBoletos(codArticulo, desglose1, desglose2);
			if (detalle != null) {

				// Si esta agrupación ya ha sido aplicada, saltamos a la siguiente linea
				if (agrupacionesAplicadas.contains(detalle.getCodArticulo())) {
					i++;
					continue;
				}

				// Calculamos el nº de combinaciones de la agrupación que existen en el ticket
				BigDecimal cantidadArticuloTicket = getCantidadAgrupacionTicket(lineasDetalle, detalle.getCodArticulo());
				
				// Si la cantidad del artículo en el ticket es menor que la del detalle, no se aplica la promoción para ese artículo
				if(BigDecimalUtil.isMenor(cantidadArticuloTicket, detalle.getCantidad())) {
					i++;
					continue;
				}
				
				Integer numCombos = aplicacionMultiple?cantidadArticuloTicket.divide(detalle.getCantidad(), 1, RoundingMode.DOWN).intValue():1;
				if (numCombos < 1) {
					i++;
					continue;
				}

				// Comprobamos si la promoción puede seguir aplicándose en esta línea según la cantidad
				BigDecimal cantidadPromocion = BigDecimal.ZERO;
				for(PromocionLineaTicket promocionLinea : linea.getPromociones()) {
					if(promocionLinea.getIdPromocion().equals(this.getIdPromocion())) {
						cantidadPromocion = cantidadPromocion.subtract(promocionLinea.getCantidadPromocion());
					}
				}
				if (BigDecimalUtil.isMayorOrIgual(cantidadPromocion, linea.getCantidad())) {
					linea.recalcularImporteFinal();
					i++;
					continue;
				}

				// Calculamos los puntos generados en detalle
				puntosDetalle+=numCombos * detalle.getPuntosAgrupacion();
				agrupacionesAplicadas.add(detalle.getCodArticulo());
				i++;

			}
			else{
				i++;
			}
		}
	    return puntosDetalle;
    }


	protected BigDecimal getCantidadAgrupacionTicket(List<LineaDocumentoPromocionable> lineasAplicables, String codAgrupacion) {
		BigDecimal cantidad = BigDecimal.ZERO;

		for (LineaDocumentoPromocionable linea : lineasAplicables) {
			PromocionDetalleKey key = new PromocionDetalleKey();
			key.setCodArticulo(linea.getCodArticulo());
			key.setDesglose1(linea.getDesglose1());
			key.setDesglose2(linea.getDesglose2());
			// Si la agrupación de la linea es la pasada por parametro subo la cantidad de articulos en linea
			if (detallesArticulos.get(key)!=null && detallesArticulos.get(key).getCodArticulo().equals(codAgrupacion)) {
				BigDecimal cantidadLinea = linea.getCantidad();
				if(linea.getArticulo().getBalanzaTipoArticulo().equals("P")) {
					cantidadLinea = BigDecimalUtil.redondear(cantidadLinea, 0, RoundingMode.UP.ordinal());
				}
				cantidad = cantidad.add(cantidadLinea);
			}
		}

		return cantidad;
	}

	protected LineaDetallePromocionRascaBoletos createLineaPromocion(PromocionDetalleBean promocionDetalleBean) throws XMLDocumentException {
		String codArticulo = promocionDetalleBean.getCodArticulo();
		String desglose1 = promocionDetalleBean.getDesglose1();
		String desglose2 = promocionDetalleBean.getDesglose2();
		return new LineaDetallePromocionRascaBoletos(codArticulo, desglose1, desglose2);
	}

	@Override
	public boolean isAplicacionFinal() {
		return true;
	}
	
	@Override
	public boolean isAplicacionCabecera() {
		return false;
	}
    

	public CondicionPrincipalPromoBean getCondiciones() {
		return condiciones;
	}

	public void setCondiciones(CondicionPrincipalPromoBean condiciones) {
		this.condiciones = condiciones;
	}

	public BigDecimal getImporteTramo() {
		return importeTramo;
	}

	public void setImporteTramo(BigDecimal importeTramos) {
		this.importeTramo = importeTramos;
	}

	public BigDecimal getPuntosTramo() {
		return puntosTramo;
	}

	public void setPuntosTramo(BigDecimal puntosTramo) {
		this.puntosTramo = puntosTramo;
	}
	
    public Boolean getAplicacionMultiple() {
    	return aplicacionMultiple;
    }
	
    public void setAplicacionMultiple(Boolean aplicacionMultiple) {
    	this.aplicacionMultiple = aplicacionMultiple;
    }

	@Override
	protected FiltroLineasPromocion createFiltroLineasPromocion(DocumentoPromocionable documento) {
		FiltroLineasPromocion filtro = new DinoFiltroLineasPromocionCabecera();
		filtro.setDocumento(documento);
		filtro.setFiltrarPromoExclusivas(false);
		return filtro;
	}

}
