package com.comerzzia.iskaypet.pos.services.ticket.lineas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.core.util.xml.XMLDocumentException;
import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.pos.persistence.promociones.PromocionBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.promociones.PromocionesService;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.promociones.tipos.componente.CondicionPrincipalPromoBean;
import com.comerzzia.pos.services.promociones.tipos.componente.GrupoComponentePromoBean;
import com.comerzzia.pos.services.promociones.tipos.componente.IComponentePromoBean;
import com.comerzzia.pos.services.promociones.tipos.componente.ItemComponentePromoBean;

@Service
public class DtoPromocionesService {

	protected Logger log = Logger.getLogger(DtoPromocionesService.class);

	@Autowired
	protected Sesion sesion;
	@Autowired
	protected PromocionesService promocionesService;

	protected GrupoComponentePromoBean aplicacion;
	protected CondicionPrincipalPromoBean condiciones;

	protected String tipoDescuento;
	protected BigDecimal precioPorcentaje;
	protected BigDecimal cantidadAplicacion;
	protected BigDecimal cantidadCondicion;
	protected String textoPromocion;

	protected List<String> codArtArticulosCondidion = new ArrayList<String>();

	public void cargarPromocion(Long idPromocion) {
		try {
			codArtArticulosCondidion.clear();
			PromocionBean promoCombiLinea = promocionesService.getPromotion(idPromocion);
			leerDatosPromocion(promoCombiLinea.getDatosPromocion());
			cargarArticulolosCondicionPricipal();
			condiciones.getReglas();
		} catch (PromocionesServiceException e) {

		}
	}

	private void cargarArticulolosCondicionPricipal() {
		for (IComponentePromoBean condicion : condiciones.getReglas()) {
			if (condicion.isTipoGrupoReglas()) {
				GrupoComponentePromoBean grupo = (GrupoComponentePromoBean) condicion;
				for (ItemComponentePromoBean articuloCondicion : grupo.getReglas())
					codArtArticulosCondidion.add(articuloCondicion.getValor());
			} else {
				ItemComponentePromoBean regla = (ItemComponentePromoBean) condicion;
				if (regla.isReglaCantidadMultiple()) {
					cantidadCondicion = regla.getValorAsBigDecimal();
				}
			}
		}
	}

	public void leerDatosPromocion(byte[] datosPromocion) {
		try {
			XMLDocument xmlPromocion = new XMLDocument(datosPromocion);

			condiciones = (new CondicionPrincipalPromoBean(xmlPromocion.getNodo("condicionLineas")));
			aplicacion = new GrupoComponentePromoBean(xmlPromocion.getNodo("aplicacion"));

			setTipoDescuento(xmlPromocion.getNodo("tipoFiltro").getValue());
			BigDecimal precioDescuentoFormat = BigDecimal.ZERO;
			try {

				precioDescuentoFormat = xmlPromocion.getNodo("precioDescuento").getValueAsBigDecimal();
			} catch (NumberFormatException e) {
				String strPrecioDescuentoFormat = xmlPromocion.getNodo("precioDescuento").getValue();
				precioDescuentoFormat = new BigDecimal(strPrecioDescuentoFormat.replace(",", "."));
			}
			setPrecioPorcentaje(precioDescuentoFormat);

			String cantidadTipo = xmlPromocion.getNodo("cantidadTipo").getValue();
			if (cantidadTipo == null || cantidadTipo.isEmpty()) {
				setCantidadAplicacion(BigDecimal.ZERO);
			} else {
				setCantidadAplicacion(new BigDecimal(cantidadTipo));
			}

			String storeLanguageCode = sesion.getAplicacion().getStoreLanguageCode();

			String textPromo = null;
			String textPromoDefault = null;
			List<XMLDocumentNode> textPromoNodes = xmlPromocion.getNodos("textoPromocion");
			for (XMLDocumentNode textPromoNode : textPromoNodes) {
				String textPromoLanguageCode = textPromoNode.getAtributoValue("lang", true);
				if (StringUtils.isNotBlank(textPromoLanguageCode)) {
					if (textPromoLanguageCode.equals(storeLanguageCode)) {
						textPromo = textPromoNode.getValue();
						break;
					}
				} else {
					textPromoDefault = textPromoNode.getValue();
				}
			}

			if (StringUtils.isBlank(textPromo)) {
				textPromo = textPromoDefault;
			}

			setTextoPromocion(textPromo);
		} catch (XMLDocumentException e) {
			log.error("Error al leer los datos de la promoción de tipo descuento  línea: " + e.getMessage(), e);
		}
	}

	public GrupoComponentePromoBean getAplicacion() {
		return aplicacion;
	}

	public void setAplicacion(GrupoComponentePromoBean aplicacion) {
		this.aplicacion = aplicacion;
	}

	public CondicionPrincipalPromoBean getCondiciones() {
		return condiciones;
	}

	public void setCondiciones(CondicionPrincipalPromoBean condiciones) {
		this.condiciones = condiciones;
	}

	public String getTipoDescuento() {
		return tipoDescuento;
	}

	public void setTipoDescuento(String tipoDescuento) {
		this.tipoDescuento = tipoDescuento;
	}

	public BigDecimal getPrecioPorcentaje() {
		return precioPorcentaje;
	}

	public void setPrecioPorcentaje(BigDecimal precioPorcentaje) {
		this.precioPorcentaje = precioPorcentaje;
	}

	public BigDecimal getCantidadAplicacion() {
		return cantidadAplicacion;
	}

	public void setCantidadAplicacion(BigDecimal cantidadAplicacion) {
		this.cantidadAplicacion = cantidadAplicacion;
	}

	public String getTextoPromocion() {
		return textoPromocion;
	}

	public void setTextoPromocion(String textoPromocion) {
		this.textoPromocion = textoPromocion;
	}

	public List<String> getCodArtArticulosCondidion() {
		return codArtArticulosCondidion;
	}

	public void setCodArtArticulosCondidion(List<String> codArtArticulosCondidion) {
		this.codArtArticulosCondidion = codArtArticulosCondidion;
	}

	public BigDecimal getCantidadCondicion() {
		return cantidadCondicion;
	}

	public void setCantidadCondicion(BigDecimal cantidadCondicion) {
		this.cantidadCondicion = cantidadCondicion;
	}

}
