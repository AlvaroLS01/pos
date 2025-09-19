package com.comerzzia.bimbaylola.pos.services.ticket.aparcados.reservas;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.bimbaylola.pos.util.xml.MyMapAdapter;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosPeticionPagoTarjeta;

@Component
@Scope("prototype")
@XmlRootElement(name = "datosRespuestaPagoTarjeta")
@XmlAccessorType(XmlAccessType.FIELD)
public class DatosRespuestaTarjetaReservaTicket{
	
	protected DatosPeticionPagoTarjeta datosPeticion; 
	
    protected String codResult;
    protected String msgRespuesta;
    protected String ticket;
    protected String tipoOp;
    protected String importe;
    protected String AID;
    protected String tarjeta;
    protected String marcaTarjeta;
    protected String descripcionMarcaTarjeta;
    protected String titular;
    protected String comercio;
    protected String terminal;
    protected String descBanco;
    protected String ARC;
    protected String numOperacionBanco;
    protected String codAutorizacion;
    protected String moneda;
    protected String numOperacion;
    protected String tipoLectura;
    protected String verificacion;
    protected String numTransaccion;
    
    protected String pos;
    protected String codigoCentro;
    protected String codigoTienda;
    protected String codigoCajera;
    protected String tipoTransaccion;
    protected String fuc;
    protected String nombreEntidad;
    protected String PAN;
    protected String P23;
    protected String applicationLabel;
    protected String fechaTransaccion;
    protected String establecimiento;
    protected String direccionEstablecimiento;
    protected String terminalId;
    protected String authMode;
    protected String contactLess;
    protected String nombredf;

    protected boolean pedirFirma;
    
    @XmlJavaTypeAdapter(MyMapAdapter.class)
    protected Map<String, String> adicionales;

    protected String importeDivisa;
    protected String codigoDivisa;
    protected String exchangeRate;
    protected String comision;
    protected boolean DCC;
    
	public DatosPeticionPagoTarjeta getDatosPeticion(){
		return datosPeticion;
	}
	public void setDatosPeticion(DatosPeticionPagoTarjeta datosPeticion){
		this.datosPeticion = datosPeticion;
	}
	public String getCodResult(){
		return codResult;
	}
	public void setCodResult(String codResult){
		this.codResult = codResult;
	}
	public String getMsgRespuesta(){
		return msgRespuesta;
	}
	public void setMsgRespuesta(String msgRespuesta){
		this.msgRespuesta = msgRespuesta;
	}
	public String getTicket(){
		return ticket;
	}
	public void setTicket(String ticket){
		this.ticket = ticket;
	}
	public String getTipoOp(){
		return tipoOp;
	}
	public void setTipoOp(String tipoOp){
		this.tipoOp = tipoOp;
	}
	public String getImporte(){
		return importe;
	}
	public void setImporte(String importe){
		this.importe = importe;
	}
	public String getAID(){
		return AID;
	}
	public void setAID(String aID){
		AID = aID;
	}
	public String getTarjeta(){
		return tarjeta;
	}
	public void setTarjeta(String tarjeta){
		this.tarjeta = tarjeta;
	}
	public String getMarcaTarjeta(){
		return marcaTarjeta;
	}
	public void setMarcaTarjeta(String marcaTarjeta){
		this.marcaTarjeta = marcaTarjeta;
	}
	public String getDescripcionMarcaTarjeta(){
		return descripcionMarcaTarjeta;
	}
	public void setDescripcionMarcaTarjeta(String descripcionMarcaTarjeta){
		this.descripcionMarcaTarjeta = descripcionMarcaTarjeta;
	}
	public String getTitular(){
		return titular;
	}
	public void setTitular(String titular){
		this.titular = titular;
	}
	public String getComercio(){
		return comercio;
	}
	public void setComercio(String comercio){
		this.comercio = comercio;
	}
	public String getTerminal(){
		return terminal;
	}
	public void setTerminal(String terminal){
		this.terminal = terminal;
	}
	public String getDescBanco(){
		return descBanco;
	}
	public void setDescBanco(String descBanco){
		this.descBanco = descBanco;
	}
	public String getARC(){
		return ARC;
	}
	public void setARC(String aRC){
		ARC = aRC;
	}
	public String getNumOperacionBanco(){
		return numOperacionBanco;
	}
	public void setNumOperacionBanco(String numOperacionBanco){
		this.numOperacionBanco = numOperacionBanco;
	}
	public String getCodAutorizacion(){
		return codAutorizacion;
	}
	public void setCodAutorizacion(String codAutorizacion){
		this.codAutorizacion = codAutorizacion;
	}
	public String getMoneda(){
		return moneda;
	}
	public void setMoneda(String moneda){
		this.moneda = moneda;
	}
	public String getNumOperacion(){
		return numOperacion;
	}
	public void setNumOperacion(String numOperacion){
		this.numOperacion = numOperacion;
	}
	public String getTipoLectura(){
		return tipoLectura;
	}
	public void setTipoLectura(String tipoLectura){
		this.tipoLectura = tipoLectura;
	}
	public String getVerificacion(){
		return verificacion;
	}
	public void setVerificacion(String verificacion){
		this.verificacion = verificacion;
	}
	public String getNumTransaccion(){
		return numTransaccion;
	}
	public void setNumTransaccion(String numTransaccion){
		this.numTransaccion = numTransaccion;
	}
	public String getPos(){
		return pos;
	}
	public void setPos(String pos){
		this.pos = pos;
	}
	public String getCodigoCentro(){
		return codigoCentro;
	}
	public void setCodigoCentro(String codigoCentro){
		this.codigoCentro = codigoCentro;
	}
	public String getCodigoTienda(){
		return codigoTienda;
	}
	public void setCodigoTienda(String codigoTienda){
		this.codigoTienda = codigoTienda;
	}
	public String getCodigoCajera(){
		return codigoCajera;
	}
	public void setCodigoCajera(String codigoCajera){
		this.codigoCajera = codigoCajera;
	}
	public String getTipoTransaccion(){
		return tipoTransaccion;
	}
	public void setTipoTransaccion(String tipoTransaccion){
		this.tipoTransaccion = tipoTransaccion;
	}
	public String getFuc(){
		return fuc;
	}
	public void setFuc(String fuc){
		this.fuc = fuc;
	}
	public String getNombreEntidad(){
		return nombreEntidad;
	}
	public void setNombreEntidad(String nombreEntidad){
		this.nombreEntidad = nombreEntidad;
	}
	public String getPAN(){
		return PAN;
	}
	public void setPAN(String pAN){
		PAN = pAN;
	}
	public String getP23(){
		return P23;
	}
	public void setP23(String p23){
		P23 = p23;
	}
	public String getApplicationLabel(){
		return applicationLabel;
	}
	public void setApplicationLabel(String applicationLabel){
		this.applicationLabel = applicationLabel;
	}
	public String getFechaTransaccion(){
		return fechaTransaccion;
	}
	public void setFechaTransaccion(String fechaTransaccion){
		this.fechaTransaccion = fechaTransaccion;
	}
	public String getEstablecimiento(){
		return establecimiento;
	}
	public void setEstablecimiento(String establecimiento){
		this.establecimiento = establecimiento;
	}
	public String getDireccionEstablecimiento(){
		return direccionEstablecimiento;
	}
	public void setDireccionEstablecimiento(String direccionEstablecimiento){
		this.direccionEstablecimiento = direccionEstablecimiento;
	}
	public String getTerminalId(){
		return terminalId;
	}
	public void setTerminalId(String terminalId){
		this.terminalId = terminalId;
	}
	public String getAuthMode(){
		return authMode;
	}
	public void setAuthMode(String authMode){
		this.authMode = authMode;
	}
	public String getContactLess(){
		return contactLess;
	}
	public void setContactLess(String contactLess){
		this.contactLess = contactLess;
	}
	public String getNombredf(){
		return nombredf;
	}
	public void setNombredf(String nombredf){
		this.nombredf = nombredf;
	}
	public boolean isPedirFirma(){
		return pedirFirma;
	}
	public void setPedirFirma(boolean pedirFirma){
		this.pedirFirma = pedirFirma;
	}
	public Map<String, String> getAdicionales(){
		return adicionales;
	}
	public void setAdicionales(Map<String, String> adicionales){
		this.adicionales = adicionales;
	}
	public String getImporteDivisa(){
		return importeDivisa;
	}
	public void setImporteDivisa(String importeDivisa){
		this.importeDivisa = importeDivisa;
	}
	public String getCodigoDivisa(){
		return codigoDivisa;
	}
	public void setCodigoDivisa(String codigoDivisa){
		this.codigoDivisa = codigoDivisa;
	}
	public String getExchangeRate(){
		return exchangeRate;
	}
	public void setExchangeRate(String exchangeRate){
		this.exchangeRate = exchangeRate;
	}
	public String getComision(){
		return comision;
	}
	public void setComision(String comision){
		this.comision = comision;
	}
	public boolean isDCC(){
		return DCC;
	}
	public void setDCC(boolean dCC){
		DCC = dCC;
	}
    
}
