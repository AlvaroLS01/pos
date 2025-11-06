package com.comerzzia.dinosol.pos.services.promociones.tipos.especificos;

import java.math.BigDecimal;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.services.promociones.filtros.DinoFiltroLineasPromocionCabecera;
import com.comerzzia.dinosol.pos.services.promociones.filtros.DinoLineasAplicablesEditadasManualmentePromoBean;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.FiltroLineasPromocion;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionDescuentoCabeceraBean;
import com.comerzzia.pos.services.ticket.cupones.CuponAplicadoTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

@Component
@Primary
@Scope("prototype")
public class DinoPromocionDescuentoCabeceraBean extends PromocionDescuentoCabeceraBean {

	@Override
	protected FiltroLineasPromocion createFiltroLineasPromocion(DocumentoPromocionable documento) {
		FiltroLineasPromocion filtro = new DinoFiltroLineasPromocionCabecera();
		filtro.setDocumento(documento);
		filtro.setFiltrarPromoExclusivas(false);
		return filtro;
	}

	public boolean isPorcentajeSobrePVP() {
		return tipoFiltro.equals("Porcentaje sobre tarifa");
	}

	@Override
	public boolean aplicarPromocion(DocumentoPromocionable documento) {
		log.trace("aplicarPromocion() - " + this);
		// Obtenemos las líneas aplicables según el filtro configurado
		FiltroLineasPromocion filtroLineas = createFiltroLineasPromocion(documento);
		filtroLineas.setFiltrarPromoExclusivas(false); // Da igual que las líneas tengan una promoción exclusiva
		LineasAplicablesPromoBean lineasAplicables = filtroLineas.getNumCombosCondicion(condiciones);
		if (lineasAplicables.isEmpty()) {
			log.trace(this + " aplicarPromocion() - La promoción no se puede aplicar por no existir líneas aplicables en el documento .");
			return false;
		}

		lineasAplicables = filtroLineas.getLineasAplicablesGrupo(lineasAplicacion);
		if (lineasAplicables.isEmpty()) {
			log.trace(this + " aplicarPromocion() - La promoción no se puede aplicar por no existir líneas aplicables en el documento .");
			return false;
		}

		// Obtenemos el importe de descuento
		BigDecimal valorConfigurado = getImporteDescuento(lineasAplicables);

		if (valorConfigurado == null) {
			log.trace(this + " aplicarPromocion() - La promoción no se puede aplicar porque no se ha definido un intervalo de importe aplicable compatible. Importe: "
			        + lineasAplicables.getImporteLineasConDto());
			return false;
		}
		BigDecimal importeLineasConDto = lineasAplicables.getImporteLineasConDto();
		BigDecimal totalesPromocionesCabecera = BigDecimal.ZERO;
		for (LineaDocumentoPromocionable linea : lineasAplicables.getLineasAplicables()) {
			totalesPromocionesCabecera = totalesPromocionesCabecera.add(((LineaTicket) linea).getImporteTotalPromocionesMenosIngreso());
		}

		// Aplicamos la promoción sobre el ticket
		PromocionTicket promocionTicket = createPromocionTicket(codigoCupon);
		BigDecimal importePromocion = BigDecimal.ZERO;

		// Si el tipo de filtro es importe
		if (isImporte()) {
			BigDecimal importeDespuesPromociones = importeLineasConDto.subtract(totalesPromocionesCabecera);
			// A petición de Dinosol, si el importe del descuento es menor o igual que el total del ticket y es una promoción que genera un cupón,
			// el cupón se genera con el importe configurado en la promoción
			if (BigDecimalUtil.isMayorOrIgual(valorConfigurado, importeDespuesPromociones) && !isDescuentoAFuturo()) {
				importePromocion = importeDespuesPromociones;
			}
			else {
				importePromocion = valorConfigurado;
			}

		}
		// Si el tipo de filtro es porcentaje
		else if (isPorcentaje()) {
			BigDecimal importeDespuesPromociones = importeLineasConDto.subtract(totalesPromocionesCabecera);
			importePromocion = BigDecimalUtil.porcentaje(importeDespuesPromociones, valorConfigurado);
		}
		else if (isPorcentajeSobrePVP()) {
			importeLineasConDto = BigDecimal.ZERO;
			for(LineaDocumentoPromocionable linea : lineasAplicables.getLineasAplicables()) {
				BigDecimal precioPromocionSinDto = ((LineaTicket) linea).getPrecioPromocionSinDto();
				BigDecimal importeTotalLinea = null;
				if(precioPromocionSinDto != null) {
					importeTotalLinea = precioPromocionSinDto.multiply(linea.getCantidad());
				}
				else {
					importeTotalLinea = ((LineaTicket) linea).getImporteTotalSinDto();
				}
				importeLineasConDto = importeLineasConDto.add(importeTotalLinea);
			}
			BigDecimal importeDespuesPromociones = importeLineasConDto.subtract(totalesPromocionesCabecera);
			importePromocion = BigDecimalUtil.porcentaje(importeDespuesPromociones, valorConfigurado);
		}

		promocionTicket.setImporteTotalAhorro(importePromocion);
		documento.addPromocion(promocionTicket);

		if (isImporte()) {
			BigDecimal porcentajeDescuento = BigDecimal.ZERO;
			BigDecimal importeDespuesPromociones = importeLineasConDto.subtract(totalesPromocionesCabecera);
			if (!BigDecimalUtil.isIgualACero(importePromocion)) {
				porcentajeDescuento = BigDecimalUtil.getTantoPorCiento(importeDespuesPromociones, importePromocion);
			}

			lineasAplicables.aplicaDescuentoPorcentajeGeneral(promocionTicket, porcentajeDescuento);
		}
		else if (isPorcentaje()) {
			lineasAplicables.aplicaDescuentoPorcentajeGeneral(promocionTicket, valorConfigurado);
		}
		else if (isPorcentajeSobrePVP()) {
			((DinoLineasAplicablesEditadasManualmentePromoBean) lineasAplicables).aplicaDescuentoPorcentajeGeneralSobrePVP(promocionTicket, valorConfigurado);
		}

		CuponAplicadoTicket cupon = documento.getCuponAplicado(codigoCupon);
		if (cupon != null) {
			cupon.setTextoPromocion(promocionTicket.getTextoPromocion());
		}

		return true;
	}

}
