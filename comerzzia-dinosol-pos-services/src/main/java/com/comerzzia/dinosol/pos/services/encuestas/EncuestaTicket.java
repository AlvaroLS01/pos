package com.comerzzia.dinosol.pos.services.encuestas;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class EncuestaTicket {

	private Long idEncuesta;

	private List<RespuestaEncuestaTicket> respuestas;

	public EncuestaTicket() {
		super();
		this.respuestas = new ArrayList<RespuestaEncuestaTicket>();
	}

	public Long getIdEncuesta() {
		return idEncuesta;
	}

	public void setIdEncuesta(Long idEncuesta) {
		this.idEncuesta = idEncuesta;
	}

	@XmlElementWrapper
	@XmlElement(name = "respuesta")
	public List<RespuestaEncuestaTicket> getRespuestas() {
		return respuestas;
	}

	public void setRespuestas(List<RespuestaEncuestaTicket> respuestas) {
		this.respuestas = respuestas;
	}

	public void addRespuesta(RespuestaEncuestaTicket respuesta) {
		this.respuestas.add(respuesta);
	}

}
