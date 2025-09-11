package com.comerzzia.iskaypet.pos.persistence.ticket.comprasfidelizado;

import java.math.BigDecimal;
import java.util.Date;

@SuppressWarnings("serial")
public class ComprasFidelizado extends ComprasFidelizadoKey {
    private String codemp;

    private Integer periodo;

    private String codserie;

    private Long numalb;

    private Date fecha;

    private String hora;

    private Date fechaSuministro;

    private String codalm;

    private String codcaja;

    private String uidDiarioCaja;

    private String codaplicacion;

    private String codconalm;

    private String codcli;

    private String codtar;

    private Long idTipoPorte;

    private String personaContacto;

    private String referenciaCliente;

    private Integer idGrupoImpuestos;

    private Long idTratImpuestos;

    private BigDecimal base;

    private BigDecimal impuestos;

    private BigDecimal total;

    private String observaciones;

    private String usuario;

    private Long idFacturaRep;

    private Long idFacturaSop;

    private String tarjetaFidelizacion;

    private String trackingPorte;

    private String uidTicket;

    private Long idTipoDocumento;

    private String codAlbaran;

    private String codAlbaranOrigen;

    private Long idTipoDocumentoOrigen;

    private String serieAlbaran;

    private String coddivisa;

    private String descli;

    private String domicilio;

    private String poblacion;

    private String provincia;

    private String localidad;

    private String cp;

    private String codpais;

    private String codtipoiden;

    private String cif;

    private String codcliFactura;

    private String deposito;

    private String codtrans;

    private Long idAccionEstadosLog;

    private Long idEstadoLog;

    private String uidExpedicion;

    private String codalmDestino;

    private String preciosConImpuestos;
    
    private String tienda;
    
    private String tipoDoc;
    
    private String locatorId;
    
    private Long idTicket;
    
    private String firma;
    
    private String serieTicket;

    public String getCodemp() {
        return codemp;
    }

    public void setCodemp(String codemp) {
        this.codemp = codemp == null ? null : codemp.trim();
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public String getCodserie() {
        return codserie;
    }

    public void setCodserie(String codserie) {
        this.codserie = codserie == null ? null : codserie.trim();
    }

    public Long getNumalb() {
        return numalb;
    }

    public void setNumalb(Long numalb) {
        this.numalb = numalb;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora == null ? null : hora.trim();
    }

    public Date getFechaSuministro() {
        return fechaSuministro;
    }

    public void setFechaSuministro(Date fechaSuministro) {
        this.fechaSuministro = fechaSuministro;
    }

    public String getCodalm() {
        return codalm;
    }

    public void setCodalm(String codalm) {
        this.codalm = codalm == null ? null : codalm.trim();
    }

    public String getCodcaja() {
        return codcaja;
    }

    public void setCodcaja(String codcaja) {
        this.codcaja = codcaja == null ? null : codcaja.trim();
    }

    public String getUidDiarioCaja() {
        return uidDiarioCaja;
    }

    public void setUidDiarioCaja(String uidDiarioCaja) {
        this.uidDiarioCaja = uidDiarioCaja == null ? null : uidDiarioCaja.trim();
    }

    public String getCodaplicacion() {
        return codaplicacion;
    }

    public void setCodaplicacion(String codaplicacion) {
        this.codaplicacion = codaplicacion == null ? null : codaplicacion.trim();
    }

    public String getCodconalm() {
        return codconalm;
    }

    public void setCodconalm(String codconalm) {
        this.codconalm = codconalm == null ? null : codconalm.trim();
    }

    public String getCodcli() {
        return codcli;
    }

    public void setCodcli(String codcli) {
        this.codcli = codcli == null ? null : codcli.trim();
    }

    public String getCodtar() {
        return codtar;
    }

    public void setCodtar(String codtar) {
        this.codtar = codtar == null ? null : codtar.trim();
    }

    public Long getIdTipoPorte() {
        return idTipoPorte;
    }

    public void setIdTipoPorte(Long idTipoPorte) {
        this.idTipoPorte = idTipoPorte;
    }

    public String getPersonaContacto() {
        return personaContacto;
    }

    public void setPersonaContacto(String personaContacto) {
        this.personaContacto = personaContacto == null ? null : personaContacto.trim();
    }

    public String getReferenciaCliente() {
        return referenciaCliente;
    }

    public void setReferenciaCliente(String referenciaCliente) {
        this.referenciaCliente = referenciaCliente == null ? null : referenciaCliente.trim();
    }

    public Integer getIdGrupoImpuestos() {
        return idGrupoImpuestos;
    }

    public void setIdGrupoImpuestos(Integer idGrupoImpuestos) {
        this.idGrupoImpuestos = idGrupoImpuestos;
    }

    public Long getIdTratImpuestos() {
        return idTratImpuestos;
    }

    public void setIdTratImpuestos(Long idTratImpuestos) {
        this.idTratImpuestos = idTratImpuestos;
    }

    public BigDecimal getBase() {
        return base;
    }

    public void setBase(BigDecimal base) {
        this.base = base;
    }

    public BigDecimal getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(BigDecimal impuestos) {
        this.impuestos = impuestos;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones == null ? null : observaciones.trim();
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario == null ? null : usuario.trim();
    }

    public Long getIdFacturaRep() {
        return idFacturaRep;
    }

    public void setIdFacturaRep(Long idFacturaRep) {
        this.idFacturaRep = idFacturaRep;
    }

    public Long getIdFacturaSop() {
        return idFacturaSop;
    }

    public void setIdFacturaSop(Long idFacturaSop) {
        this.idFacturaSop = idFacturaSop;
    }

    public String getTarjetaFidelizacion() {
        return tarjetaFidelizacion;
    }

    public void setTarjetaFidelizacion(String tarjetaFidelizacion) {
        this.tarjetaFidelizacion = tarjetaFidelizacion == null ? null : tarjetaFidelizacion.trim();
    }

    public String getTrackingPorte() {
        return trackingPorte;
    }

    public void setTrackingPorte(String trackingPorte) {
        this.trackingPorte = trackingPorte == null ? null : trackingPorte.trim();
    }

    public String getUidTicket() {
        return uidTicket;
    }

    public void setUidTicket(String uidTicket) {
        this.uidTicket = uidTicket == null ? null : uidTicket.trim();
    }

    public Long getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(Long idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }

    public String getCodAlbaran() {
        return codAlbaran;
    }

    public void setCodAlbaran(String codAlbaran) {
        this.codAlbaran = codAlbaran == null ? null : codAlbaran.trim();
    }

    public String getCodAlbaranOrigen() {
        return codAlbaranOrigen;
    }

    public void setCodAlbaranOrigen(String codAlbaranOrigen) {
        this.codAlbaranOrigen = codAlbaranOrigen == null ? null : codAlbaranOrigen.trim();
    }

    public Long getIdTipoDocumentoOrigen() {
        return idTipoDocumentoOrigen;
    }

    public void setIdTipoDocumentoOrigen(Long idTipoDocumentoOrigen) {
        this.idTipoDocumentoOrigen = idTipoDocumentoOrigen;
    }

    public String getSerieAlbaran() {
        return serieAlbaran;
    }

    public void setSerieAlbaran(String serieAlbaran) {
        this.serieAlbaran = serieAlbaran == null ? null : serieAlbaran.trim();
    }

    public String getCoddivisa() {
        return coddivisa;
    }

    public void setCoddivisa(String coddivisa) {
        this.coddivisa = coddivisa == null ? null : coddivisa.trim();
    }

    public String getDescli() {
        return descli;
    }

    public void setDescli(String descli) {
        this.descli = descli == null ? null : descli.trim();
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio == null ? null : domicilio.trim();
    }

    public String getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion == null ? null : poblacion.trim();
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia == null ? null : provincia.trim();
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad == null ? null : localidad.trim();
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp == null ? null : cp.trim();
    }

    public String getCodpais() {
        return codpais;
    }

    public void setCodpais(String codpais) {
        this.codpais = codpais == null ? null : codpais.trim();
    }

    public String getCodtipoiden() {
        return codtipoiden;
    }

    public void setCodtipoiden(String codtipoiden) {
        this.codtipoiden = codtipoiden == null ? null : codtipoiden.trim();
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif == null ? null : cif.trim();
    }

    public String getCodcliFactura() {
        return codcliFactura;
    }

    public void setCodcliFactura(String codcliFactura) {
        this.codcliFactura = codcliFactura == null ? null : codcliFactura.trim();
    }

    public String getDeposito() {
        return deposito;
    }

    public void setDeposito(String deposito) {
        this.deposito = deposito == null ? null : deposito.trim();
    }

    public String getCodtrans() {
        return codtrans;
    }

    public void setCodtrans(String codtrans) {
        this.codtrans = codtrans == null ? null : codtrans.trim();
    }

    public Long getIdAccionEstadosLog() {
        return idAccionEstadosLog;
    }

    public void setIdAccionEstadosLog(Long idAccionEstadosLog) {
        this.idAccionEstadosLog = idAccionEstadosLog;
    }

    public Long getIdEstadoLog() {
        return idEstadoLog;
    }

    public void setIdEstadoLog(Long idEstadoLog) {
        this.idEstadoLog = idEstadoLog;
    }

    public String getUidExpedicion() {
        return uidExpedicion;
    }

    public void setUidExpedicion(String uidExpedicion) {
        this.uidExpedicion = uidExpedicion == null ? null : uidExpedicion.trim();
    }

    public String getCodalmDestino() {
        return codalmDestino;
    }

    public void setCodalmDestino(String codalmDestino) {
        this.codalmDestino = codalmDestino == null ? null : codalmDestino.trim();
    }

    public String getPreciosConImpuestos() {
        return preciosConImpuestos;
    }

    public void setPreciosConImpuestos(String preciosConImpuestos) {
        this.preciosConImpuestos = preciosConImpuestos == null ? null : preciosConImpuestos.trim();
    }
	
	public String getTienda() {
		return tienda;
	}
	
	public void setTienda(String tienda) {
		this.tienda = tienda;
	}
	
	public String getTipoDoc() {
		return tipoDoc;
	}
	
	public void setTipoDoc(String tipoDoc) {
		this.tipoDoc = tipoDoc;
	}

	public String getLocatorId() {
		return locatorId;
	}
	
	public void setLocatorId(String locatorId) {
		this.locatorId = locatorId;
	}

	public Long getIdTicket() {
		return idTicket;
	}

	public void setIdTicket(Long idTicket) {
		this.idTicket = idTicket;
	}

	public String getFirma() {
		return firma;
	}
	
	public void setFirma(String firma) {
		this.firma = firma;
	}

	public String getSerieTicket() {
		return serieTicket;
	}

	public void setSerieTicket(String serieTicket) {
		this.serieTicket = serieTicket;
	}    
	
}