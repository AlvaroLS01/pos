/**
 * ComerZZia 3.0 Copyright (c) 2008-2015 Comerzzia, S.L. All Rights Reserved. THIS WORK IS SUBJECT TO SPAIN AND
 * INTERNATIONAL COPYRIGHT LAWS AND TREATIES. NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED COPIED,
 * DISTRIBUTED, REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED, LINKED, RECAST,
 * TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION OF THIS WORK
 * WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. CONSULT THE END USER LICENSE
 * AGREEMENT FOR INFORMATION ON ADDITIONAL RESTRICTIONS.
 */

package com.comerzzia.pos.services.ticket;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.core.empresas.EmpresaBean;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.persistence.tickets.datosfactura.DatosFactura;
import com.comerzzia.pos.services.core.tiendas.Tienda;
import com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket;
import com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket;
import com.comerzzia.pos.services.ticket.lineas.ILineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;
import com.comerzzia.pos.services.ticket.pagos.IPagoTicket;

@SuppressWarnings("rawtypes")
@XmlAccessorType(XmlAccessType.NONE)
public abstract class Ticket<L extends ILineaTicket, P extends IPagoTicket, C extends ICabeceraTicket> implements ITicket {
	protected String schemaVersion;	
	protected String softwareVersion;	
	protected String localCopyVersion;
	
	public Ticket() {		
	}
	
	@Override
    public String getSchemaVersion() {
		return schemaVersion;
	}

	@Override
	public void setSchemaVersion(String schemaVersion) {
		this.schemaVersion = schemaVersion;
	}	
	
	@Override
    public String getSoftwareVersion() {
		return softwareVersion;
	}

	@Override
	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}

	@Override
	public String getLocalCopyVersion() {
		return localCopyVersion;
	}

	@Override
	public void setLocalCopyVersion(String localCopyVersion) {
		this.localCopyVersion = localCopyVersion;
	}

	public String getUidTicket() {
		return getCabecera().getUidTicket();
	}

	public String getCodCaja() {
		return getCabecera().getCodCaja();
	}

	public Tienda getTienda() {
		return getCabecera().getTienda();
	}

	public String getUidActividad() {
		return getCabecera().getUidActividad();
	}

	public String getUidDiarioCaja() {
		return getCabecera().getUidDiarioCaja();
	}

	public EmpresaBean getEmpresa() {
		return getCabecera().getEmpresa();
	}

	public ITotalesTicket getTotales() {
		return getCabecera().getTotales();
	}

	public Date getFecha() {
		return getCabecera().getFecha();
	}

	public Long getIdTicket() {
		return getCabecera().getIdTicket();
	}

	public ClienteBean getCliente() {
		return getCabecera().getCliente();
	}

	public UsuarioBean getCajero() {
		return getCabecera().getCajero();
	}

	public void setFecha(Date date) {
		getCabecera().setFecha(date);
	}

	public void setFechaContable(Date fechaContable) {
		getCabecera().setFechaContable(fechaContable);
	}

	public void setIdTicket(Long idTicket) {
		getCabecera().setIdTicket(idTicket);
	}

	public void setCliente(ClienteBean cliente) {
		getCabecera().setCliente(cliente);
	}

	public void setDatosFacturacion(DatosFactura datosFactura) {
		getCabecera().getCliente().setDatosFactura(datosFactura);
	}

	public DatosFactura getDatosFacturacion() {
		return getCabecera().getCliente().getDatosFactura();
	}

	public void setCajero(UsuarioBean cajero) {
		getCabecera().setCajero(cajero);
	}

	public abstract C getCabecera();

	/**
	 * Vuelve a establecer los parámetros del ticket que dependen de la caja y tienda en la que se va a completar el
	 * ticket
	 */
	public void setParametrosTicketAparcado() {
		getCabecera().setParametrosTicketAparcado();
	}

	public abstract List<L> getLineas();

	public abstract void setLineas(List lineas);

	public abstract List<P> getPagos();

	public abstract void setPagos(List pagos);

	/**
	 * @param idLinea
	 * @return
	 */

	public abstract LineaTicketAbstract getLinea(Integer idLinea);

}