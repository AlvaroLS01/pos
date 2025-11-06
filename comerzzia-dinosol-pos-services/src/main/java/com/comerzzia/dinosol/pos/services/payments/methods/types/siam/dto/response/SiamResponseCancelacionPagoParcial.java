package com.comerzzia.dinosol.pos.services.payments.methods.types.siam.dto.response;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SiamResponseCancelacionPagoParcial {
	
	private static final Logger log = Logger.getLogger(SiamResponseCancelacionPagoParcial.class);
	
	protected String estado;
	protected SiamMensajeResponseCancelacionPagoParcial mensaje;
	
	public String getEstado() {
		return estado;
	}
	
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	public SiamMensajeResponseCancelacionPagoParcial getMensaje() {
		return mensaje;
	}
	
	public void setMensaje(SiamMensajeResponseCancelacionPagoParcial mensaje) {
		this.mensaje = mensaje;
	}
	
	public Map crearMapaDatosRespuesta() {
		log.debug("crearMapaDatosRespuesta() - creando mapa de campos y longitud");

		Map<String, Integer> mapaDatosRespuesta = new LinkedHashMap<>();
		mapaDatosRespuesta.put("estado", 1);
		mapaDatosRespuesta.put("codigoRespuesta", 2);
		mapaDatosRespuesta.put("centroAutorizador", 1);
		mapaDatosRespuesta.put("secuencia", 6);

		return mapaDatosRespuesta;
	}
	
	public void setearDatosRespuestaCancelacionPago(String msgRespuesta) {
		log.debug("setearDatosRespuestaCancelacionPago() - seteando los datos de la respuesta");

		mensaje = new SiamMensajeResponseCancelacionPagoParcial();
		
		Map<String, Integer> mapaDatosRespuesta = crearMapaDatosRespuesta();
		mapaDatosRespuesta.put("mensajeError00", msgRespuesta.length()-6);
		
		if(msgRespuesta.startsWith("KO")) {
			msgRespuesta = "5".concat(msgRespuesta);
		}
		estado = msgRespuesta.substring(0, 1);
		
		msgRespuesta = msgRespuesta.substring(1);
		for (Map.Entry<String, Integer> entry : mapaDatosRespuesta.entrySet()) {

			String nombreCampo = entry.getKey();

			if (nombreCampo.equals("estado")) {
				continue;
			}

			int longitud = entry.getValue();
			if (StringUtils.isNotBlank(msgRespuesta) && msgRespuesta.length() >= longitud) {
				// Extraer la porción de texto correspondiente a la longitud del campo
				String valorCampo = msgRespuesta.substring(0, longitud); // Empezando desde el segundo carácter
				// Establecer el valor del campo en la respuestaMensaje con respeto a los espacios
				try {
					Field campo = SiamMensajeResponseCancelacionPagoParcial.class.getDeclaredField(nombreCampo);
					campo.setAccessible(true);
					if (campo.getType().equals(String.class)) {
						campo.set(mensaje, valorCampo);
					}
					else if (campo.getType().equals(BigDecimal.class)) {
						campo.set(mensaje, new BigDecimal(valorCampo));
					}
					else if (campo.getType().equals(Integer.class)) {
						campo.set(mensaje, Integer.parseInt(valorCampo));
					}
				}
				catch (NoSuchFieldException | IllegalAccessException e) {
					log.error("Error a la hora de setear el campo:" + nombreCampo);
				}

				// Reducir la cadena de texto para el próximo campo
				msgRespuesta = msgRespuesta.substring(longitud);

			}
		}
	}
}
