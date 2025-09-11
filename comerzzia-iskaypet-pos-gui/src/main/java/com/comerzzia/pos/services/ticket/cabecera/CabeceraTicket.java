/**
 * ComerZZia 3.0 Copyright (c) 2008-2015 Comerzzia, S.L. All Rights Reserved. THIS WORK IS SUBJECT TO SPAIN AND
 * INTERNATIONAL COPYRIGHT LAWS AND TREATIES. NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED COPIED,
 * DISTRIBUTED, REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED, LINKED, RECAST,
 * TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION OF THIS WORK
 * WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. CONSULT THE END USER LICENSE
 * AGREEMENT FOR INFORMATION ON ADDITIONAL RESTRICTIONS.
 */

package com.comerzzia.pos.services.ticket.cabecera;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRangoBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.core.empresas.EmpresaBean;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.persistence.giftcard.GiftCardBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.tiendas.Tienda;
import com.comerzzia.pos.services.fiscaldata.FiscalData;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;

@SuppressWarnings("rawtypes")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "uidTicket", "idTicket", "codTicket", "uidActividad", "codCaja", "uidDiarioCaja", "serieTicket", "fecha", "fechaContable", "datosDocOrigen", "tipoDocumento", "codTipoDocumento",
        "desTipoDocumento", "formatoImpresion", "uidTicketEnlace", "cajero", "tienda", "cliente", "datosEnvio", "empresa", "subtotalesIva", "totales", "datosFidelizado", "tarjetaRegalo", "firma",
        "localizador", "ticketsReferenciados", "cantidadArticulos", "configContadorRango", "fiscalData" })
@Component
@Scope("prototype")
public class CabeceraTicket extends CabeceraTicketAbstract {

	@XmlTransient
	protected ITicket ticket;

	@XmlElement(name = "uid_ticket")
	protected String uidTicket;

	@XmlElement(name = "uid_ticket_enlace")
	protected String uidTicketEnlace;

	@XmlElement(name = "id_ticket")
	protected Long idTicket;

	@XmlElement(name = "cod_ticket")
	protected String codTicket;

	@XmlElement(name = "serie_ticket")
	protected String serieTicket;

	protected String fecha;

	@XmlElement(name = "fecha_contable")
	protected String fechaContable;

	@XmlElement(name = "uid_actividad")
	protected String uidActividad;

	@XmlElement(name = "tipo_documento")
	protected Long tipoDocumento;

	@XmlElement(name = "cod_tipo_documento")
	protected String codTipoDocumento;

	@XmlElement(name = "des_tipo_documento")
	protected String desTipoDocumento;

	@XmlElement(name = "formato_impresion")
	protected String formatoImpresion;

	@XmlTransient
	protected String formatoImpresionTicketRegalo;

	protected Tienda tienda;

	@XmlElement(name = "codcaja")
	protected String codCaja;

	@XmlElement(name = "uid_diario_caja")
	protected String uidDiarioCaja;

	protected ClienteBean cliente;

	protected ClienteBean datosEnvio;

	@XmlElement(name = "datos_fidelizacion")
	protected FidelizacionBean datosFidelizado;

	protected UsuarioBean cajero;

	protected EmpresaBean empresa;

	protected TotalesTicket totales;

	@XmlElementWrapper(name = "lineasimpuestos")
	@XmlElement(name = "lineaimpuestos")
	protected List<SubtotalIvaTicket> subtotalesIva;

	@XmlElement(name = "firma")
	protected FirmaTicket firma;

	@XmlElement(name = "datos_documento_origen")
	protected DatosDocumentoOrigenTicket datosDocOrigen;

	@XmlElement(name = "giftcard")
	protected TarjetaRegaloTicket tarjetaRegalo;

	@XmlElement(name = "cantidad_articulos")
	protected BigDecimal cantidadArticulos;

	@XmlElementWrapper(name = "tickets_referenciados")
	@XmlElement(name = "uid_ticket_referencia")
	protected List<String> ticketsReferenciados;

	@XmlTransient
	@Autowired
	protected Documentos documentos;

	@XmlElement(name = "vigencia")
	protected ConfigContadorRangoBean configContadorRango;
	
	@XmlElement(name = "fiscal_data")
	protected FiscalData fiscalData;

	public CabeceraTicket() {
		super();
		subtotalesIva = new ArrayList<>();
		documentos = SpringContext.getBean(Documentos.class);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#inicializarCabecera(com.comerzzia.pos.services.ticket
	 * .ITicket)
	 */
	@Override
	public void inicializarCabecera(ITicket ticket) {
		this.ticket = ticket;
		uidTicket = UUID.randomUUID().toString();
		Sesion sesion = SpringContext.getBean(Sesion.class);
		codCaja = sesion.getAplicacion().getCodCaja();
		tienda = sesion.getAplicacion().getTienda();
		uidActividad = sesion.getAplicacion().getUidActividad();
		uidDiarioCaja = sesion.getSesionCaja().getUidDiarioCaja();
		empresa = sesion.getAplicacion().getEmpresa();
		subtotalesIva = new ArrayList<>();
		datosFidelizado = null;
		tarjetaRegalo = null;
		datosDocOrigen = null;
		ticketsReferenciados = new ArrayList<>();
		firma = new FirmaTicket();
		cantidadArticulos = BigDecimal.ZERO;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getTicket()
	 */
	@Override
	public ITicket getTicket() {
		return ticket;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setTicket(com.comerzzia.pos.services.ticket.Ticket)
	 */
	@Override
	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getUidTicket()
	 */
	@Override
	public String getUidTicket() {
		return uidTicket;
	}

	public void setUidTicket(String uidTicket) {
		this.uidTicket = uidTicket;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getCodCaja()
	 */
	@Override
	public String getCodCaja() {
		return codCaja;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getTienda()
	 */
	@Override
	public Tienda getTienda() {
		return tienda;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getUidActividad()
	 */
	@Override
	public String getUidActividad() {
		return uidActividad;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getUidDiarioCaja()
	 */
	@Override
	public String getUidDiarioCaja() {
		return uidDiarioCaja;
	}

	@Override
	public void setUidDiarioCaja(String uidDiarioCaja) {
		this.uidDiarioCaja = uidDiarioCaja;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getEmpresa()
	 */
	@Override
	public EmpresaBean getEmpresa() {
		return empresa;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getTotales()
	 */
	@Override
	public TotalesTicket getTotales() {
		return totales;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setTotales(com.comerzzia.pos.services.ticket.cabecera
	 * .TotalesTicket)
	 */
	@Override
	public void setTotales(ITotalesTicket totales) {
		this.totales = (TotalesTicket) totales;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getFecha()
	 */
	@Override
	public Date getFecha() {
		if (fecha == null) {
			return null;
		}
		return FormatUtil.getInstance().desformateaFechaHoraTicket(fecha);
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getIdTicket()
	 */
	@Override
	public Long getIdTicket() {
		return idTicket;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getIdTicketAsString()
	 */
	@Override
	public String getIdTicketAsString() {
		return FormatUtil.getInstance().completarCerosIzquierda(idTicket, 10);
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getCliente()
	 */
	@Override
	public ClienteBean getCliente() {
		return cliente;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getCajero()
	 */
	@Override
	public UsuarioBean getCajero() {
		return cajero;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setFecha(java.util.Date)
	 */
	@Override
	public void setFecha(Date fecha) {
		this.fecha = FormatUtil.getInstance().formateaFechaHoraTicket(fecha);
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setIdTicket(java.lang.Long)
	 */
	@Override
	public void setIdTicket(Long idTicket) {
		this.idTicket = idTicket;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setCliente(com.comerzzia.pos.persistence.clientes.
	 * ClienteBean)
	 */
	@Override
	public void setCliente(ClienteBean cliente) {
		this.cliente = cliente;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setCajero(com.comerzzia.pos.persistence.core.usuarios
	 * .UsuarioBean)
	 */
	@Override
	public void setCajero(UsuarioBean cajero) {
		this.cajero = cajero;
	}

	// Setters para el unmarshall
	/*
	 * (non-Javadoc)
	 * @see
	 * com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setTienda(com.comerzzia.pos.services.core.tiendas.
	 * Tienda)
	 */
	@Override
	public void setTienda(Tienda tienda) {
		this.tienda = tienda;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setEmpresa(com.comerzzia.pos.persistence.core.empresas
	 * .EmpresaBean)
	 */
	@Override
	public void setEmpresa(EmpresaBean empresa) {
		this.empresa = empresa;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getSubtotalesIva()
	 */
	public List<SubtotalIvaTicket> getSubtotalesIva() {
		return subtotalesIva;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getFechaAsLocale()
	 */
	@Override
	public String getFechaAsLocale() {

		Date fecha = getFecha();
		if (fecha == null) {
			fecha = new Date();
		}
		String fechaTicket = FormatUtil.getInstance().formateaFechaCorta(fecha);
		String horaTicket = FormatUtil.getInstance().formateaHora(fecha);
		return fechaTicket + " " + horaTicket;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setParametrosTicketAparcado()
	 */
	@Override
	public void setParametrosTicketAparcado() {
		Sesion sesion = SpringContext.getBean(Sesion.class);
		cajero = sesion.getSesionUsuario().getUsuario();
		// codCaja = sesion.getAplicacion().getCodCaja();
		// tienda = sesion.getAplicacion().getTienda();
		// uidActividad = sesion.getAplicacion().getUidActividad();
		// uidDiarioCaja = sesion.getSesionCaja().getUidDiarioCaja();
		// empresa = sesion.getAplicacion().getEmpresa();
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getUidTicketEnlace()
	 */
	@Override
	public String getUidTicketEnlace() {
		return uidTicketEnlace;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setUidTicketEnlace(java.lang.String)
	 */
	@Override
	public void setUidTicketEnlace(String uidTicketEnlace) {
		this.uidTicketEnlace = uidTicketEnlace;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setDocumento(com.comerzzia.pos.persistence.core.documentos
	 * .tipos.TipoDocumentoBean)
	 */
	@Override
	public void setDocumento(TipoDocumentoBean doc) {
		tipoDocumento = doc.getIdTipoDocumento();
		codTipoDocumento = doc.getCodtipodocumento();
		formatoImpresion = doc.getFormatoImpresion();
		desTipoDocumento = doc.getDestipodocumento();
		formatoImpresionTicketRegalo = doc.getFormatoImpresionTicketRegalo();
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getTipoDocumento()
	 */
	@Override
	public Long getTipoDocumento() {
		return tipoDocumento;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setTipoDocumento(java.lang.Long)
	 */
	@Override
	public void setTipoDocumento(Long tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getCodTipoDocumento()
	 */
	@Override
	public String getCodTipoDocumento() {
		return codTipoDocumento;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setCodTipoDocumento(java.lang.String)
	 */
	@Override
	public void setCodTipoDocumento(String codTipoDocumento) {
		this.codTipoDocumento = codTipoDocumento;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getFormatoImpresion()
	 */
	@Override
	public String getFormatoImpresion() {
		return formatoImpresion;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setFormatoImpresion(java.lang.String)
	 */
	@Override
	public void setFormatoImpresion(String formatoImpresion) {
		this.formatoImpresion = formatoImpresion;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getFormatoImpresionTicketRegalo()
	 */
	@Override
	public String getFormatoImpresionTicketRegalo() {
		return formatoImpresionTicketRegalo;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setFormatoImpresionTicketRegalo(java.lang.String)
	 */
	@Override
	public void setFormatoImpresionTicketRegalo(String formatoImpresionTicketRegalo) {
		this.formatoImpresionTicketRegalo = formatoImpresionTicketRegalo;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getSerieTicket()
	 */
	@Override
	public String getSerieTicket() {
		return serieTicket;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setSerieTicket(java.lang.String)
	 */
	@Override
	public void setSerieTicket(String serieTicket) {
		this.serieTicket = serieTicket;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getCodTicket()
	 */
	@Override
	public String getCodTicket() {
		return codTicket;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setCodTicket(java.lang.String)
	 */
	@Override
	public void setCodTicket(String codTicket) {
		this.codTicket = codTicket;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getDatosDocOrigen()
	 */
	@Override
	public DatosDocumentoOrigenTicket getDatosDocOrigen() {
		return datosDocOrigen;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setDatosDocOrigen(com.comerzzia.pos.services.ticket
	 * .cabecera.DatosDocumentoOrigenTicket)
	 */
	@Override
	public void setDatosDocOrigen(DatosDocumentoOrigenTicket datosDocOrigen) {
		this.datosDocOrigen = datosDocOrigen;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getFirma()
	 */
	@Override
	public FirmaTicket getFirma() {
		return firma;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setFirma(com.comerzzia.pos.services.ticket.cabecera
	 * .FirmaTicket)
	 */
	@Override
	public void setFirma(FirmaTicket firma) {
		this.firma = firma;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getDatosFidelizado()
	 */
	@Override
	public FidelizacionBean getDatosFidelizado() {
		return datosFidelizado;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setDatosFidelizado(com.comerzzia.pos.persistence.
	 * fidelizacion.DatosFidelizadoBean)
	 */
	// @Override
	// public void setDatosFidelizado(FidelizacionBean datosFidelizado) {
	// this.datosFidelizado = datosFidelizado;
	// }

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setDatosFidelizado(com.comerzzia.pos.persistence.
	 * fidelizacion.FidelizacionBean)
	 */
	@Override
	public void setDatosFidelizado(FidelizacionBean tarjeta) {
		datosFidelizado = tarjeta;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setDatosFidelizado(java.lang.String)
	 */
	@Override
	public void setDatosFidelizado(String numTarjeta) {
		if (numTarjeta != null) {
			datosFidelizado = new FidelizacionBean();
			datosFidelizado.setNumTarjetaFidelizado(numTarjeta);
		}
		else {
			datosFidelizado = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setTarjetaRegalo(com.comerzzia.pos.services.ticket
	 * .cabecera.TarjetaRegaloTicket)
	 */
	@Override
	public void setTarjetaRegalo(TarjetaRegaloTicket tarjetaRegalo) {
		this.tarjetaRegalo = tarjetaRegalo;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getTarjetaRegalo()
	 */
	@Override
	public TarjetaRegaloTicket getTarjetaRegalo() {
		return this.tarjetaRegalo;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#agnadirTarjetaRegalo(com.comerzzia.pos.persistence
	 * .giftcard.GiftCardBean)
	 */
	@Override
	public void agnadirTarjetaRegalo(GiftCardBean tarjeta) {

		tarjetaRegalo = new TarjetaRegaloTicket();
		tarjetaRegalo.setImporteRecarga(tarjeta.getImporteRecarga());
		tarjetaRegalo.setNumTarjetaRegalo(tarjeta.getNumTarjetaRegalo());
		tarjetaRegalo.setSaldoProvisional(tarjeta.getSaldoProvisional());
		tarjetaRegalo.setSaldo(tarjeta.getSaldo());
		tarjetaRegalo.setUidTransaccion(tarjeta.getUidTransaccion());
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getDesTipoDocumento()
	 */
	@Override
	public String getDesTipoDocumento() {
		return desTipoDocumento;
	}

	/*
	 * (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getDatosEnvio()
	 */
	@Override
	public ClienteBean getDatosEnvio() {
		return datosEnvio;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setDatosEnvio(com.comerzzia.pos.persistence.clientes
	 * .ClienteBean)
	 */
	@Override
	public void setDatosEnvio(ClienteBean datosEnvio) {
		this.datosEnvio = datosEnvio;
	}

	public void addTicketReferenciado(String uidTicketVc) {
		ticketsReferenciados.add(uidTicketVc);
	}

	public List<String> getTicketsReferenciados() {
		return ticketsReferenciados;
	}

	public void setTicketsReferenciados(List<String> ventasCredito) {
		this.ticketsReferenciados = ventasCredito;
	}

	public BigDecimal getCantidadArticulos() {
		return cantidadArticulos;
	}

	public void setCantidadArticulos(BigDecimal cantidadArticulos) {
		this.cantidadArticulos = cantidadArticulos;
	}

	public String getCantidadArticulosAsString() {
		return FormatUtil.getInstance().formateaNumero(cantidadArticulos.abs());
	}

	public ConfigContadorRangoBean getConfigContadorRango() {
		return configContadorRango;
	}

	public void setConfigContadorRango(ConfigContadorRangoBean rango) {
		configContadorRango = rango;
	}

	public Date getFechaContable() {
		return FormatUtil.getInstance().desformateaFechaHoraTicket(fechaContable);
	}

	public void setFechaContable(Date fechaContable) {
		if(fechaContable != null) {
			this.fechaContable = FormatUtil.getInstance().formateaFechaHoraTicket(fechaContable);
		}
	}
	
	@Override
	@XmlElement
	public String getLocalizador() {
		return super.getLocalizador();
	}
	
	public Boolean esVenta(){
    	String codDocumentoAbono = "";
    	Boolean tieneDocumentoAbono = false;
		try {
			tieneDocumentoAbono = documentos.isTieneDocumentoAbonoConfigurado(this.getCodTipoDocumento());
			codDocumentoAbono = documentos.getDocumentoAbono(this.getCodTipoDocumento()).getCodtipodocumento();
		} catch (DocumentoException ignore) {}
		//Si el documento que genero se tiene a si mismo como documento abono 
		if(tieneDocumentoAbono && codDocumentoAbono.equals(this.getCodTipoDocumento())){
			return BigDecimalUtil.isMayorOrIgualACero(this.getTotales().getTotal());
		}else{
			return (!documentos.isDocumentoAbono(this.getCodTipoDocumento())) && BigDecimalUtil.isMayorOrIgualACero(this.getTotales().getTotal());
		}
    }

	@Override
	public FiscalData getFiscalData() {
		return fiscalData;
	}

	@Override
	public void setFiscalData(FiscalData fiscalData) {
		this.fiscalData = fiscalData;
	}

}