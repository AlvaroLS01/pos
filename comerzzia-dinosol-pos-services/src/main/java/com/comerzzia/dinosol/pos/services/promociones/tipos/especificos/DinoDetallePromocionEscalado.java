package com.comerzzia.dinosol.pos.services.promociones.tipos.especificos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import com.comerzzia.core.util.xml.XMLDocumentException;
import com.comerzzia.pos.persistence.promociones.detalle.PromocionDetalleBean;
import com.comerzzia.pos.services.promociones.Promocion;
import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.escalado.DetallePromocionEscalado;

public class DinoDetallePromocionEscalado extends DetallePromocionEscalado{
	
	protected final List<DinoLineaDetallePromocionEscalado> lineasAgrupacion;

	public DinoDetallePromocionEscalado(Promocion promocion, PromocionDetalleBean detalle) throws XMLDocumentException {
		super(promocion, detalle);
		lineasAgrupacion = new ArrayList<>();
        lineasAgrupacion.add(new DinoLineaDetallePromocionEscalado(getCodArticulo(), getDesglose1(), getDesglose2(), getTipoDto(), getTramos(), detalle.getFechaInicio(), detalle.getFechaFin()));
	}

	public List<DinoLineaDetallePromocionEscalado> getLineasAgrupacion() {
        return lineasAgrupacion;
    }
	
	public DinoLineaDetallePromocionEscalado getLineaAgrupacion(Calendar cal, String codArt) {
    	for (DinoLineaDetallePromocionEscalado lineaDetallePromocionEscalado : getLineasAgrupacion(cal)) {
			if (lineaDetallePromocionEscalado.getCodArticulo().equals(codArt)) {
				return lineaDetallePromocionEscalado;
			}
		}
    	return null;
    }

	public List<DinoLineaDetallePromocionEscalado> getLineasAgrupacion(Calendar cal) {
		List<DinoLineaDetallePromocionEscalado> list = new LinkedList<>();
		Calendar calInicio = Calendar.getInstance();
		Calendar calFin = Calendar.getInstance();
		for (DinoLineaDetallePromocionEscalado lineaDetallePromocionEscalado : getLineasAgrupacion()) {
			calInicio.setTime(lineaDetallePromocionEscalado.getFechaInicio());
			calFin.setTime(lineaDetallePromocionEscalado.getFechaFin());
			if ((cal.compareTo(calInicio) >= 0) && (cal.compareTo(calFin) <= 0)) {
				list.add(lineaDetallePromocionEscalado);
			}
		}
		return list;
	}
}
