/**
 * ComerZZia 3.0 Copyright (c) 2008-2015 Comerzzia, S.L. All Rights Reserved. THIS WORK IS SUBJECT TO SPAIN AND
 * INTERNATIONAL COPYRIGHT LAWS AND TREATIES. NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED COPIED,
 * DISTRIBUTED, REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED, LINKED, RECAST,
 * TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION OF THIS WORK
 * WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. CONSULT THE END USER LICENSE
 * AGREEMENT FOR INFORMATION ON ADDITIONAL RESTRICTIONS.
 */
package com.comerzzia.dinosol.pos.services.promociones.tipos.especificos;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.services.core.sesion.DinoSesionPromociones;
import com.comerzzia.dinosol.pos.services.ticket.cupones.DinoCuponEmitidoTicket;
import com.comerzzia.pos.services.cupones.CuponGeneradoDto;
import com.comerzzia.pos.services.cupones.CuponesServiceException;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionGeneracionCuponesBean;
import com.comerzzia.pos.services.ticket.cupones.CuponEmitidoTicket;
import com.comerzzia.pos.util.format.FormatUtil;

@Component
@Scope("prototype")
@Primary
public class DinoPromocionGeneracionCuponesBean extends PromocionGeneracionCuponesBean {
	
	@Autowired
	private DinoSesionPromociones sesionPromociones;

	@Override
	public CuponEmitidoTicket generaCupon(DocumentoPromocionable documento) throws CuponesServiceException {
		CuponEmitidoTicket cupon = crearCupon(documento);
		BigDecimal importe = cupon.getImporteCupon();
		if (importe != null) {
			String res = cupon.getDescripcionCupon();
			res = res.replaceAll("#IMPORTE#", FormatUtil.getInstance().formateaImporte(importe));
			cupon.setDescripcionCupon(res);
		}
		return cupon;
	}

	public CuponEmitidoTicket crearCupon(DocumentoPromocionable documento) throws CuponesServiceException {
		CuponEmitidoTicket cupon = new DinoCuponEmitidoTicket();
		cupon.setTituloCupon(getTitCupon());
		cupon.setDescripcionCupon(getDesCupon());
		cupon.setIdPromocionOrigen(getIdPromocion());
		cupon.setIdPromocionAplicacion(getIdPromoAplicar());
		CuponGeneradoDto cuponGenerado = generaCodigoCupon(documento);
		cupon.setCodigoCupon(cuponGenerado.getCodigoCupon());
		cupon.setImporteCupon(cuponGenerado.getImporteCupon());
		
		try {
			Date fechaFin = sesionPromociones.obtenerCuponFechaFin(datosCupon.getIdPromocionAplicacion());
			((DinoCuponEmitidoTicket) cupon).setFechaFin(fechaFin);
		}
		catch (Exception e) {
			log.error("generarCuponesDtoFuturo() - Error obteniendo fecha de fin del cupon: " + e.getMessage(), e);
		}
		
		return cupon;
	}

}
