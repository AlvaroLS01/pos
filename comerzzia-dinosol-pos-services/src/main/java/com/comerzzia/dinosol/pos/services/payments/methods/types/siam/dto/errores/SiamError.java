package com.comerzzia.dinosol.pos.services.payments.methods.types.siam.dto.errores;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class SiamError {

	private static final Logger log = Logger.getLogger(SiamError.class);

	protected String estado;
	protected SiamErrorMensaje mensaje;
	protected Map<String, String> mapaErrores;

	public SiamError() {
		super();
		crearMapaErrores();
	}

	public HashMap crearMapaDatosRespuesta(String msgRespuesta) {
		HashMap<String, Integer> mapaDatosRespuesta = new LinkedHashMap<>();
		mapaDatosRespuesta.put("estado", 1);
		mapaDatosRespuesta.put("codigoRespuesta", 2);
		mapaDatosRespuesta.put("centroAutorizador", 1);
		mapaDatosRespuesta.put("codigoError", 2);
		mapaDatosRespuesta.put("mensajeError00", msgRespuesta.length()-6);
		
		return mapaDatosRespuesta;
	}

	public void setearDatosRespuestaError(String msgRespuesta) {
		log.debug("setearDatosRespuestaError() - seteando datos de error " + msgRespuesta);

		mensaje = new SiamErrorMensaje();
		Map<String, Integer> mapaDatosRespuesta = crearMapaDatosRespuesta(msgRespuesta);
		
		//En principio no deberia de venir sin estado nunca, asi que aqui no deberia de entrar, si entra aqui, es que SIAM nos manda la respuesta erronea
		//con esto "forzariamos" el estado cosa que no es recomendable en este caso lo pongo en cursada para esperar la respuesta de SIAM
		if(msgRespuesta.startsWith("KO")) {
			msgRespuesta = "2".concat(msgRespuesta);
		}
		
		estado = msgRespuesta.substring(0, 1);
		msgRespuesta = msgRespuesta.substring(1);
		
		for (Map.Entry<String, Integer> entry : mapaDatosRespuesta.entrySet()) {
			String nombreCampo = entry.getKey();

			if (nombreCampo.equals("estado")) {
				continue;
			}

			int longitud = entry.getValue();

			log.debug("setearDatosRespuestaError() - seteando campo " + nombreCampo + " con longitud " + longitud);

			if (StringUtils.isNotBlank(msgRespuesta) && msgRespuesta.length() >= longitud) {
				// Extraer la porción de texto correspondiente a la longitud del campo
				String valorCampo = msgRespuesta.substring(0, longitud);

				// Establecer el valor del campo en la respuestaMensaje respetando los espacios
				try {
					Field campo = SiamErrorMensaje.class.getDeclaredField(nombreCampo);
					campo.setAccessible(true);
					if (campo.getType().equals(String.class)) {
						campo.set(mensaje, valorCampo);
					}
				}
				catch (NoSuchFieldException | IllegalAccessException e) {
					log.error("Error a la hora de setear el campo: " + nombreCampo);
				}

				// Reducir la cadena de texto para el próximo campo
				msgRespuesta = msgRespuesta.substring(longitud);
			}
		}
	}

	public void crearMapaErrores() {
		mapaErrores = new HashMap<>();
		mapaErrores.put("N0", "Tarjeta invalida");
		mapaErrores.put("NI", "Tarjeta invalida");
		mapaErrores.put("NH", "Su banco no contesta");
		mapaErrores.put("NK", "Denegada por banco emisor");
		mapaErrores.put("NC", "Error en número secreto");
		mapaErrores.put("NP", "Error en número secreto");
		mapaErrores.put("NW", "Límite excedido");
		mapaErrores.put("NE", "Tarjeta caducada");
		mapaErrores.put("ND", "Error datos tarjeta");
		mapaErrores.put("NA", "Error en datos");
		mapaErrores.put("TO", "Terminal ocupado");
		mapaErrores.put("CO", "Centro ocupado");
		mapaErrores.put("CI", "Centro inexistente");
		mapaErrores.put("RL", "Error en conexión");
		mapaErrores.put("AR", "Agotados intentos de conexión");
		mapaErrores.put("ST", "Saturación de red");
		mapaErrores.put("NO", "Numero ocupado");
		mapaErrores.put("CF", "Centro autorizador fuera de servicio");
		mapaErrores.put("VT", "No hay respuesta");
		mapaErrores.put("NL", "No hay línea disponible");
		mapaErrores.put("RM", "Revise modem");
		mapaErrores.put("F1", "Tarjeta no admitida");
		mapaErrores.put("NX", "Fecha incorrecta");
		mapaErrores.put("N7", "Error sistema remoto");
		mapaErrores.put("N6", "Operación no admitida");
		mapaErrores.put("N5", "No hay totales");
		mapaErrores.put("N4", "Error de protocolo");
		mapaErrores.put("N3", "Reintente de nuevo");
		mapaErrores.put("N2", "Error de protocolo");
		mapaErrores.put("N1", "Error en respuesta");
		mapaErrores.put("KK", "Error recarga telefónica");
		mapaErrores.put("00", "Ver mensaje recibido");
		mapaErrores.put("CC", "Ningún centro disponible");
		mapaErrores.put("N9", "Error en envío");
		mapaErrores.put("I1", "No hay sesion establecida");
		mapaErrores.put("I2", "Modem KO");
		mapaErrores.put("I3", "No hay CV's disponibles");
		mapaErrores.put("P1", "Nombre de funcion no valido");
		mapaErrores.put("P2", "Error en la longitud");
		mapaErrores.put("P3", "No hay mensajes pendientes o buffer lleno");
		mapaErrores.put("P4", "Comando rechazado, datos no validos");
		mapaErrores.put("P5", "Conexion en curso");
		mapaErrores.put("P6", "Hay datos en el buffer de lectura");
		mapaErrores.put("P7", "No hay llamadas para esta sesion");
		mapaErrores.put("P8", "Circuito ya establecido");
		mapaErrores.put("P9", "Comando rechazado, identificador de sesion no valido");
		mapaErrores.put("U4", "Problema a nivel de enlace");
		mapaErrores.put("U5", "Recurso OS/2 no disponible");
		mapaErrores.put("U6", "Problema interno (error fatal)");
		mapaErrores.put("U8", "Software X25 no cargado");
		mapaErrores.put("RX", "Error al abrir puerto serie");
		mapaErrores.put("PP", "Error en PIN-PAD");
		mapaErrores.put("Z0", "Pin-Pad no configurado");
		mapaErrores.put("Z1", "Error accediendo Pin-Pad");
		mapaErrores.put("Z2", "Error en conexión con Pin-Pad");
		mapaErrores.put("Z3", "Pin-Pad realizando test");
		mapaErrores.put("Z4", "Error esperando respuesta al MSG2000");
		mapaErrores.put("Z5", "Error esperando respuesta al MSG1000");
		mapaErrores.put("Z6", "Cancelación en Pin-Pad");
		mapaErrores.put("Z7", "Pin-Pad sin inicializar");
		mapaErrores.put("Z8", "Error en MSG1010");
		mapaErrores.put("Z9", "Error datos Chip EMV o cancelación en Pin-Pad");
		mapaErrores.put("ZA", "Denegada por la tarjeta");
		mapaErrores.put("ZB", "Aceptada por la tarjeta");
		mapaErrores.put("ZC", "tarjetas EMV sólo a centros que acepten EMV");
		mapaErrores.put("ZD", "Error en Pin-Pad procesando respuesta EMV");
		mapaErrores.put("F2", "Error en etiquetas EMV");
		mapaErrores.put("ZE", "Realizada carga de parámetros automática en el PinPad, por favor repita la operación");
		mapaErrores.put("ZF", "Error en carga o renovación de claves de PIN");
		mapaErrores.put("ZG", "Resultado de carga de claves automática en el PinPad, por favor repita la operación");
		mapaErrores.put("W0", "Error en carga de claves");
		mapaErrores.put("W1", "Error No existen claves en Host");
		mapaErrores.put("W2", "Error de validación de CI");
		mapaErrores.put("W3", "Error No existen claves en Host");
		mapaErrores.put("W4", "Error en verificación MAC");
		mapaErrores.put("W5", "Error por desincronización de claves de PIN");
		mapaErrores.put("W6", "Error en verificación MAC");
		mapaErrores.put("D1", "Operación original no encontrada");
		mapaErrores.put("D2", "Fecha original anterior al límite");
		mapaErrores.put("D3", "Importe restante inferior al propuesto");
		mapaErrores.put("D4", "Tarjeta diferente a la original");
		mapaErrores.put("00", "Ver mensaje recibido");
		mapaErrores.put("SI", "Secuencia de operaciones no encontrada");
		mapaErrores.put("IX", "Importe de operación original superado");
		mapaErrores.put("II", "Importe original incorrecto");
		mapaErrores.put("TD", "Tarjeta diferente");
		mapaErrores.put("ON", "Original no encontrado");
		
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public SiamErrorMensaje getMensaje() {
		return mensaje;
	}

	public void setMensaje(SiamErrorMensaje mensaje) {
		this.mensaje = mensaje;
	}

	public Map<String, String> getMapaErrores() {
		return mapaErrores;
	}

	public void setMapaErrores(Map<String, String> mapaErrores) {
		this.mapaErrores = mapaErrores;
	}

}
