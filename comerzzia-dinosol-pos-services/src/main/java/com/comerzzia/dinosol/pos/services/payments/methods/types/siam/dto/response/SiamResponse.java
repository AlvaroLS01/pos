package com.comerzzia.dinosol.pos.services.payments.methods.types.siam.dto.response;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class SiamResponse {

	private static final Logger log = Logger.getLogger(SiamResponse.class);

	private static final CharSequence CODIGO_CREDITO = "CRE";
	private static final CharSequence CODIGO_DEBITO = "DEB";
	private static final CharSequence CODIGO_EURO = "0978";

	protected String estado;

	protected SiamMensajeResponse mensaje;

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public SiamMensajeResponse getMensaje() {
		return mensaje;
	}

	public void setMensaje(SiamMensajeResponse mensaje) {
		this.mensaje = mensaje;
	}

	public Map crearMapaParametrosSiamResponse() {
		log.debug("crearMapaDatosRespuesta() - creando mapa de campos y longitud");

		Map<String, Integer> mapaDatosRespuesta = new LinkedHashMap<>();
		mapaDatosRespuesta.put("estado", 1);
		mapaDatosRespuesta.put("codigoRespuesta", 2);
		mapaDatosRespuesta.put("centroAutorizador", 1);
		mapaDatosRespuesta.put("secuencia", 6);
		mapaDatosRespuesta.put("autorizacion", 6);
		mapaDatosRespuesta.put("comercio", 15);
		mapaDatosRespuesta.put("importe", 12);
		mapaDatosRespuesta.put("transaccion", 2);
		mapaDatosRespuesta.put("hora", 6);
		mapaDatosRespuesta.put("nombreTitular", 26);
		mapaDatosRespuesta.put("binTarjeta", 6);
		mapaDatosRespuesta.put("indicadorTipoLectura", 1);
		mapaDatosRespuesta.put("relleno", 1);
		mapaDatosRespuesta.put("tipoDispositivoContacless", 2);
		mapaDatosRespuesta.put("identCentroProcesador", 1);
		mapaDatosRespuesta.put("panEnmascarado", 19);
		mapaDatosRespuesta.put("tarjetaEMV", 1);
		mapaDatosRespuesta.put("modoEntradaDatos", 1);
		mapaDatosRespuesta.put("modoVerificacionUsuario", 1);
		mapaDatosRespuesta.put("ARC", 4);
		mapaDatosRespuesta.put("AID", 32);
		mapaDatosRespuesta.put("panSequenceNumber", 4);
		mapaDatosRespuesta.put("applicationLabel", 32);
		mapaDatosRespuesta.put("codigoMoneda", 4);
		mapaDatosRespuesta.put("indicadorCreDeb", 3);
		mapaDatosRespuesta.put("transparencia", 150);
		mapaDatosRespuesta.put("DCC", 1);
		mapaDatosRespuesta.put("monedaDCC", 3);
		mapaDatosRespuesta.put("importeDCC", 12);
		mapaDatosRespuesta.put("exchangeRate", 7);
		mapaDatosRespuesta.put("markUp", 7);
		mapaDatosRespuesta.put("commision", 7);
		mapaDatosRespuesta.put("entidad", 20);
		mapaDatosRespuesta.put("textoTicket", 458);

		return mapaDatosRespuesta;
	}

	public void setearDatosRespuesta(String msgRespuesta) {
		log.debug("setearDatosRespuesta() - seteando los datos de la respuesta");

		boolean tieneIndicacador = false;
		boolean tieneMoneda = false;

		estado = msgRespuesta.substring(0, 1);
		mensaje = new SiamMensajeResponse();
		msgRespuesta = msgRespuesta.substring(1);
		Map<String, Integer> mapaParametros = crearMapaParametrosSiamResponse();

		for (Map.Entry<String, Integer> entry : mapaParametros.entrySet()) {
			String nombreCampo = entry.getKey();

			if (nombreCampo.equals("estado")) {
				continue;
			}

			if (StringUtils.isNotBlank(mensaje.getDCC()) && !mensaje.isDCC()) {
				continue;
			}

			// en este punto comprobamos si tiene el distribuidor de tarjeta relleno (Visa, Mastercard, etc...)
			// con esto sabemos que el siguiente campo, es el codigo de moneda, este puede venir vacio por tanto tenemos
			// que
			// añadir al string (4) espacios en blanco, en el caso de venga relleno, hemos comprobado previamente si el
			// indicador de tipo
			// de tarjeta viene o no, ya que tambien puede venir en blanco, en el caso de que tengamos el código pero no
			// el indicador (1º if)
			// añadimos los espacios en blanco (3) para añadirlos al mensaje
			if (StringUtils.isNotBlank(mensaje.applicationLabel)) {
				if (msgRespuesta.contains(CODIGO_CREDITO) || msgRespuesta.contains(CODIGO_DEBITO)) {
					tieneIndicacador = true;
				}
				// no viene indicador añadimos los espacios (3)
				if (!tieneIndicacador && msgRespuesta.contains(CODIGO_EURO)) {
					msgRespuesta = msgRespuesta.replace(CODIGO_EURO, CODIGO_EURO + "   ");
					tieneIndicacador = true;
				} // no viene indicador ni código de moneda por tanto añadimos los espacios de ambos (7)
				else if (!tieneIndicacador && !tieneMoneda && !msgRespuesta.contains(CODIGO_EURO)) {
					msgRespuesta = "       " + msgRespuesta;
				}
			}

			int longitud = entry.getValue();

			log.debug("seteando campo " + nombreCampo + " con longitud " + longitud);

			if (StringUtils.isNotBlank(msgRespuesta) && msgRespuesta.length() >= longitud) {

				// Extraer la porción de texto correspondiente a la longitud del campo
				String valorCampo = msgRespuesta.substring(0, longitud);

				// Establecer el valor del campo en la respuestaMensaje con respeto a los espacios
				try {
					Field campo = SiamMensajeResponse.class.getDeclaredField(nombreCampo);
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
