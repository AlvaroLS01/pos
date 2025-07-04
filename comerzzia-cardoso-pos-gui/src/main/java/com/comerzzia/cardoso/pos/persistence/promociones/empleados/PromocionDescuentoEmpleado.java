package com.comerzzia.cardoso.pos.persistence.promociones.empleados;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.xml.XMLDocumentException;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.tipos.PromocionCabecera;

/**
 * GAP - PERSONALIZACIONES V3 - PROMOCIÓN EMPLEADO
 */

@Component
@Primary
public class PromocionDescuentoEmpleado extends PromocionCabecera {

	@Override
	public void leerDatosPromocion(byte[] datosPromocion) {
		
	}

	@Override
	public boolean aplicarPromocion(DocumentoPromocionable documento) {
		return false;
	}
	
	@Override
	public void leerCondicionesCabecera(byte[] datosPromocion) throws XMLDocumentException {
		// TODO Si se hace el super se genera un error : El nodo DescuentoGeneral no contiene el nodo condicionCabecera
		//super.leerCondicionesCabecera(datosPromocion);
	}

}
