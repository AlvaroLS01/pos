package com.comerzzia.cardoso.pos.services.ticket.cabecera.adicionales;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * GAP XX - REALIZAR DEVOLUCIONES SIN DOCUMENTO ORIGEN 
 * Bean creado para poder pasar los datos de documento origen para la devolución, saltando la validación 
 * que se realiza en la clase de ServicioTicketsImpl - existeDocumentoOrigen() 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DatosOrigenTicketBean{

	@XmlElement(name = "uid_ticket")
	private String uidTicket;
	@XmlElement(name = "numero_factura")
	private Long numFactura;
	@XmlElement(name = "cod_caja")
	private String codCaja;
	@XmlElement(name = "cod_almacen")
	private String codAlmacen;
	@XmlElement(name = "serie")
	private String serie;
	@XmlElement(name = "cod_ticket")
	private String codTicket;
	@XmlElement(name = "id_tipo_documento")
	private Long idTipoDocumento;
	@XmlElement(name = "cod_tipo_documento")
	private String codTipoDocumento;
	@XmlElement(name = "des_tipo_documento")
	private String desTipoDocumento;

	public String getUidTicket(){
		return uidTicket;
	}

	public void setUidTicket(String uidTicket){
		this.uidTicket = uidTicket;
	}

	public Long getNumFactura(){
		return numFactura;
	}

	public void setNumFactura(Long numFactura){
		this.numFactura = numFactura;
	}

	public String getCodCaja(){
		return codCaja;
	}

	public void setCodCaja(String codCaja){
		this.codCaja = codCaja;
	}

	public String getCodAlmacen(){
		return codAlmacen;
	}

	public void setCodAlmacen(String codAlmacen){
		this.codAlmacen = codAlmacen;
	}

	public String getSerie(){
		return serie;
	}

	public void setSerie(String serie){
		this.serie = serie;
	}

	public String getCodTicket(){
		return codTicket;
	}

	public void setCodTicket(String codTicket){
		this.codTicket = codTicket;
	}

	public Long getIdTipoDocumento(){
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(Long idTipoDocumento){
		this.idTipoDocumento = idTipoDocumento;
	}

	public String getCodTipoDocumento(){
		return codTipoDocumento;
	}

	public void setCodTipoDocumento(String codTipoDocumento){
		this.codTipoDocumento = codTipoDocumento;
	}

	public String getDesTipoDocumento(){
		return desTipoDocumento;
	}

	public void setDesTipoDocumento(String desTipoDocumento){
		this.desTipoDocumento = desTipoDocumento;
	}

}
