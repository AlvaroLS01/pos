package com.comerzzia.dinosol.pos.services.promociones.tipos.especificos;

import java.util.Date;
import java.util.List;

import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.escalado.TramoEscalado;

public class DinoLineaDetallePromocionEscalado {

	protected final String codArticulo;
	protected final String desglose1;
	protected final String desglose2;
	protected final List<TramoEscalado> tramos;
	protected final String tipoDto;
    protected final Date fechaInicio;
    protected final Date fechaFin;
    
	public DinoLineaDetallePromocionEscalado(String codArticulo, String desglose1, String desglose2, String tipoDto, List<TramoEscalado> tramos, Date fechaInicio, Date fechaFin) {
		super();
		this.codArticulo = codArticulo;
		this.desglose1 = desglose1;
		this.desglose2 = desglose2;
		this.tipoDto = tipoDto;
		this.tramos = tramos;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
	}

	public String getCodArticulo() {
		return codArticulo;
	}

	public String getDesglose1() {
		return desglose1;
	}

	public String getDesglose2() {
		return desglose2;
	}

	public String getTipoDto() {
		return tipoDto;
	}

	public List<TramoEscalado> getTramos(){
		return tramos;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}
	
}
