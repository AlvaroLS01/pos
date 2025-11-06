package com.comerzzia.dinosol.pos.services.ticket.cabecera;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.cabecera.SubtotalIvaTicket;
import com.comerzzia.pos.services.ticket.cabecera.TotalesTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;
import com.comerzzia.pos.services.ticket.pagos.IPagoTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

@Component
@Scope("prototype")
@Primary
public class DinoTotalesTicket extends TotalesTicket {

	public DinoTotalesTicket() {
		totalPromocionesCabecera = BigDecimal.ZERO;
		totalPromocionesLineas = BigDecimal.ZERO;
	}

	@SuppressWarnings("rawtypes")
    public DinoTotalesTicket(ITicket ticket) {
		resetearTotales();
		this.ticket = ticket;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void recalcular() {
		// Reseteamos los totales
		resetearTotales();
		((TicketVentaAbono) ticket).recalcularSubtotalesIva();

		// calculo de la base y el total a partir de los subtotales
		for (SubtotalIvaTicket subtotal : (List<SubtotalIvaTicket>) ticket.getCabecera().getSubtotalesIva()) {
			base = base.add(subtotal.getBase());
			impuestos = impuestos.add(subtotal.getImpuestos());
			total = total.add(subtotal.getTotal());
		}

		// Calculamos los totales de las promociones
		for (LineaTicketAbstract lineaT : (List<LineaTicketAbstract>) ticket.getLineas()) {
			totalPromocionesCabecera = totalPromocionesCabecera.add(lineaT.getImporteTotalPromocionesMenosIngreso());
			// totalPromocionesLineas =
			// BigDecimalUtil.redondear(totalPromocionesLineas.add(lineaT.getImporteTotalPromociones()));
			if (lineaT.getPromociones().isEmpty()) {
				totalSinPromociones = BigDecimalUtil.redondear(totalSinPromociones.add(lineaT.getImporteTotalConDto()));
			}
			else {
				totalSinPromociones = BigDecimalUtil.redondear(totalSinPromociones.add(lineaT.getImporteTotalSinDto()));
			}
		}
		totalPromocionesLineas = totalSinPromociones.subtract(total);

		// Calculamos entregado sumando todos los pagos
		for (Object pagoTicket : ((TicketVentaAbono) ticket).getPagos()) {
			entregado = entregado.add(((IPagoTicket) pagoTicket).getImporte());
		}

		// Calculamos totales
		totalPromociones = totalPromocionesCabecera.add(totalPromocionesLineas);
		totalAPagar = total;

		totalSinPromociones = BigDecimalUtil.redondear(totalSinPromociones);
		totalPromocionesLineas = BigDecimalUtil.redondear(totalPromocionesLineas);
		totalPromociones = BigDecimalUtil.redondear(totalPromociones);
		totalPromocionesCabecera = BigDecimalUtil.redondear(totalPromocionesCabecera);

		BigDecimal totalEntregado = entregado.add(entregadoACuenta);
		if (BigDecimalUtil.isMenorACero(totalAPagar)) {
			if (BigDecimalUtil.isMayor(totalEntregado, totalAPagar)) {
				pendiente = totalAPagar.subtract(totalEntregado);
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
	}

}
