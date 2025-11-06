package com.comerzzia.dinosol.pos.services.promociones.tipos.especificos.detalles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.comerzzia.core.util.numeros.Numero;
import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.core.util.xml.XMLDocumentException;
import com.comerzzia.pos.persistence.promociones.detalle.PromocionDetalleBean;
import com.comerzzia.pos.services.promociones.Promocion;
import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.DetallePromocion;

public class DetallePromocionRascaBoletos extends DetallePromocion {

	private boolean xmlLeido;
	private BigDecimal cantidad;
	private Integer puntosAgrupacion;
	private String textoPromocion;
	protected final List<LineaDetallePromocionRascaBoletos> lineasAgrupacion;

	public DetallePromocionRascaBoletos(Promocion promocion, PromocionDetalleBean detalle) throws XMLDocumentException {
		super(promocion, detalle);
		lineasAgrupacion = new ArrayList<>();
        lineasAgrupacion.add(new LineaDetallePromocionRascaBoletos(getCodArticulo(), getDesglose1(), getDesglose2()));
	}

	public void leerXml() throws XMLDocumentException {
		XMLDocument xml = new XMLDocument(detalle.getDatosPromocion());

		cantidad = xml.getNodo("cantidad").getValueAsBigDecimal();
		puntosAgrupacion = Numero.desformateaInteger(xml.getNodo("puntosAgrupacion").getValue(), null);
		textoPromocion = xml.getNodo("texto_promo").getValue();    

		xmlLeido = true;
	}

	public boolean isXmlLeido() {
		return xmlLeido;
	}

	public void setXmlLeido(boolean xmlLeido) {
		this.xmlLeido = xmlLeido;
	}

	public BigDecimal getCantidad() {
		return cantidad;
	}

	public void setCantidad(BigDecimal cantidad) {
		this.cantidad = cantidad;
	}

	public Integer getPuntosAgrupacion() {
		return puntosAgrupacion;
	}

	public void setPuntosAgrupacion(Integer puntosAgrupacion) {
		this.puntosAgrupacion = puntosAgrupacion;
	}

	public String getTextoPromocion() {
		return textoPromocion;
	}

	public void setTextoPromocion(String textoPromocion) {
		this.textoPromocion = textoPromocion;
	}
    public List<LineaDetallePromocionRascaBoletos> getLineasAgrupacion() {
    	return lineasAgrupacion;
    }
	
	

}
