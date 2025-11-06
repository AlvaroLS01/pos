package com.comerzzia.dinosol.pos.services.promociones.tipos.especificos.bonificacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.core.util.xml.XMLDocumentNodeNotFoundException;

public class TramoBonificacion {

	private BigDecimal importe;
	private String textoPromo;
	private boolean reglaOr;
	private List<ArticuloRegaloTramoBonificacion> articulos;

	public TramoBonificacion(XMLDocumentNode tramo) throws XMLDocumentNodeNotFoundException {
		importe = tramo.getNodo("importeTramo").getValueAsBigDecimal();
		textoPromo = tramo.getNodo("texto_promo").getValue();
		articulos = new ArrayList<ArticuloRegaloTramoBonificacion>();
		reglaOr = tramo.getNodo("regla", true) != null && tramo.getNodo("regla").getValue() != null && tramo.getNodo("regla").getValue().toLowerCase().equals("or");
		for (XMLDocumentNode nodo : tramo.getNodo("articulos").getHijos("articulo")) {
			articulos.add(new ArticuloRegaloTramoBonificacion(nodo));
		}

	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public String getTextoPromo() {
		return textoPromo;
	}

	public void setTextoPromo(String textoPromo) {
		this.textoPromo = textoPromo;
	}

	public List<ArticuloRegaloTramoBonificacion> getArticulos() {
		return articulos;
	}

	public void setArticulos(List<ArticuloRegaloTramoBonificacion> articulos) {
		this.articulos = articulos;
	}

	public ArticuloRegaloTramoBonificacion getArticulo(String codArt) {
		ArticuloRegaloTramoBonificacion res = null;
		for (ArticuloRegaloTramoBonificacion art : getArticulos()) {
			if (art.getCodArticulo().equals(codArt)) {
				res = art;
				break;
			}
		}
		return res;
	}

	public boolean isReglaOr() {
		return reglaOr;
	}

	public void setReglaOr(boolean reglaOr) {
		this.reglaOr = reglaOr;
	}
	
	public Map<String, BigDecimal> getArticulosRegalo() {
		Map<String, BigDecimal> articulosRegalo = new HashMap<String, BigDecimal>();
		for(ArticuloRegaloTramoBonificacion regalo : articulos) {
			articulosRegalo.put(regalo.getCodArticulo(), regalo.getCantidad());
		}
		return articulosRegalo;
	}

}
