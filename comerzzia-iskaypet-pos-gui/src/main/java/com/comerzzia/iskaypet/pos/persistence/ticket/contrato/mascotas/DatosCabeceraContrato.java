package com.comerzzia.iskaypet.pos.persistence.ticket.contrato.mascotas;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.contrato.ContratoAnimalDto;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.laws.ContractLaw;
import com.comerzzia.pos.services.core.tiendas.Tienda;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class DatosCabeceraContrato {
	private String codArt;
	private String desArt;
	private boolean enviado;
	private BooleanProperty selected = new SimpleBooleanProperty(false);
	private String especie;
	private String raza;
	private String peso;
	private String sexo;
	private String tipoIden;
	private String numIden;
	private String precio;
	private String nombre;
	private String docIdentidad;
	private String direccion;
	private String tlf = "", email = "", prefijo = "";
	private Tienda tienda;
	private String datosTienda;
	private String correoTienda;
	private String fechaContrato;
	private String cp;
	private String localidad;
	private String provincia;
	private String poblacion;
	private String codLenguaje;

	private List<ContractLaw> lstLaws;

	// GAP 116 GUARDADO LOCAL DEL PDF CONTRATO
	private ContratoAnimalDto contratoAnimal;
	private byte[] contratoRecuperad;
	private int idLinea;
	private String uidTicket;

	public DatosCabeceraContrato() {

	}

	public DatosCabeceraContrato(ContratoAnimalDto contrato) {
		this.contratoAnimal=contrato;
		this.nombre =contrato.getNombre() + " " + contrato.getApellidos();
		this.docIdentidad = contrato.getDocumento();
		this.direccion = contrato.getDireccion();
		//lustrum 128891
		this.cp = contrato.getCp();
		this.poblacion = contrato.getPoblacion();
		this.localidad = contrato.getLocalidad();
		this.provincia = contrato.getProvincia();
		//lustrum 128891
		this.tlf = contrato.getTlf();
		this.email = contrato.getEmail();						

		
		// Los datos anteiorioes son comunes para todos los contratos que contenga la venta
		// Las mascotas son por cada linea de mascotas que contega Mascota
		this.especie = contrato.getEspecie();
		this.raza = contrato.getRaza();
		this.peso = contrato.getPeso() == null ? "" : contrato.getPeso() ;
	
		this.sexo = contrato.getSexo();
		this.tipoIden = contrato.getTipoIden();
		this.numIden = contrato.getNumIden();
		this.prefijo = contrato.getPrefijo();
	}

	public String getEspecie() {
		return especie;
	}

	public void setEspecie(String especie) {
		this.especie = especie;
	}

	public String getRaza() {
		return raza;
	}

	public void setRaza(String raza) {
		this.raza = raza;
	}

	public String getPeso() {
		return peso;
	}

	public void setPeso(String peso) {
		this.peso = peso;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getTipoIden() {
		return tipoIden;
	}

	public void setTipoIden(String tipoIden) {
		this.tipoIden = tipoIden;
	}

	public String getNumIden() {
		return numIden;
	}

	public void setNumIden(String numIden) {
		this.numIden = numIden;
	}

	public String getPrecio() {
		return precio;
	}

	public void setPrecio(String precio) {
		this.precio = precio;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDocIdentidad() {
		return docIdentidad;
	}

	public void setDocIdentidad(String docIdentidad) {
		this.docIdentidad = docIdentidad;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getTlf() {
		return tlf;
	}

	public void setTlf(String tlf) {
		this.tlf = tlf;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Tienda getTienda() {
		return tienda;
	}

	public void setTienda(Tienda tienda) {
		this.tienda = tienda;
	}

	public String getDatosTienda() {
		return datosTienda;
	}

	public void setDatosTienda(String datosTienda) {
		this.datosTienda = datosTienda;
	}

	public String getCorreoTienda() {
		return correoTienda;
	}

	public void setCorreoTienda(String correoTienda) {
		this.correoTienda = correoTienda;
	}

	public String getFechaContrato() {
		return fechaContrato;
	}

	public void setFechaContrato(String fechaContrato) {
		this.fechaContrato = fechaContrato;
	}

	public String getCodArt() {
		return codArt;
	}

	public void setCodArt(String codArt) {
		this.codArt = codArt;
	}

	public boolean isEnviado() {
		return enviado;
	}

	public void setEnviado(boolean enviado) {
		this.enviado = enviado;
	}

	public String getDesArt() {
		return desArt;
	}

	public void setDesArt(String desArt) {
		this.desArt = desArt;
	}

	public SimpleStringProperty getCodigoProperty() {
		return new SimpleStringProperty(StringUtils.isNotBlank(codArt) ? codArt : "");
	}

	public SimpleStringProperty getDescripcionProperty() {
		return new SimpleStringProperty(StringUtils.isNotBlank(desArt) ? desArt : "");
	}

	public SimpleStringProperty getEnviadoProperty() {
		return new SimpleStringProperty(enviado ? "SI" : "NO");
	}

	public boolean isSelected() {
		return selected.get();
	}

	public void setSelected(boolean selected) {
		this.selected.set(selected);
	}

	public BooleanProperty selectedProperty() {
		return selected;
	}

	public BooleanProperty getSelected() {
		return selected;
	}

	public void setSelected(BooleanProperty selected) {
		this.selected = selected;
	}

	public List<ContractLaw> getLstLaws() {
		return lstLaws;
	}

	public void setLstLaws(List<ContractLaw> lstLaws) {
		this.lstLaws = lstLaws;
	}

	public ContratoAnimalDto getContratoAnimal() {
		return contratoAnimal;
	}

	public void setContratoAnimal(ContratoAnimalDto contratoAnimal) {
		this.contratoAnimal = contratoAnimal;
	}

	public byte[] getContratoRecuperad() {
		return contratoRecuperad;
	}

	public void setContratoRecuperad(byte[] contratoRecuperad) {
		this.contratoRecuperad = contratoRecuperad;
	}

	public String getPrefijo() {
		return prefijo;
	}

	public void setPrefijo(String prefijo) {
		this.prefijo = prefijo;
	}

	public String getCp() {
		return cp;
	}

	public void setCp(String cp) {
		this.cp = cp;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getPoblacion() {
		return poblacion;
	}

	public void setPoblacion(String poblacion) {
		this.poblacion = poblacion;
	}

	public String getCodLenguaje() {
		return codLenguaje;
	}

	public void setCodLenguaje(String codLenguaje) {
		this.codLenguaje = codLenguaje;
	}

	public int getIdLinea() {
		return idLinea;
	}

	public void setIdLinea(int idLinea) {
		this.idLinea = idLinea;
	}

	public String getUidTicket() {
		return uidTicket;
	}

	public void setUidTicket(String uidTicket) {
		this.uidTicket = uidTicket;
	}
	
}
