package com.comerzzia.bimbaylola.pos.services.ticket.cabecera;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.bimbaylola.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRango;
import com.comerzzia.bimbaylola.pos.persistence.core.contadores.ByLContadorBean;
import com.comerzzia.bimbaylola.pos.persistence.giftcard.ByLGiftCardBean;
import com.comerzzia.bimbaylola.pos.services.epsontse.EposOutput;
import com.comerzzia.bimbaylola.pos.services.impresorafiscal.InformacionFiscal;
import com.comerzzia.bimbaylola.pos.services.spark130f.Spark130FOutput;
import com.comerzzia.bimbaylola.pos.services.vertex.CabeceraVertex;
import com.comerzzia.pos.persistence.giftcard.GiftCardBean;
import com.comerzzia.pos.services.ticket.cabecera.CabeceraTicket;
import com.comerzzia.pos.services.ticket.cabecera.TarjetaRegaloTicket;

@Component
@Scope("prototype")
@Primary
public class ByLCabeceraTicket extends CabeceraTicket {

	@XmlElement(name = "email_ticket_electronico")
	protected String email;

	@XmlTransient
	protected String idTransaccion;

	protected ByLGiftCardBean tarjeta;

	@XmlTransient
	protected ByLContadorBean contador;

	@XmlElement(name = "estado_apartado")
	protected Short estadoApartado;

	@XmlElement(name = "codcanal")
	protected String codCanal;

	@XmlElement(name = "identificador_fiscal")
	protected String identificadorFiscal;

	@XmlElement(name = "vigencia")
	protected ConfigContadorRango configContadorRango;

	@XmlElement(name = "TSE")
	private EposOutput tse;

	@XmlElement(name = "informacion_fiscal")
	private InformacionFiscal informacionFiscal;

	@XmlElement(name = "spark130F")
	private Spark130FOutput spark130F;

	@XmlElement(name = "modo_impuesto")
	protected String modoImpuesto;

	@XmlElement(name = "tax_breakdown_header")
	protected CabeceraVertex cabeceraVertex;

	@XmlTransient
	protected boolean esVertexOnline;

	@XmlElement(name = "TotalNbOfPayments")
	protected Integer totalMeses;

	@XmlElement(name = "fecha_documento_origen")
	protected String fechaDocumentoOrigen;

	@XmlElement(name = "accessCode")
	protected String claveAcceso;

	protected Integer turno;

	@XmlElement(name = "propiedades_documento")
	protected PropiedadesDocumento propiedadesDocumento;

	@XmlElement(name = "propiedades_documento_origen")
	protected PropiedadesDocumento propiedadesDocumentoOrigen;

	@XmlElement(name = "esPagoAnticipo")
	protected String esPagoAnticipo;

	@XmlElement(name = "tipo_id_fiscal_cliente")
	protected String tipoIdFiscalCliente;
	
	@XmlElement(name = "moneda")
	protected String moneda;

	public PropiedadesDocumento getPropiedadesDocumento() {
		return propiedadesDocumento;
	}

	public void setPropiedadesDocumento(PropiedadesDocumento propiedadesDocumento) {
		this.propiedadesDocumento = propiedadesDocumento;
	}

	public PropiedadesDocumento getPropiedadesDocumentoOrigen() {
		return propiedadesDocumentoOrigen;
	}

	public void setPropiedadesDocumentoOrigen(PropiedadesDocumento propiedadesDocumentoOrigen) {
		this.propiedadesDocumentoOrigen = propiedadesDocumentoOrigen;
	}

	public void inicializarCabeceraVertex() {
		cabeceraVertex = new CabeceraVertex();
	}

	public ByLCabeceraTicket() {
		super();
		this.propiedadesDocumento = new PropiedadesDocumento();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIdTransaccion() {
		return idTransaccion;
	}

	public void setIdTransaccion(String idTransaccion) {
		this.idTransaccion = idTransaccion;
	}

	public ByLGiftCardBean getTarjeta() {
		return tarjeta;
	}

	public void setTarjeta(ByLGiftCardBean tarjeta) {
		this.tarjeta = tarjeta;
	}

	@Override
	public void agnadirTarjetaRegalo(GiftCardBean tarjeta) {

		tarjetaRegalo = new TarjetaRegaloTicket();
		tarjetaRegalo.setImporteRecarga(tarjeta.getImporteRecarga());
		tarjetaRegalo.setNumTarjetaRegalo(tarjeta.getNumTarjetaRegalo());
		tarjetaRegalo.setSaldoProvisional(tarjeta.getSaldoProvisional());
		tarjetaRegalo.setSaldo(tarjeta.getSaldo());
		tarjetaRegalo.setUidTransaccion(tarjeta.getUidTransaccion());

		this.tarjeta = (ByLGiftCardBean) tarjeta;
	}

	public String getCronomarcador() {
		String crono = "";
		crono += getTienda().getCodAlmacen();
		crono += getCodCaja();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		crono += sdf.format(getFecha());

		return crono;
	}

	public ByLContadorBean getContador() {
		return contador;
	}

	public void setContador(ByLContadorBean contador) {
		this.contador = contador;
	}

	public Short getEstadoApartado() {
		return estadoApartado;
	}

	public void setEstadoApartado(Short estadoApartado) {
		this.estadoApartado = estadoApartado;
	}

	public String getCodCanal() {
		return codCanal;
	}

	public void setCodCanal(String codCanal) {
		this.codCanal = codCanal;
	}

	public String getIdentificadorFiscal() {
		return identificadorFiscal;
	}

	public void setIdentificadorFiscal(String identificadorFiscal) {
		this.identificadorFiscal = identificadorFiscal;
	}

	public ConfigContadorRango getConfigContadorRango() {
		return configContadorRango;
	}

	public void setConfigContadorRango(ConfigContadorRango configContadorRango) {
		this.configContadorRango = configContadorRango;
	}

	public EposOutput getTse() {
		return tse;
	}

	public void setTse(EposOutput tse) {
		this.tse = tse;
	}

	public InformacionFiscal getInformacionFiscal() {
		return informacionFiscal;
	}

	public void setInformacionFiscal(InformacionFiscal informacionFiscal) {
		this.informacionFiscal = informacionFiscal;
	}

	public Spark130FOutput getSpark130F() {
		return spark130F;
	}

	public void setSpark130F(Spark130FOutput spark130f) {
		spark130F = spark130f;
	}

	public void setModoImpuesto(String modoImpuesto) {
		this.modoImpuesto = modoImpuesto;
	}

	public String getModoImpuesto() {
		return modoImpuesto;
	}

	public CabeceraVertex getCabeceraVertex() {
		return cabeceraVertex;
	}

	public void setCabeceraVertex(CabeceraVertex byLCabeceraVertex) {
		this.cabeceraVertex = byLCabeceraVertex;
	}

	public boolean isEsVertexOnline() {
		return esVertexOnline;
	}

	public void setEsVertexOnline(boolean esVertexOnline) {
		this.esVertexOnline = esVertexOnline;
	}

	@Override
	public Date getFecha() {
		if (fecha == null) {
			return null;
		}
		else {
			return super.getFecha();
		}
	}

	public Integer getTotalMeses() {
		return totalMeses;
	}

	public void setTotalMeses(Integer totalMeses) {
		this.totalMeses = totalMeses;
	}

	public String getFechaDocumentoOrigen() {
		return fechaDocumentoOrigen;
	}

	public void setFechaDocumentoOrigen(String fechaDocumentoOrigen) {
		this.fechaDocumentoOrigen = fechaDocumentoOrigen;
	}

	public String getClaveAcceso() {
		return claveAcceso;
	}

	public void setClaveAcceso(String claveAcceso) {
		this.claveAcceso = claveAcceso;
	}

	public Integer getTurno() {
		return turno;
	}

	public void setTurno(Integer turno) {
		this.turno = turno;
	}

	public String getEsPagoAnticipo() {
		return esPagoAnticipo;
	}

	public void setEsPagoAnticipo(String esPagoAnticipo) {
		this.esPagoAnticipo = esPagoAnticipo;
	}

	public String getTipoIdFiscalCliente() {
		return tipoIdFiscalCliente;
	}

	public void setTipoIdFiscalCliente(String tipoIdFiscalCliente) {
		this.tipoIdFiscalCliente = tipoIdFiscalCliente;
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

}
