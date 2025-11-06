package com.comerzzia.dinosol.pos.services.promociones.tipos.especificos;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.dinosol.pos.services.promociones.filtros.DinoFiltroLineasPromocionCabecera;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.FiltroLineasPromocion;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionTextoBean;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;

@Component
@Scope("prototype")
@Primary
public class DinoPromocionTextoBean extends PromocionTextoBean {

	protected boolean visibleCajero;

	@Override
	public void leerDatosPromocion(byte[] datosPromocion) {
		super.leerDatosPromocion(datosPromocion);
		try {
			XMLDocument xmlPromocion = new XMLDocument(datosPromocion);
			XMLDocumentNode nodoVisibleCajero = xmlPromocion.getNodo("visibleCajero", true);
			if (nodoVisibleCajero != null) {
				setVisibleCajero(nodoVisibleCajero.getValueAsBoolean());
			}
		}
		catch (Exception e) {
			log.error("leerDatosPromocion() - Ha habido un error al leer la promoción: " + e.getMessage(), e);
		}

	}
	
	public boolean isVisibleCajero() {
		return visibleCajero;
	}

	public void setVisibleCajero(boolean visibleCajero) {
		this.visibleCajero = visibleCajero;
	}

	@Override
	public boolean isAplicacionFinal() {
		return !visibleCajero;
	}

	@Override
	public boolean isAplicacionCabecera() {
		return visibleCajero;
	}
	
	@Override
	protected PromocionTicket createPromocionTicket(String cupon) {
		PromocionTicket promocionTicket = super.createPromocionTicket(cupon);
		
		if(visibleCajero) {
			promocionTicket.setAcceso("CAJERO");
		}
		
		return promocionTicket;
	}

	@Override
	protected FiltroLineasPromocion createFiltroLineasPromocion(DocumentoPromocionable documento) {
		FiltroLineasPromocion filtro = new DinoFiltroLineasPromocionCabecera();
		filtro.setDocumento(documento);
		filtro.setFiltrarPromoExclusivas(false);
		return filtro;
	}

}
