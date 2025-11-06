package com.comerzzia.dinosol.pos.services.promociones.opciones;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OpcionPromocionesDto {

	private String titulo;

	private List<String> promociones;

	private String textoTicket;

	public OpcionPromocionesDto() {
		super();
		this.promociones = new ArrayList<String>();
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public List<String> getPromociones() {
		return promociones;
	}

	public void addPromocion(String promocion) {
		this.promociones.add(promocion);
	}

	public String getTextoTicket() {
		return textoTicket;
	}

	public void setTextoTicket(String textoTicket) {
		this.textoTicket = textoTicket;
	}

	@Override
	public String toString() {
		return "Opción: " + titulo + ". Promociones SAP: (" + promociones + ")";
	}

	@Override
	public int hashCode() {
		return Objects.hash(titulo);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OpcionPromocionesDto other = (OpcionPromocionesDto) obj;
		return Objects.equals(titulo, other.titulo);
	}

}
