/**
 * ComerZZia 3.0
 *
 * Copyright (c) 2008-2015 Comerzzia, S.L.  All Rights Reserved.
 *
 * THIS WORK IS  SUBJECT  TO  SPAIN  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 * TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 * EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 *
 * CONSULT THE END USER LICENSE AGREEMENT FOR INFORMATION ON ADDITIONAL
 * RESTRICTIONS.
 */


package com.comerzzia.pos.services.ticket.cabecera;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.springframework.beans.factory.annotation.Autowired;

import com.comerzzia.core.servicios.ContextHolder;
import com.comerzzia.core.servicios.documents.LocatorManager;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.core.empresas.EmpresaBean;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.persistence.giftcard.GiftCardBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.tiendas.Tienda;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;


@XmlAccessorType(XmlAccessType.NONE)
public abstract class CabeceraTicketAbstract <S extends ISubtotalIvaTicket, T extends ITotalesTicket> implements ICabeceraTicket {
	@Autowired
	LocatorManager locatorManager;
	
	@XmlTransient
    protected ITicket ticket;    
	@XmlTransient
    protected String uidTicket;    
	@XmlTransient
    protected String uidTicketEnlace;        
	@XmlTransient
    protected Long idTicket;    
	@XmlTransient
    protected String codTicket;    
	@XmlTransient
    protected String serieTicket;
	@XmlTransient
    protected String fecha;
	@XmlTransient
    protected String uidActividad;    
	@XmlTransient
    protected Long tipoDocumento;
	@XmlTransient
    protected String codTipoDocumento;
	@XmlTransient
    protected String desTipoDocumento;
	@XmlTransient
    protected String formatoImpresion;
	@XmlTransient
    protected String formatoImpresionTicketRegalo;
	@XmlTransient
    protected Tienda tienda;
	@XmlTransient
    protected String codCaja;
	@XmlTransient
    protected String uidDiarioCaja;
	@XmlTransient
    protected ClienteBean cliente;
	@XmlTransient
    protected ClienteBean datosEnvio;
	@XmlTransient
    protected FidelizacionBean datosFidelizado;
	@XmlTransient
    protected UsuarioBean cajero;
	@XmlTransient
    protected EmpresaBean empresa;
	@XmlTransient
    protected T totales;
	@XmlTransient
    protected List<S> subtotalesIva;
	@XmlTransient
    protected FirmaTicket firma;	
	@XmlTransient
    protected String localizador;
	@XmlTransient
    protected DatosDocumentoOrigenTicket datosDocOrigen;
	@XmlTransient
    protected TarjetaRegaloTicket tarjetaRegalo;

    public CabeceraTicketAbstract() {
        super();
        subtotalesIva = new ArrayList<>();
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#inicializarCabecera(com.comerzzia.pos.services.ticket.ITicket)
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
        
        firma = new FirmaTicket();
    }
    
    
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getTicket()
	 */
    @Override
	public ITicket getTicket() {
        return ticket;
    }
    
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setTicket(com.comerzzia.pos.services.ticket.Ticket)
	 */
    @Override
	public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getUidTicket()
	 */
    @Override
	public String getUidTicket() {
        return uidTicket;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getCodCaja()
	 */
    @Override
	public String getCodCaja() {
        return codCaja;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getTienda()
	 */
    @Override
	public Tienda getTienda() {
        return tienda;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getUidActividad()
	 */
    @Override
	public String getUidActividad() {
        return uidActividad;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getUidDiarioCaja()
	 */
    @Override
	public String getUidDiarioCaja() {
        return uidDiarioCaja;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getEmpresa()
	 */
    @Override
	public EmpresaBean getEmpresa() {
        return empresa;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getTotales()
	 */

    @Override
	public abstract T getTotales();
    
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setTotales(com.comerzzia.pos.services.ticket.cabecera.TotalesTicket)
	 */

    @Override
	public void setTotales(ITotalesTicket total){
    	totales = (T) total;
    }
    	
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getFecha()
	 */
    @Override
	public Date getFecha() {
    	if (fecha == null) {
			return null;
		}
        return FormatUtil.getInstance().desformateaFechaHoraTicket(fecha);
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getIdTicket()
	 */
    @Override
	public Long getIdTicket() {
        return idTicket;
    }
        
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getIdTicketAsString()
	 */
    @Override
	public String getIdTicketAsString(){
        return FormatUtil.getInstance().completarCerosIzquierda(idTicket, 10);
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getCliente()
	 */
    @Override
	public ClienteBean getCliente() {
        return cliente;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getCajero()
	 */
    @Override
	public UsuarioBean getCajero() {
        return cajero;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setFecha(java.util.Date)
	 */
    @Override
	public void setFecha(Date fecha) {
        this.fecha = FormatUtil.getInstance().formateaFechaHoraTicket(fecha);
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setIdTicket(java.lang.Long)
	 */
    @Override
	public void setIdTicket(Long idTicket) {
        this.idTicket = idTicket;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setCliente(com.comerzzia.pos.persistence.clientes.ClienteBean)
	 */
    @Override
	public void setCliente(ClienteBean cliente) {
        this.cliente = cliente;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setCajero(com.comerzzia.pos.persistence.core.usuarios.UsuarioBean)
	 */
    @Override
	public void setCajero(UsuarioBean cajero) {
        this.cajero = cajero;
    }
    
    // Setters para el unmarshall
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setTienda(com.comerzzia.pos.services.core.tiendas.Tienda)
	 */
    @Override
	public void setTienda(Tienda tienda) {
        this.tienda = tienda;
    }
    
     /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setEmpresa(com.comerzzia.pos.persistence.core.empresas.EmpresaBean)
	 */
    @Override
	public void setEmpresa(EmpresaBean empresa) {
        this.empresa = empresa;
    }
    

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getSubtotalesIva()
	 */
    	    
	public abstract List<S> getSubtotalesIva();
	
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getFechaAsLocale()
	 */
    @Override
	public String getFechaAsLocale(){
        
        Date fecha = getFecha();
        if(fecha==null){
            fecha = new Date();
        }
        String fechaTicket = FormatUtil.getInstance().formateaFechaCorta(fecha);
        String horaTicket = FormatUtil.getInstance().formateaHora(fecha);
        return fechaTicket + " " + horaTicket;
    }

    /* (non-Javadoc)
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

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getUidTicketEnlace()
	 */
    @Override
	public String getUidTicketEnlace() {
        return uidTicketEnlace;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setUidTicketEnlace(java.lang.String)
	 */
    @Override
	public void setUidTicketEnlace(String uidTicketEnlace) {
        this.uidTicketEnlace = uidTicketEnlace;
    }
    
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setDocumento(com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean)
	 */
    @Override
	public void setDocumento (TipoDocumentoBean doc){
        tipoDocumento = doc.getIdTipoDocumento();
        codTipoDocumento = doc.getCodtipodocumento();
        formatoImpresion = doc.getFormatoImpresion();
        desTipoDocumento = doc.getDestipodocumento();
        formatoImpresionTicketRegalo = doc.getFormatoImpresionTicketRegalo();
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getTipoDocumento()
	 */
    @Override
	public Long getTipoDocumento() {
        return tipoDocumento;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setTipoDocumento(java.lang.Long)
	 */
    @Override
	public void setTipoDocumento(Long tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getCodTipoDocumento()
	 */
    @Override
	public String getCodTipoDocumento() {
        return codTipoDocumento;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setCodTipoDocumento(java.lang.String)
	 */
    @Override
	public void setCodTipoDocumento(String codTipoDocumento) {
        this.codTipoDocumento = codTipoDocumento;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getFormatoImpresion()
	 */
    @Override
	public String getFormatoImpresion() {
        return formatoImpresion;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setFormatoImpresion(java.lang.String)
	 */
    @Override
	public void setFormatoImpresion(String formatoImpresion) {
        this.formatoImpresion = formatoImpresion;
    }

	/* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getFormatoImpresionTicketRegalo()
	 */
	@Override
	public String getFormatoImpresionTicketRegalo() {
		return formatoImpresionTicketRegalo;
	}

	/* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setFormatoImpresionTicketRegalo(java.lang.String)
	 */
	@Override
	public void setFormatoImpresionTicketRegalo(String formatoImpresionTicketRegalo) {
		this.formatoImpresionTicketRegalo = formatoImpresionTicketRegalo;
	}

	/* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getSerieTicket()
	 */
	@Override
	public String getSerieTicket() {
		return serieTicket;
	}

	/* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setSerieTicket(java.lang.String)
	 */
	@Override
	public void setSerieTicket(String serieTicket) {
		this.serieTicket = serieTicket;
	}

	/* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getCodTicket()
	 */
	@Override
	public String getCodTicket() {
		return codTicket;
	}

	/* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setCodTicket(java.lang.String)
	 */
	@Override
	public void setCodTicket(String codTicket) {
		this.codTicket = codTicket;
	}

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getDatosDocOrigen()
	 */
    @Override
	public DatosDocumentoOrigenTicket getDatosDocOrigen() {
        return datosDocOrigen;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setDatosDocOrigen(com.comerzzia.pos.services.ticket.cabecera.DatosDocumentoOrigenTicket)
	 */
    @Override
	public void setDatosDocOrigen(DatosDocumentoOrigenTicket datosDocOrigen) {
        this.datosDocOrigen = datosDocOrigen;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getFirma()
	 */
    @Override
	public FirmaTicket getFirma() {
        return firma;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setFirma(com.comerzzia.pos.services.ticket.cabecera.FirmaTicket)
	 */
    @Override
	public void setFirma(FirmaTicket firma) {
        this.firma = firma;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getDatosFidelizado()
	 */
    @Override
	public FidelizacionBean getDatosFidelizado() {
        return datosFidelizado;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setDatosFidelizado(com.comerzzia.pos.persistence.fidelizacion.DatosFidelizadoBean)
	 */
//    @Override
//	public void setDatosFidelizado(FidelizacionBean datosFidelizado) {
//        this.datosFidelizado = datosFidelizado;
//    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setDatosFidelizado(com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean)
	 */
    @Override
	public void setDatosFidelizado(FidelizacionBean tarjeta) {
    	datosFidelizado = tarjeta;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setDatosFidelizado(java.lang.String)
	 */
    @Override
	public void setDatosFidelizado(String numTarjeta) {
    	if(numTarjeta != null){
	    	datosFidelizado = new FidelizacionBean();
	        datosFidelizado.setNumTarjetaFidelizado(numTarjeta);
    	}else{
    		datosFidelizado = null;
    	}
    }
    
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setTarjetaRegalo(com.comerzzia.pos.services.ticket.cabecera.TarjetaRegaloTicket)
	 */
    @Override
	public void setTarjetaRegalo(TarjetaRegaloTicket tarjetaRegalo){
    	this.tarjetaRegalo = tarjetaRegalo;
    }
    
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getTarjetaRegalo()
	 */
    @Override
	public TarjetaRegaloTicket getTarjetaRegalo(){
    	return this.tarjetaRegalo;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#agnadirTarjetaRegalo(com.comerzzia.pos.persistence.giftcard.GiftCardBean)
	 */
    @Override
	public void agnadirTarjetaRegalo(GiftCardBean tarjeta){

    	tarjetaRegalo = new TarjetaRegaloTicket();
    	tarjetaRegalo.setImporteRecarga(tarjeta.getImporteRecarga());
    	tarjetaRegalo.setNumTarjetaRegalo(tarjeta.getNumTarjetaRegalo());
    	tarjetaRegalo.setSaldoProvisional(tarjeta.getSaldoProvisional());
    	tarjetaRegalo.setSaldo(tarjeta.getSaldo());
    	tarjetaRegalo.setUidTransaccion(tarjeta.getUidTransaccion());
    }

	/* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getDesTipoDocumento()
	 */
	@Override
	public String getDesTipoDocumento() {
		return desTipoDocumento;
	}
	
	@Override
	public void setLocalizador(String localizador){
		this.localizador = localizador;
	}
            
     /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getLocalizador()
	 */
    @Override
	public String getLocalizador(){     
    	if ( this.localizador == null && this.getFecha() != null) {
    		HashMap<String, Object> params = new HashMap<>();

    		params.put("fechaOperacion", this.getFecha());
    		params.put("codAlmacen", this.getTienda().getCodAlmacen());
    		params.put("idTicket", this.getIdTicket().toString());
    		params.put("uidTicket", this.getUidTicket());
    		params.put("codTicket", this.getCodTicket());
    		params.put("codCaja", this.getCodCaja());
    		
    		this.localizador = ContextHolder.get().getBean(LocatorManager.class).encode(params);     		    	
    	}
    	
    	return this.localizador;
     }

	/* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#getDatosEnvio()
	 */
	@Override
	public ClienteBean getDatosEnvio() {
		return datosEnvio;
	}

	/* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket#setDatosEnvio(com.comerzzia.pos.persistence.clientes.ClienteBean)
	 */
	@Override
	public void setDatosEnvio(ClienteBean datosEnvio) {
		this.datosEnvio = datosEnvio;
	}
     
	@Override
	public abstract Boolean esVenta();
}
