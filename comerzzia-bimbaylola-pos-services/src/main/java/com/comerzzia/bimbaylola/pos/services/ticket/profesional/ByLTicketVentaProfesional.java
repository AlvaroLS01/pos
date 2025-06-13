package com.comerzzia.bimbaylola.pos.services.ticket.profesional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.comerzzia.bimbaylola.pos.services.ticket.cabecera.ByLCabeceraTicket;
import com.comerzzia.bimbaylola.pos.services.vertex.LineaDetailVertex;
import com.comerzzia.bimbaylola.pos.services.vertex.LineaVertex;
import com.comerzzia.pos.persistence.core.impuestos.porcentajes.PorcentajeImpuestoBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.ticket.cabecera.ComparadorSubtotalesIvaTicketPorcentaje;
import com.comerzzia.pos.services.ticket.cabecera.SubtotalIvaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.profesional.TicketVentaProfesional;
import com.comerzzia.pos.util.config.SpringContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "ticket")
@Scope("prototype")
@Component
//@Primary
public class ByLTicketVentaProfesional extends TicketVentaProfesional {

	public Logger log = Logger.getLogger(ByLTicketVentaProfesional.class);

	@SuppressWarnings("unchecked")
	@Override
	public void recalcularSubtotalesIva() {
		if (((ByLCabeceraTicket) cabecera).getCabeceraVertex() != null) {
			log.debug("recalcularSubtotalesIva() - Tiene impuestos Vertex");

			Sesion sesion = SpringContext.getBean(Sesion.class);

			BigDecimal totalLineasVertex = BigDecimal.ZERO;
			BigDecimal baseLineasVertex = BigDecimal.ZERO;

			BigDecimal totalLineaVertexOffline = BigDecimal.ZERO;
			BigDecimal baseLineaVertexOffline = BigDecimal.ZERO;

			Map<String, ByLSubtotalIvaTicketProfesional> subtotales = new HashMap<>();
			for (LineaTicket linea : lineas) {
				ByLSubtotalIvaTicketProfesional subtotal = null;
				LineaVertex lineaVertex = ((ByLLineaTicketProfesional) linea).getLineaVertex();

				if (lineaVertex != null) {
					totalLineaVertexOffline = new BigDecimal(lineaVertex.getAmountWithTax());
					baseLineaVertexOffline = new BigDecimal(lineaVertex.getAmount());
					/*
					 * Cogemos el absoluto ya que cuando hace la petición vertex se modifican a negativo aumount y
					 * amountWithTax en la vuelta del invoice
					 */
					if (isEsDevolucion()) {
						totalLineaVertexOffline = totalLineaVertexOffline.abs().negate();
						baseLineaVertexOffline = baseLineaVertexOffline.abs().negate();
					}

					if (lineaVertex != null) {
						totalLineasVertex = totalLineasVertex.add(totalLineaVertexOffline);
						baseLineasVertex = baseLineasVertex.add(baseLineaVertexOffline);
					}

					BigDecimal impuestosVertex = totalLineasVertex.subtract(baseLineasVertex);

					String codImpuesto = linea.getArticulo().getCodImpuesto();
					PorcentajeImpuestoBean porcentajeImpuesto = null;
					if (!((ByLCabeceraTicket) cabecera).isEsVertexOnline() && StringUtils.isBlank(((ByLCabeceraTicket) cabecera).getCabeceraVertex().getIsSentToVertex())
					        || ((ByLCabeceraTicket) cabecera).getCabeceraVertex().getIsSentToVertex().equals("N")) {
						/* MODO OFFLINE */
						recalcularSubtotalesIvaOffline(subtotal, codImpuesto, subtotales, linea, porcentajeImpuesto, sesion, baseLineaVertexOffline, totalLineaVertexOffline);
					}
					else {
						/* MODO ONLINE */
						recalcularSubtotalesIvaOnline(subtotal, subtotales, baseLineasVertex, totalLineasVertex, impuestosVertex);
					}
				}
			}

			getCabecera().getSubtotalesIva().clear();
			for (ByLSubtotalIvaTicketProfesional subtotal : subtotales.values()) {
				subtotal.recalcularVertex();
				getCabecera().getSubtotalesIva().add(subtotal);
			}

			ComparadorSubtotalesIvaTicketPorcentaje comparador = new ComparadorSubtotalesIvaTicketPorcentaje();
			Collections.sort(getCabecera().getSubtotalesIva(), comparador);
		}
		else {
			super.recalcularSubtotalesIva();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void inicializarTotales() {
		getCabecera().setTotales(SpringContext.getBean(ByLTotalesTicketProfesional.class, this));
	}

	@Override
	protected SubtotalIvaTicket crearSubTotalIvaTicket() {
		return new ByLSubtotalIvaTicketProfesional();
	}

	private void recalcularSubtotalesIvaOffline(ByLSubtotalIvaTicketProfesional subtotal, String codImpuesto, Map<String, ByLSubtotalIvaTicketProfesional> subtotales, LineaTicket linea,
	        PorcentajeImpuestoBean porcentajeImpuesto, Sesion sesion, BigDecimal baseLineaVertexOffline, BigDecimal totalLineaVertexOffline) {
		subtotal = subtotales.get(codImpuesto);

		List<LineaDetailVertex> listaLineasVertex = ((ByLLineaTicketProfesional) linea).getListaImpuestosVertex();

		BigDecimal cuota = BigDecimal.ZERO;
		BigDecimal cuotaRecargo = BigDecimal.ZERO;
		BigDecimal cuotaRecargo2 = BigDecimal.ZERO;
		BigDecimal cuotaRecargo3 = BigDecimal.ZERO;
		BigDecimal cuotaRecargo4 = BigDecimal.ZERO;
		BigDecimal cuotaRecargo5 = BigDecimal.ZERO;
		for (LineaDetailVertex lineaDetailVertex : listaLineasVertex) {
			BigDecimal impuesto = new BigDecimal(lineaDetailVertex.getTaxAmount());
			switch (lineaDetailVertex.getTaxTypeId()) {
				case "1":
					cuota = cuota.add(impuesto);
					
					break;
				case "2":
					cuotaRecargo = cuotaRecargo.add(impuesto);
					if (isEsDevolucion()) {
						cuotaRecargo = cuotaRecargo.negate();
					}
					break;
				case "3":
					cuotaRecargo2 = cuotaRecargo2.add(impuesto);
					if (isEsDevolucion()) {
						cuotaRecargo2 = cuotaRecargo2.negate();
					}
					break;
				case "4":
					cuotaRecargo3 = cuotaRecargo3.add(impuesto);
					if (isEsDevolucion()) {
						cuotaRecargo3 = cuotaRecargo3.negate();
					}
					break;
				case "5":
					cuotaRecargo4 = cuotaRecargo4.add(impuesto);
					if (isEsDevolucion()) {
						cuotaRecargo4 = cuotaRecargo4.negate();
					}
					break;
				case "6":
					cuotaRecargo5 = cuotaRecargo5.add(impuesto);
					if (isEsDevolucion()) {
						cuotaRecargo5 = cuotaRecargo5.negate();
					}
					break;
				default:
					/*
					 * Entrará en el caso que se haga una devolución en modo OFFLINE de una venta ONLINE En este caso
					 * añadiremos todos los impuestos que vengan al campo "cuota" para que se sigan sumando los
					 * impuestos
					 */
					cuota = cuota.add(impuesto);
					
					break;
			}

		}
		
		if (isEsDevolucion()) {
			cuota = cuota.negate();
		}

		if (subtotal == null) {
			subtotal = new ByLSubtotalIvaTicketProfesional();
			porcentajeImpuesto = sesion.getImpuestos().getPorcentaje(getCliente().getIdTratImpuestos(), linea.getArticulo().getCodImpuesto());
			subtotal.setPorcentajeImpuestoBean(porcentajeImpuesto);

			subtotal.setCuota(cuota);
			subtotal.setCuotaRecargo(cuotaRecargo);
			subtotal.setCuotaRecargo2(cuotaRecargo2);
			subtotal.setCuotaRecargo3(cuotaRecargo3);
			subtotal.setCuotaRecargo4(cuotaRecargo4);
			subtotal.setCuotaRecargo5(cuotaRecargo5);

			subtotal.setBase(baseLineaVertexOffline);
			subtotal.setImpuestos(totalLineaVertexOffline.subtract(baseLineaVertexOffline));
			subtotal.setTotal(totalLineaVertexOffline);

			subtotales.put(codImpuesto, subtotal);
		}
		else {
			subtotal.addLineaVertex(linea, cuota, cuotaRecargo, cuotaRecargo2, cuotaRecargo3, cuotaRecargo4, cuotaRecargo5);
		}
	}

	private void recalcularSubtotalesIvaOnline(ByLSubtotalIvaTicketProfesional subtotal, Map<String, ByLSubtotalIvaTicketProfesional> subtotales, BigDecimal baseLineasVertex,
	        BigDecimal totalLineasVertex, BigDecimal impuestosVertex) {
		subtotal = new ByLSubtotalIvaTicketProfesional();

		subtotal.setCuota(impuestosVertex);
		String codImpuesto = "0";
		
		PorcentajeImpuestoBean porcentajeImpuesto = new PorcentajeImpuestoBean();
		porcentajeImpuesto.setPorcentaje(BigDecimal.ZERO);
		porcentajeImpuesto.setCodImpuesto("0");
//		if(isEsDevolucion()) {
//			baseLineasVertex = baseLineasVertex.negate();
//			impuestosVertex = impuestosVertex.negate();
//			totalLineasVertex = totalLineasVertex.negate();
//		}
		subtotal.setBase(baseLineasVertex);
		subtotal.setImpuestos(impuestosVertex);
		subtotal.setPorcentajeImpuestoBean(porcentajeImpuesto);
		subtotal.setTotal(totalLineasVertex);

		subtotales.put(codImpuesto, subtotal);
	}
}
