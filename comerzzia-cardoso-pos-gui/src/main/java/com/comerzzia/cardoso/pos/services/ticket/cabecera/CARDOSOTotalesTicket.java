package com.comerzzia.cardoso.pos.services.ticket.cabecera;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.apache.log4j.Logger;
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
@Primary
@Scope("prototype")
@SuppressWarnings({"rawtypes", "unchecked"})
@XmlAccessorType(XmlAccessType.FIELD)
public class CARDOSOTotalesTicket extends TotalesTicket{

	private static final Logger log = Logger.getLogger(CARDOSOTotalesTicket.class.getName());

	public CARDOSOTotalesTicket(ITicket ticket){
		this.ticket = ticket;
		resetearTotales();
	}
	
	@Override
	protected void resetearTotales(){
		super.resetearTotales();
		
		/* GAP XX - PROMOCIONES ESPECIALES : PROMOCIONES MONOGRÁFICAS Y DE EMPLEADOS */
		((CARDOSOCabeceraTicket) ticket.getCabecera()).setBaseSinDescuento(BigDecimal.ZERO);
		((CARDOSOCabeceraTicket) ticket.getCabecera()).setTotalSinDescuento(BigDecimal.ZERO);
		((CARDOSOCabeceraTicket) ticket.getCabecera()).setTotalRecargo(BigDecimal.ZERO);
	}
	
	@Override
	public void recalcular(){
		resetearTotales();
		((TicketVentaAbono) ticket).recalcularSubtotalesIva();
		
		/* GAP XX - PROMOCIONES ESPECIALES : PROMOCIONES MONOGRÁFICAS Y DE EMPLEADOS 
		 * Variables necesarias para datos de la V3. */
		BigDecimal baseSinDto = BigDecimal.ZERO, totalSinDto = BigDecimal.ZERO, recargoTotal = BigDecimal.ZERO;
		
		/* Sacamos el importe de descuento de cabecera para aplicarlo. */
		BigDecimal descuentoCabecera = BigDecimal.ZERO;
		if(((CARDOSOCabeceraTicket) ticket.getCabecera()).getDatosDescuentoPromocionMonografica() == null && 
				((CARDOSOCabeceraTicket)ticket.getCabecera()).getDatosDescuentoPromocionEmpleados() != null){
			log.info("recalcular() - GAP XX - PROMOCIONES ESPECIALES : PROMOCIONES MONOGRÁFICAS Y DE EMPLEADOS...");
			
			descuentoCabecera = ((CARDOSOCabeceraTicket)ticket.getCabecera()).getDatosDescuentoPromocionEmpleados().getDescuento();
		}
		
		for(SubtotalIvaTicket subtotal : (List<SubtotalIvaTicket>) ticket.getCabecera().getSubtotalesIva()){
			totalSinDto = totalSinDto.add(subtotal.getTotal());
			BigDecimal porcentajeMasRecargo = subtotal.getPorcentaje().add(subtotal.getPorcentajeRecargo());
			baseSinDto = baseSinDto.add(BigDecimalUtil.getAntesDePorcentaje(subtotal.getTotal(), porcentajeMasRecargo));
			
			if(!BigDecimalUtil.isIgualACero(descuentoCabecera)){
				BigDecimal totalAux = BigDecimal.ONE.subtract(descuentoCabecera.divide(new BigDecimal(100)));
				totalAux = BigDecimalUtil.redondear(subtotal.getTotal().multiply(totalAux));
				subtotal.setTotal(totalAux);
				subtotal.recalcular();
			}
			// FIN GAP XX
			
			base = base.add(subtotal.getBase());
			impuestos = impuestos.add(subtotal.getImpuestos());
			recargoTotal = recargoTotal.add(subtotal.getCuotaRecargo());
			total = total.add(subtotal.getTotal());
		}

		for(LineaTicketAbstract lineaT : (List<LineaTicketAbstract>) ticket.getLineas()){
			totalPromocionesCabecera = totalPromocionesCabecera.add(lineaT.getImporteTotalPromocionesMenosIngreso());
			if(lineaT.getPromociones().isEmpty()){
				totalSinPromociones = BigDecimalUtil.redondear(totalSinPromociones.add(lineaT.getImporteTotalConDto()));
			}
			else{
				totalSinPromociones = BigDecimalUtil.redondear(totalSinPromociones.add(lineaT.getImporteTotalSinDto()));
			}	
		}
		totalPromocionesLineas = totalSinPromociones.subtract(total);

		/* GAP XX - PROMOCIONES ESPECIALES : PROMOCIONES MONOGRÁFICAS Y DE EMPLEADOS 
		 * Aplicamos las promociones siempre en caso de que no se haya aplicado ya una de ellas.
		 * Primero comprobamos la parte de monográfica, en caso de tener, no aplicamos empleados. */
		if(((CARDOSOCabeceraTicket) ticket.getCabecera()).getDatosDescuentoPromocionMonografica() != null){
			log.info("recalcular() - GAP XX - PROMOCIONES ESPECIALES : PROMOCIONES MONOGRÁFICAS Y DE EMPLEADOS...");
			
			baseSinDto = baseSinDto.add(((CARDOSOCabeceraTicket) ticket.getCabecera()).getDatosDescuentoPromocionMonografica().getImporte());
			totalSinDto = totalSinDto.add(((CARDOSOCabeceraTicket) ticket.getCabecera()).getDatosDescuentoPromocionMonografica().getImporteTotal());
		}
		
		((CARDOSOCabeceraTicket) ticket.getCabecera()).setBaseSinDescuento(baseSinDto);
		((CARDOSOCabeceraTicket) ticket.getCabecera()).setTotalSinDescuento(totalSinDto);
		((CARDOSOCabeceraTicket) ticket.getCabecera()).setTotalRecargo(recargoTotal);
		
		for(Object pagoTicket : ((TicketVentaAbono) ticket).getPagos()){
			entregado = entregado.add(((IPagoTicket) pagoTicket).getImporte());
		}

		totalPromociones = totalPromocionesCabecera.add(totalPromocionesLineas);
		totalAPagar = total;

		totalSinPromociones = BigDecimalUtil.redondear(totalSinPromociones);
		totalPromocionesLineas = BigDecimalUtil.redondear(totalPromocionesLineas);
		totalPromociones = BigDecimalUtil.redondear(totalPromociones);
		totalPromocionesCabecera = BigDecimalUtil.redondear(totalPromocionesCabecera);

		BigDecimal totalEntregado = entregado.add(entregadoACuenta);
		if(BigDecimalUtil.isMenorACero(totalAPagar)){
			if(BigDecimalUtil.isMayor(totalEntregado, totalAPagar)){
				pendiente = totalEntregado.subtract(totalAPagar);
				cambio.setImporte(BigDecimal.ZERO);
			}
			else{
				pendiente = BigDecimal.ZERO;
				cambio.setImporte(totalEntregado.subtract(totalAPagar));
			}
		}
		else{
			if(BigDecimalUtil.isMenor(totalEntregado, totalAPagar)){
				pendiente = totalAPagar.subtract(totalEntregado);
				cambio.setImporte(BigDecimal.ZERO);
			}
			else{
				pendiente = BigDecimal.ZERO;
				cambio.setImporte(totalEntregado.subtract(totalAPagar));
			}
		}
	}
	
}