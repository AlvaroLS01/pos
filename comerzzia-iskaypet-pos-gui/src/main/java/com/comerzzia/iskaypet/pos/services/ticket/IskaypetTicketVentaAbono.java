package com.comerzzia.iskaypet.pos.services.ticket;

import com.comerzzia.iskaypet.pos.services.ticket.cabecera.IskaypetCabeceraTicket;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.cupones.CuponEmitidoTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "ticket")
@Primary
@Component
@Scope("prototype")
public class IskaypetTicketVentaAbono extends TicketVentaAbono{

	private Logger log = Logger.getLogger(IskaypetTicketVentaAbono.class);

	public IskaypetTicketVentaAbono(){
	}

	@SuppressWarnings("rawtypes")
	public BigDecimal getPuntosDevueltosAsBigDecimal(){
		BigDecimal puntosDevueltos = BigDecimal.ZERO;
		if(this.getLineas() != null){
			Iterator var2 = this.getLineas().iterator();
			while(var2.hasNext()){
				LineaTicket lineaTicket = (LineaTicket) var2.next();
				Double puntosLineaDevueltos = lineaTicket.getPuntosADevolver();
				if(puntosLineaDevueltos != null){
					puntosDevueltos = puntosDevueltos.add(new BigDecimal(puntosLineaDevueltos));
				}
			}
		}
		return puntosDevueltos;
	}

	@Override
	public BigDecimal getCantidadTotal() {
		log.debug("getCantidadTotal() - Calculando la cantidad total de artículos en el ticket");
		BigDecimal cantidad = super.getCantidadTotal();
		calcularCantidadTotalAImprimir();
		log.debug("getCantidadTotal() - Cantidad total de artículos: " + cantidad);
		return cantidad;
	}

	public void calcularCantidadTotalAImprimir() {
		log.debug("calcularCantidadTotalAImprimir() - Calculando la cantidad total a imprimir");
		if(cabecera instanceof IskaypetCabeceraTicket) {
			BigDecimal cantidad = BigDecimal.ZERO;
			try {
				for (LineaTicket lineaTicket : getLineas()) {
					if ( lineaTicket instanceof IskaypetLineaTicket && ((IskaypetLineaTicket) lineaTicket).isImprimir()) {
						cantidad = cantidad.add(lineaTicket.getCantidad().abs());
					}
				}
				log.debug("calcularCantidadTotalAImprimir() - Cantidad total a imprimir: " + cantidad);
				((IskaypetCabeceraTicket) cabecera).setTotalArticulosImpresos(cantidad);
			} catch (Exception e) {
				log.error("calcularCantidadTotalAImprimir() - Error al calcular la cantidad total a imprimir", e);
			}
		}
	}

	@Override
	public void addCuponEmitido(CuponEmitidoTicket cupon) {
		log.debug("addCuponEmitido() - Añadiendo cupón emitido al ticket de venta abono");
		if (cuponesEmitidos == null) {
			log.debug("addCuponEmitido() - Inicializando la lista de cupones emitidos");
			this.cuponesEmitidos = new ArrayList<>();
		}

		// Verificar si el cupón ya existe en la lista de cupones emitidos
		CuponEmitidoTicket cuponEmitidoTicket = cuponesEmitidos.stream().filter(c -> c.equals(cupon)).findFirst().orElse(null);
		if (cuponEmitidoTicket == null) {
			log.debug("addCuponEmitido() - Cupón no encontrado, añadiendo nuevo cupón emitido");
			this.cuponesEmitidos.add(cupon);
		}
	}
}
