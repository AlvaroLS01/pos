package com.comerzzia.bimbaylola.pos.services.ticket.profesional;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.cabecera.SubtotalIvaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;
import com.comerzzia.pos.services.ticket.pagos.IPagoTicket;
import com.comerzzia.pos.services.ticket.profesional.TicketVentaProfesional;
import com.comerzzia.pos.services.ticket.profesional.TotalesTicketProfesional;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "ivaTotal", "recargoTotal" })
@Component
@Scope("prototype")
public class ByLTotalesTicketProfesional extends TotalesTicketProfesional {

	protected BigDecimal totalRecargosVertexOffline;

	public ByLTotalesTicketProfesional() {
		super();
	}

	@SuppressWarnings("rawtypes")
	public ByLTotalesTicketProfesional(ITicket ticket) {
		resetearTotales();
		this.ticket = ticket;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void recalcular() {
		resetearTotales();
		((TicketVentaProfesional) ticket).recalcularSubtotalesIva();

		for (SubtotalIvaTicket subtotal : (List<SubtotalIvaTicket>) ticket.getCabecera().getSubtotalesIva()) {

			BigDecimal cuotaRecargo2 = ((ByLSubtotalIvaTicketProfesional) subtotal).getCuotaRecargo2() == null ? BigDecimal.ZERO : ((ByLSubtotalIvaTicketProfesional) subtotal).getCuotaRecargo2();
			BigDecimal cuotaRecargo3 = ((ByLSubtotalIvaTicketProfesional) subtotal).getCuotaRecargo3() == null ? BigDecimal.ZERO : ((ByLSubtotalIvaTicketProfesional) subtotal).getCuotaRecargo3();
			BigDecimal cuotaRecargo4 = ((ByLSubtotalIvaTicketProfesional) subtotal).getCuotaRecargo4() == null ? BigDecimal.ZERO : ((ByLSubtotalIvaTicketProfesional) subtotal).getCuotaRecargo4();
			BigDecimal cuotaRecargo5 = ((ByLSubtotalIvaTicketProfesional) subtotal).getCuotaRecargo5() == null ? BigDecimal.ZERO : ((ByLSubtotalIvaTicketProfesional) subtotal).getCuotaRecargo5();

			ivaTotal = ivaTotal.add(subtotal.getCuota());
			recargoTotal = recargoTotal.add(subtotal.getCuotaRecargo());

			subtotal.setTotal(subtotal.getBase().add(subtotal.getCuota()).add(subtotal.getCuotaRecargo()));
			subtotal.setImpuestos(BigDecimalUtil.redondear(subtotal.getTotal().subtract(subtotal.getBase()), 2));
			subtotal.setTotal(BigDecimalUtil.redondear(subtotal.getTotal(), 2));

			totalRecargosVertexOffline = totalRecargosVertexOffline.add(cuotaRecargo2).add(cuotaRecargo3).add(cuotaRecargo4).add(cuotaRecargo5);
			base = base.add(subtotal.getBase());
		}

		/*
		 * En el caso de que sea integración con vertex modo OFFLINE el totalRecargosVertexOffline se rellenará con los
		 * recargos de la tabla personalizada. En cualquier otro caso, estos recargos serán 0 por lo que se sumaría 0.
		 */
		total = base.add(ivaTotal).add(recargoTotal).add(totalRecargosVertexOffline);
		impuestos = BigDecimalUtil.redondear(total.subtract(base), 2);
		total = BigDecimalUtil.redondear(total, 2);
		// Se comenta este método, comprobarImportesTotales();, por petición de Claudia relacionado con el merge branch
		// [LUST-117718] - Se añade comprobacion para que el importe total de los subtotales
		// y los importes totales de las lineas cuadren. Si no cuadran, revisamos si es debido al IVA o recargo y lo
		// modificamos. Se tiene en cuenta el importeTotalConDto
		// para el caso en que hubiera promociones aplicadas al ticket o lineas
		// comprobarImportesTotales();

		// Calcular el total a pagar (total sin promociones - total promociones tanto en lÃ­nea como en cabecera)
		totalAPagar = total.subtract(totalPromocionesCabecera);

		// Calculamos entregado sumando todos los pagos
		for (Object pagoTicket : ((TicketVentaAbono) ticket).getPagos()) {
			entregado = entregado.add(((IPagoTicket) pagoTicket).getImporte());
		}

		// Calculamos el pendiente y el cambio
		BigDecimal totalEntregado = entregado.add(entregadoACuenta);
		if (BigDecimalUtil.isMenorACero(totalAPagar)) {
			if (BigDecimalUtil.isMayor(totalEntregado, totalAPagar)) {
				pendiente = totalEntregado.subtract(totalAPagar);
				cambio.setImporte(BigDecimal.ZERO);
			}
			else {
				pendiente = BigDecimal.ZERO;
				cambio.setImporte(totalEntregado.subtract(totalAPagar));
			}
		}
		else {
			if (BigDecimalUtil.isMenor(totalEntregado, totalAPagar)) {
				pendiente = totalAPagar.subtract(totalEntregado);
				cambio.setImporte(BigDecimal.ZERO);
			}
			else {
				pendiente = BigDecimal.ZERO;
				cambio.setImporte(totalEntregado.subtract(totalAPagar));
			}
		}

		// Calculamos los totales de las promociones
		for (LineaTicketAbstract lineaT : (List<LineaTicketAbstract>) ticket.getLineas()) {
			BigDecimal importeTotalPromociones = lineaT.getImporteTotalPromociones();
			BigDecimal importeTotalPromocionesMenosIngreso = lineaT.getImporteTotalPromocionesMenosIngreso();

			totalPromocionesCabecera = totalPromocionesCabecera.add(importeTotalPromocionesMenosIngreso);

			totalPromocionesLineas = BigDecimalUtil.redondear(totalPromocionesLineas.add(importeTotalPromociones));
			// totalSinPromociones = BigDecimalUtil.redondear(totalSinPromociones.add(lineaT.getImporteTotalSinDto()));
			if (lineaT.getPromociones().isEmpty()) {
				totalSinPromociones = BigDecimalUtil.redondear(totalSinPromociones.add(lineaT.getImporteTotalConDto()));
			}
			else {
				totalSinPromociones = BigDecimalUtil.redondear(totalSinPromociones.add(lineaT.getImporteTotalSinDto()));
			}
			
			if (((ByLLineaTicketProfesional) lineaT).getLineaVertex() != null) {
				BigDecimal impuestoLinea = new BigDecimal(((ByLLineaTicketProfesional) lineaT).getLineaVertex().getAmountWithTax())
				        .subtract(new BigDecimal(((ByLLineaTicketProfesional) lineaT).getLineaVertex().getAmount()));
				impuestoLinea = impuestoLinea.divide(lineaT.getCantidad());
				// [LUST-135128] - Se añade condicion en caso de que sea devolución siendo el precio en
				// negativo se le resta, no suma.
				if (("SR").equals(ticket.getCabecera().getCodTipoDocumento())) {
					lineaT.setImporteTotalConDto(lineaT.getImporteConDto().add(impuestoLinea));
					lineaT.setPrecioTotalConDto(lineaT.getPrecioConDto().subtract(impuestoLinea));

				}
				else {
					lineaT.setPrecioTotalConDto(lineaT.getPrecioConDto().add(impuestoLinea));
					lineaT.setImporteTotalConDto(lineaT.getImporteConDto().add(impuestoLinea));
				}
			}
		}
		totalPromociones = totalPromocionesCabecera.add(totalPromocionesLineas);
		totalPromocionesLineas = BigDecimalUtil.redondear(totalPromocionesLineas);
		totalPromociones = BigDecimalUtil.redondear(totalPromociones);
		totalPromocionesCabecera = BigDecimalUtil.redondear(totalPromocionesCabecera);

	}

	@Override
	protected void resetearTotales() {
		super.resetearTotales();
		this.totalRecargosVertexOffline = BigDecimal.ZERO;
	}

	public BigDecimal getTotalRecargosVertexOffline() {
		return totalRecargosVertexOffline;
	}

	public void setTotalRecargosVertexOffline(BigDecimal totalRecargosVertexOffline) {
		this.totalRecargosVertexOffline = totalRecargosVertexOffline;
	}
	// Se comenta este método,comprobarImportesTotales() , por petición de Claudia relacionado con el merge branch
	// [LUST-117718] - Se añade comprobacion para que el importe total de los subtotales
	// y los importes totales de las lineas cuadren. Si no cuadran, revisamos si es debido al IVA o recargo y lo
	// modificamos. Se tiene en cuenta el importeTotalConDto
	// para el caso en que hubiera promociones aplicadas al ticket o lineas
// @SuppressWarnings("unchecked")
//	private void comprobarImportesTotales() {
//		BigDecimal totalRecalculado = BigDecimal.ZERO;
//		BigDecimal ivaLineasTotal = BigDecimal.ZERO;
//		BigDecimal recargoLineasTotal = BigDecimal.ZERO;
//		BigDecimal impuestosLineasTotal = BigDecimal.ZERO;
//		for (LineaTicketAbstract lineaT : (List<LineaTicketAbstract>) ticket.getLineas()) {
//			if (lineaT instanceof ByLLineaTicketProfesional) {
//				totalRecalculado = totalRecalculado.add(lineaT.getImporteTotalConDto());
//				ivaLineasTotal = ivaLineasTotal.add(((ByLLineaTicketProfesional) lineaT).getTotalIva());
//				recargoLineasTotal = recargoLineasTotal.add(((ByLLineaTicketProfesional) lineaT).getTotalRecargo());
//				impuestosLineasTotal = impuestosLineasTotal.add(lineaT.getImpuestos());
//			}
//		}
//		if (!BigDecimalUtil.isIgual(total, totalRecalculado)) {
//			total = totalRecalculado;
//			if (!BigDecimalUtil.isIgual(ivaTotal, ivaLineasTotal)) {
//				ivaTotal = ivaLineasTotal;
//			}
//			if (!BigDecimalUtil.isIgual(recargoTotal, recargoLineasTotal)) {
//				recargoTotal = recargoLineasTotal;
//			}
//			if (!BigDecimalUtil.isIgual(impuestos, impuestosLineasTotal)) {
//				impuestos = impuestosLineasTotal;
//			}
//		}
//	}

}
