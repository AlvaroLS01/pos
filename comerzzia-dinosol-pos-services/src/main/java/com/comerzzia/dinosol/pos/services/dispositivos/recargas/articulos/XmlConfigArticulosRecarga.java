package com.comerzzia.dinosol.pos.services.dispositivos.recargas.articulos;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "articulos_recargas")
@XmlAccessorType(XmlAccessType.NONE)
public class XmlConfigArticulosRecarga {

	@XmlElementWrapper(name = "recarga_movil")
	@XmlElement(name = "articulo")
	private List<String> articulosRecargaMovil;

	@XmlElementWrapper(name = "posa_card")
	@XmlElement(name = "articulo")
	private List<String> articulosPosaCard;

	@XmlElementWrapper(name = "pin_printing")
	@XmlElement(name = "articulo")
	private List<String> articulosPinPrinting;

	public List<String> getArticulosRecargaMovil() {
		return articulosRecargaMovil;
	}

	public void setArticulosRecargaMovil(List<String> articulosRecargaMovil) {
		this.articulosRecargaMovil = articulosRecargaMovil;
	}

	public List<String> getArticulosPosaCard() {
		return articulosPosaCard;
	}

	public void setArticulosPosaCard(List<String> articulosPosaCard) {
		this.articulosPosaCard = articulosPosaCard;
	}

	public List<String> getArticulosPinPrinting() {
		return articulosPinPrinting;
	}

	public void setArticulosPinPrinting(List<String> articulosPinPrinting) {
		this.articulosPinPrinting = articulosPinPrinting;
	}
	
	public List<String> getArticulos() {
		List<String> codigos = new ArrayList<String>();
		codigos.addAll(articulosRecargaMovil);
		codigos.addAll(articulosPinPrinting);
		codigos.addAll(articulosPosaCard);
		return codigos;
	}

}
