package com.comerzzia.cardoso.pos.devices.dispositivo.tarjeta.conexflow;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.dispositivo.tarjeta.conexflow.metodosconexion.TefConexflowUSBManager;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosPeticionPagoTarjeta;

@Component
public class CardosoTESTTefConexflow extends TefConexflowUSBManager {
	
	private Logger log = Logger.getLogger(CardosoTESTTefConexflow.class);
	
	protected final String TOTALES = "TOTALES";
	
	@Override
	public void setConfiguration(PaymentMethodConfiguration configuration) {
		super.setConfiguration(configuration);
		
		try {
			log.debug("setConfiguration() - Realizando conexión de prueba para inicializar dispositivo.");
			
			conecta();
		}
		catch (Exception e) {
			log.error("setConfiguration() - Ha habido un error al realizar la conexión previa: " + e.getMessage(), e);
		}
	}

	@Override
	protected String enviarPeticion(String peticion) {
		try {
			log.debug("enviarPeticion() - Enviando el siguiente mensaje al servicio VisualPlugin: " + peticion);
	
			DataInputStream entrada;
			DataOutputStream salida;
			byte buffer[] = new byte[50000];
	
			entrada = new DataInputStream(socket.getInputStream());
			salida = new DataOutputStream(socket.getOutputStream());
	
			salida.write(peticion.getBytes());
	
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
			int s;
			do {
				s = entrada.read(buffer);
				if (s != -1) {
					baos.write(buffer, baos.size(), s);
				}
			} while (s >= buffer.length);
	
			byte resultado[] = baos.toByteArray();
	
			String respuesta = new String(resultado, "UTF-8");
			log.debug("enviarPeticion() - Se ha recibido la siguiente respuesta del servicio VisualPlugin: " + respuesta);
	
			return respuesta;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected String generarPeticion(String tipo, DatosPeticionPagoTarjeta datosPeticion) {
		log.debug("generarPeticion() - Generando trama de petición");
		
		Date fechaActual = new Date();
		SimpleDateFormat formatFecha = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat formatHora = new SimpleDateFormat("HHmmss");
		String fechaFormateada = formatFecha.format(fechaActual);
		String horaFormateada = formatHora.format(fechaActual);
		
		String importeTransaccion = "";
		String idTicket = "";

		String peticion = "PE01FN0002";
		switch (tipo) {
			case VENTA:
				// Añadimos un identificador por delante para diferenciar ventas de devoluciones
				intTransactionID = "0" + intTransactionID;
				importeTransaccion = datosPeticion.getImporte().multiply(new BigDecimal(100)).toBigInteger().toString();
				idTicket = datosPeticion.getIdDocumento().toString();
				peticion = peticion  + "03" + generarSegmentoPeticion("E0", empresa + tienda + numeroTPV)
				        + generarSegmentoPeticion("E1", importeTransaccion)
				        + generarSegmentoPeticion("E3", idTicket)
				        + generarSegmentoPeticion("F3", fechaFormateada)
				        + generarSegmentoPeticion("F4", horaFormateada)
						+ generarSegmentoPeticion("EM", intTransactionID);
				break;
			case DEVOLUCION:
				// Añadimos un identificador por delante para diferenciar ventas de devoluciones
				intTransactionID = "1" + intTransactionID;
				importeTransaccion = datosPeticion.getImporte().abs().multiply(new BigDecimal(100)).toBigInteger().toString();
				idTicket = datosPeticion.getIdDocumento().toString();
				
				peticion = peticion + "04" + generarSegmentoPeticion("E0", empresa + tienda + numeroTPV)
				        + generarSegmentoPeticion("E1", importeTransaccion)
				        + generarSegmentoPeticion("E3", idTicket)
				        + generarSegmentoPeticion("F3", fechaFormateada)
				        + generarSegmentoPeticion("F4", horaFormateada)
						+ generarSegmentoPeticion("EM", intTransactionID);
				break;
			case ANULACION:
				peticion = peticion + "05" + generarSegmentoPeticion("E0", empresa + tienda + numeroTPV)
						+ generarSegmentoPeticion("E1", importeTransaccion)
				        + generarSegmentoPeticion("F3", fechaFormateada)
				        + generarSegmentoPeticion("F4", horaFormateada)
						+ generarSegmentoPeticion("EM", intTransactionID);
				break;
			case TOTALES:
				peticion = "PE04FN000225" + generarSegmentoPeticion("E9", fechaFormateada);
			default:
				break;
		}
		
		peticion = "P" + StringUtils.leftPad(Integer.toString(peticion.length()), 5, '0') + peticion;
		log.debug("generarPeticion() - Trama generada: " + peticion);
		
		return peticion;
	}

}
