package com.comerzzia.dinosol.pos.services.ticket.sorteos;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ParticipacionesDTO {

	@XmlElement(name = "participaciones_concedidas")
	private int participacionesObtenidas;

	@XmlElement(name = "participaciones_extra")
	private int participacionesExtras;

	@XmlElement(name = "participaciones_premiadas")
	private int participacionesPremiadas;

	@XmlElementWrapper(name = "participaciones_entregadas")
	@XmlElement(name = "participacion")
	private List<ParticipacionDTO> listaParticipaciones;

	@XmlElement(name = "nombre_sorteo")
	private String nombreSorteo;

	@XmlElement
	private String textoTicketSinPremioSinApp;
	
	@XmlElement
	private String textoTicketConPremioSinApp;
	
	@XmlElement
	private String textoTicketIdentificadoTicketFisico;
	
	@XmlElement
	private String textoTicketIncidenciaTecnica;

	@XmlElement(name = "enlace_qr")
	private String enlaceQr;

	@XmlTransient
	private String textoVisorConPremioSinApp;

	@XmlTransient
	private String textoVisorSinPremioSinApp;

	@XmlTransient
	private String textoVisorIdentificadoTicketFisico;

	@XmlTransient
	private String textoVisorIdentificadoTicketDigital;

	@XmlTransient
	private String textoVisorIncidenciaTecnica;

	public String getTextoVisorConPremioSinApp() {
		return textoVisorConPremioSinApp;
	}

	public void setTextoVisorConPremioSinApp(String textoVisorConPremioSinApp) {
		this.textoVisorConPremioSinApp = textoVisorConPremioSinApp;
	}

	public String getTextoVisorSinPremioSinApp() {
		return textoVisorSinPremioSinApp;
	}

	public void setTextoVisorSinPremioSinApp(String textoVisorSinPremioSinApp) {
		this.textoVisorSinPremioSinApp = textoVisorSinPremioSinApp;
	}

	public String getTextoVisorIdentificadoTicketFisico() {
		return textoVisorIdentificadoTicketFisico;
	}

	public void setTextoVisorIdentificadoTicketFisico(String textoVisorIdentificadoTicketFisico) {
		this.textoVisorIdentificadoTicketFisico = textoVisorIdentificadoTicketFisico;
	}

	public String getTextoVisorIdentificadoTicketDigital() {
		return textoVisorIdentificadoTicketDigital;
	}

	public void setTextoVisorIdentificadoTicketDigital(String textoVisorIdentificadoTicketDigital) {
		this.textoVisorIdentificadoTicketDigital = textoVisorIdentificadoTicketDigital;
	}

	public String getTextoVisorIncidenciaTecnica() {
		return textoVisorIncidenciaTecnica;
	}

	public void setTextoVisorIncidenciaTecnica(String textoVisorIncidenciaTecnica) {
		this.textoVisorIncidenciaTecnica = textoVisorIncidenciaTecnica;
	}

	public String getEnlaceQr() {
		return enlaceQr;
	}

	public void setEnlaceQr(String enlaceQr) {
		this.enlaceQr = enlaceQr;
	}

	public ParticipacionesDTO() {
		listaParticipaciones = new ArrayList<ParticipacionDTO>();
	}

	public int getParticipacionesObtenidas() {
		return participacionesObtenidas;
	}

	public void setParticipacionesObtenidas(int participacionesObtenidas) {
		this.participacionesObtenidas = participacionesObtenidas;
	}

	public int getParticipacionesPremiadas() {
		return participacionesPremiadas;
	}

	public void setParticipacionesPremiadas(int participacionesPremiadas) {
		this.participacionesPremiadas = participacionesPremiadas;
	}

	public List<ParticipacionDTO> getListaParticipaciones() {
		return listaParticipaciones;
	}

	public void setListaParticipaciones(List<ParticipacionDTO> listaParticipaciones) {
		this.listaParticipaciones = listaParticipaciones;
	}

	public int getParticipacionesExtras() {
		return participacionesExtras;
	}

	public void setParticipacionesExtras(int participacionesExtras) {
		this.participacionesExtras = participacionesExtras;
	}

	public String getNombreSorteo() {
		return nombreSorteo;
	}

	public void setNombreSorteo(String nombreSorteo) {
		this.nombreSorteo = nombreSorteo;
	}

	public String getTextoTicketSinPremioSinApp() {
		return textoTicketSinPremioSinApp;
	}

	public void setTextoTicketSinPremioSinApp(String textoTicketSinPremioSinApp) {
		this.textoTicketSinPremioSinApp = textoTicketSinPremioSinApp;
	}

	public String getTextoTicketConPremioSinApp() {
		return textoTicketConPremioSinApp;
	}

	public void setTextoTicketConPremioSinApp(String textoTicketConPremioSinApp) {
		this.textoTicketConPremioSinApp = textoTicketConPremioSinApp;
	}

	public String getTextoTicketIdentificadoTicketFisico() {
		return textoTicketIdentificadoTicketFisico;
	}

	public void setTextoTicketIdentificadoTicketFisico(String textoTicketIdentificadoTicketFisico) {
		this.textoTicketIdentificadoTicketFisico = textoTicketIdentificadoTicketFisico;
	}

	public String getTextoTicketIncidenciaTecnica() {
		return textoTicketIncidenciaTecnica;
	}

	public void setTextoTicketIncidenciaTecnica(String textoTicketIncidenciaTecnica) {
		this.textoTicketIncidenciaTecnica = textoTicketIncidenciaTecnica;
	}

}
