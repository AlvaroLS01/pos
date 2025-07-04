package com.comerzzia.cardoso.pos.services.ticket;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.cardoso.pos.services.ticket.cabecera.CARDOSOTotalesTicket;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.util.config.SpringContext;

/**
 * GAP - SUBTOTALES IVA 
 */
@Component
@Scope("prototype")
@Primary
@XmlRootElement(name = "ticket")
@SuppressWarnings("unchecked")
@XmlAccessorType(XmlAccessType.NONE)
public class CardosoTicketVentaAbono extends TicketVentaAbono{

	@Override
	public void inicializarTotales(){
		getCabecera().setTotales(SpringContext.getBean(CARDOSOTotalesTicket.class, this));
		//getCabecera().setTotales(SpringContext.getBean(TotalesTicket.class, this));
	}

//	@Override
//	public void recalcularSubtotalesIva(){
//		recalcularSubtotalesIva(false);
//	}
//
//	public void recalcularSubtotalesIva(boolean calcular){
//		if(!calcular){
//			return;
//		}
//		/* Construimos mapa con subtotales recorriendo todas las líneas del ticket */
//		Map<String, SubtotalIvaTicket> subtotales = new HashMap<>();
//		Sesion sesion = SpringContext.getBean(Sesion.class);
//		for(LineaTicket linea : lineas){
//			linea.recalcularImporteFinal();
//			String codImpuesto = linea.getArticulo().getCodImpuesto();
//			SubtotalIvaTicket subtotal = subtotales.get(codImpuesto);
//			if(subtotal == null){
//				subtotal = crearSubTotalIvaTicket();
//				PorcentajeImpuestoBean porcentajeImpuesto = sesion.getImpuestos().getPorcentaje(getCliente().getIdTratImpuestos(), codImpuesto);
//				subtotal.setPorcentajeImpuestoBean(porcentajeImpuesto);
//				subtotales.put(codImpuesto, subtotal);
//			}
//			subtotal.addLinea(linea);
//		}
//
//		/* Añadimos cada subtotal a la lista de la cabecera del ticket */
//		getCabecera().getSubtotalesIva().clear();
//		BigDecimal impuestosTotal = BigDecimal.ZERO;
//		BigDecimal baseTotal = BigDecimal.ZERO;
//		for(SubtotalIvaTicket subtotal : subtotales.values()){
//			/* Quitamos el descuento de cabecera en el total del subtotal para obtener la base y los impuestos correctamente
//			 * BigDecimal vBaseDescuentoCabecera =
//			 * BigDecimalUtil.redondear(subtotal.getBase().multiply(((CardosoCabeceraTicket)getCabecera()).
//			 * getDescuentoCabecera().divide(new BigDecimal(100)))); BigDecimal vTotalDescuentoCabecera =
//			 * BigDecimalUtil.redondear(subtotal.getTotal().multiply(((CardosoCabeceraTicket)getCabecera()).
//			 * getDescuentoCabecera().divide(new BigDecimal(100))));
//			 * subtotal.setBase(subtotal.getBase().subtract(vBaseDescuentoCabecera));
//			 * subtotal.setTotal(subtotal.getTotal().subtract(vTotalDescuentoCabecera)); */
//			/* Recalculamos cada subtotal (impuestos, cuotas y totales) */
//			subtotal.recalcular();
//			getCabecera().getSubtotalesIva().add(subtotal);
//			impuestosTotal = impuestosTotal.add(subtotal.getImpuestos());
//			baseTotal = baseTotal.add(subtotal.getBase());
//		}
//		getTotales().setImpuestos(impuestosTotal);
//		getTotales().setBase(baseTotal);
//
//		ComparadorSubtotalesIvaTicketPorcentaje comparador = new ComparadorSubtotalesIvaTicketPorcentaje();
//		Collections.sort(getCabecera().getSubtotalesIva(), comparador);
//	}

}
