package com.comerzzia.dinosol.pos.services.mediospago;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.dinosol.pos.services.payments.methods.types.siam.TefSiamManager;
import com.comerzzia.dinosol.pos.services.payments.methods.types.siam.dto.errores.SiamError;
import com.comerzzia.dinosol.pos.services.payments.methods.types.siam.dto.request.cancelarpago.SiamCancelarPagoMensajeRequest;
import com.comerzzia.dinosol.pos.services.payments.methods.types.siam.dto.request.cancelarpago.SiamCancelarPagoRequest;
import com.comerzzia.dinosol.pos.services.payments.methods.types.siam.dto.request.devolucion.SiamDevolucionMensajeRequest;
import com.comerzzia.dinosol.pos.services.payments.methods.types.siam.dto.request.devolucion.SiamDevolucionRequest;
import com.comerzzia.dinosol.pos.services.payments.methods.types.siam.dto.request.venta.SiamVentaMensajeRequest;
import com.comerzzia.dinosol.pos.services.payments.methods.types.siam.dto.request.venta.SiamVentaRequest;
import com.comerzzia.dinosol.pos.services.payments.methods.types.siam.dto.response.SiamMensajeResponse;
import com.comerzzia.dinosol.pos.services.payments.methods.types.siam.dto.response.SiamMensajeResponseCancelacionPagoParcial;
import com.comerzzia.dinosol.pos.services.payments.methods.types.siam.dto.response.SiamResponse;
import com.comerzzia.dinosol.pos.services.payments.methods.types.siam.dto.response.SiamResponseCancelacionPagoParcial;
import com.comerzzia.dinosol.pos.services.ticket.cabecera.DinoCabeceraTicket;
import com.comerzzia.pos.core.dispositivos.dispositivo.tarjeta.TarjetaException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosPeticionPagoTarjeta;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosRespuestaPagoTarjeta;

@SuppressWarnings({ "rawtypes", "deprecation" })
@Service
public class ParseadorTransacciones {

	private static final Logger log = Logger.getLogger(ParseadorTransacciones.class);

	@Autowired
	private Sesion sesion;

	private String idTerminal;
	private String cliente;

	public void init(String idTerminal, String cliente) throws IOException {
		this.idTerminal = idTerminal;
		this.cliente = cliente;
	}

	public String crearPeticion(BigDecimal importe, String tipoPeticion, DatosRespuestaPagoTarjeta datosRespuesta, ITicket ticket, int paymentId) {
		log.debug("crearPeticion() - Tipo de peticion a enviar: " + tipoPeticion);
		String peticion = idTerminal;
		switch (tipoPeticion) {

			// Venta
			case "VT":
				log.debug("crearPeticion() - creando peticion de venta");
				peticion += crearPeticionVenta(importe, ticket, paymentId);
				log.debug("crearPeticion() - Peticion que se va a enviar: " + peticion);
				break;

			// Devolucion
			case "DV":
				log.debug("crearPeticion() - creando peticion de devolución");
				peticion += crearPeticionDevolucion(importe, datosRespuesta, ticket, paymentId);
				log.debug("crearPeticion() - Peticion que se va a enviar: " + peticion);
				break;
				
			// Anulacion de pago 
			case "AN":
				log.debug("crearPeticion() - creando peticion de anulacion de pago");
				peticion += crearPeticionAnulacionPago(datosRespuesta);
				log.debug("crearPeticion() - Peticion que se va a enviar: " + peticion);
				break;

			// Anulacion de pago venta
//			case "AV":
//				log.debug("crearPeticion() - creando peticion de anulacion de pago");
//				peticion += crearPeticionAnulacionPagoVenta(datosRespuesta);
//				log.debug("crearPeticion() - Peticion que se va a enviar: " + peticion);
//				break;
				
//			// Anulacion de pago venta devolucion
//			case "AD":
//				log.debug("crearPeticion() - creando peticion de anulacion de pago");
//				peticion += crearPeticionAnulacionPagoDevolucion(datosRespuesta);
//				log.debug("crearPeticion() - Peticion que se va a enviar: " + peticion);
//				break;

			// Test Pinpad
			case "PP":
				log.debug("crearPeticion() - creando peticion de test de pin-pad");
				peticion += crearPeticionTestPinpad();
				log.debug("crearPeticion() - Peticion que se va a enviar: " + peticion);
				break;

			// Consulta Totales
			case "TC":
				break;

			// Inicializa Terminales
			case "IN":
				log.debug("crearPeticion() - creando peticion de inicializacion de pin-pad");
				peticion += crearPeticionInicializacion();
				log.debug("crearPeticion() - Peticion que se va a enviar: " + peticion);
				break;

			default:
				break;
		}
		return peticion;
	}

	public String crearPeticionTestPinpad() {
		return idTerminal + Estados.PETICION.getCodEstado() + TefSiamManager.TEST_TERMINAL + TefSiamManager.INTEFAZ_EMV;
	}

	public String crearPeticionInicializacion() {
		return idTerminal + Estados.PETICION.getCodEstado() + TefSiamManager.INIT + TefSiamManager.INTEFAZ_INIT;
	}

	public String crearPeticionVenta(BigDecimal importe, ITicket ticket, int paymentId) {
		log.debug("crearPeticionVenta() - enviando peticion a Siam");

		DinoCabeceraTicket cab = (DinoCabeceraTicket) ticket.getCabecera();
		SiamVentaRequest request = new SiamVentaRequest();
		SiamVentaMensajeRequest mr = new SiamVentaMensajeRequest();

		request.setEstado("1");
		mr.setTransaccion(TefSiamManager.VENTA);
		mr.setInterfazEMV(TefSiamManager.INTEFAZ_EMV);
		mr.setCodigoAplazado(Integer.valueOf("00"));
		mr.setImporte(importe);

		if (cab.getFecha() != null) {
			mr.setFechaHoraString(cab.getFechaAsLocale());
		}
		else {
			mr.setFechahora(new Date());
		}

		mr.setCodigoTienda(sesion.getAplicacion().getTienda().getCodAlmacen());
		mr.setCodigoTPV(sesion.getAplicacion().getCodCaja());
		mr.setCodigoTicket(cab.getIdTicket().toString());
		mr.setSecuenciaPago(String.valueOf(comprobarSecuenciaPago(ticket)));
		mr.setCodigoCliente(cliente);

		request.setMensaje(mr);

		log.debug("crearPeticionVenta() - enviamos a SIAM esto" + request.toString());

		return request.toString();
	}

	public String crearPeticionDevolucion(BigDecimal importe, DatosRespuestaPagoTarjeta datosRespuesta, ITicket ticket, int paymentId) {
		log.debug("crearPeticionDevolucion() - enviando peticion a Siam");

		DinoCabeceraTicket cab = (DinoCabeceraTicket) ticket.getCabecera();
		SiamDevolucionRequest request = new SiamDevolucionRequest();
		SiamDevolucionMensajeRequest mr = new SiamDevolucionMensajeRequest();

		request.setEstado("1");
		mr.setTransaccion(TefSiamManager.DEVOLUCION);
		mr.setInterfazEMV(TefSiamManager.INTEFAZ_EMV);
		mr.setCentroAutorizador(datosRespuesta != null ? datosRespuesta.getCodigoCentro() : "0");
		mr.setCodigoAutorizacion(datosRespuesta != null ? datosRespuesta.getCodAutorizacion() : "000000");
		mr.setImporte(importe.negate());

		if (StringUtils.isNotBlank(cab.getFechaAsLocale())) {
			mr.setFechaHoraString(cab.getFechaAsLocale());
		}
		else {
			mr.setFechahora(new Date());
		}

		mr.setCodigoTienda(sesion.getAplicacion().getTienda().getCodAlmacen());
		mr.setCodigoTPV(sesion.getAplicacion().getCodCaja());
		mr.setCodigoTicket(datosRespuesta != null ? datosRespuesta.getDatosPeticion().getIdDocumento().toString() : StringUtils.leftPad(cab.getIdTicket().toString(), 10, "0"));
		mr.setSecuenciaPagoTEFTicket(String.valueOf(comprobarSecuenciaPago(ticket))); // 0 - 1
//		mr.setCodigoCliente(cliente);
		mr.setFechaOriginalAsString(datosRespuesta.getAdicional("fechaOriginal"));
//		mr.setImporteOriginal(ticket.getCabecera().getTotales().getTotal());
		mr.setImporteOriginal(new BigDecimal(datosRespuesta.getAdicional("importeOriginal")));
		mr.setSecuenciaOriginal(datosRespuesta.getAdicional("secuenciaOriginal"));

		request.setMensaje(mr);

		log.debug("crearPeticionDevolucion() - enviamos a SIAM esto" + request.toString());

		return request.toString();
	}

	private int comprobarSecuenciaPago(ITicket ticket) {
		List<PagoTicket> pagos = ((TicketVentaAbono) ticket).getPagos();
		int numero = 0;
		if (!pagos.isEmpty()) {
			for (PagoTicket pagoTicket : pagos) {
				if (pagoTicket.getDatosRespuestaPagoTarjeta() != null) {
					numero = 1;
				}
			}
		}

		return numero;
	}
	
	public String crearPeticionAnulacionPago(DatosRespuestaPagoTarjeta datosRespuesta) {
		log.debug("crearPeticionAnulacionPago() - enviando peticion a Siam");

		SiamCancelarPagoRequest request = new SiamCancelarPagoRequest();
		SiamCancelarPagoMensajeRequest mr = new SiamCancelarPagoMensajeRequest();

		request.setEstado("1");
		mr.setTransaccion(TefSiamManager.CANCEL);
		mr.setCentroAutorizador(datosRespuesta.getCodigoCentro());
		mr.setSecuencia(datosRespuesta != null ? datosRespuesta.getAdicional("secuencia") : "000000");

		request.setMensaje(mr);

		log.debug("crearPeticionDevolucion() - enviamos a SIAM esto" + request.toString());

		return request.toString();
	}

	public String crearPeticionAnulacionPagoVenta(DatosRespuestaPagoTarjeta datosRespuesta) {
		log.debug("crearPeticionAnulacionPago() - enviando peticion a Siam");

		SiamCancelarPagoRequest request = new SiamCancelarPagoRequest();
		SiamCancelarPagoMensajeRequest mr = new SiamCancelarPagoMensajeRequest();

		request.setEstado("1");
		mr.setTransaccion(TefSiamManager.CANCEL_PAY);
		mr.setCentroAutorizador(datosRespuesta.getCodigoCentro());
		mr.setSecuencia(datosRespuesta != null ? datosRespuesta.getAdicional("secuencia") : "000000");

		request.setMensaje(mr);

		log.debug("crearPeticionDevolucion() - enviamos a SIAM esto" + request.toString());

		return request.toString();
	}
	
	public String crearPeticionAnulacionPagoDevolucion(DatosRespuestaPagoTarjeta datosRespuesta) {
		log.debug("crearPeticionAnulacionPago() - enviando peticion a Siam");

		SiamCancelarPagoRequest request = new SiamCancelarPagoRequest();
		SiamCancelarPagoMensajeRequest mr = new SiamCancelarPagoMensajeRequest();

		request.setEstado("1");
		mr.setTransaccion(TefSiamManager.CANCEL_RETURN);
		mr.setCentroAutorizador(datosRespuesta.getCodigoCentro());
		mr.setSecuencia(datosRespuesta != null ? datosRespuesta.getAdicional("secuencia") : "000000");

		request.setMensaje(mr);

		log.debug("crearPeticionDevolucion() - enviamos a SIAM esto" + request.toString());

		return request.toString();
	}

	public DatosRespuestaPagoTarjeta procesarRespuesta(String msgRespuesta, DatosPeticionPagoTarjeta datosPeticion, DatosRespuestaPagoTarjeta datosRespuestaOrigen) throws TarjetaException {

		log.debug("procesarRespuesta() - procesando respuesta recibida " + msgRespuesta);
		SiamResponse response = new SiamResponse();
		response.setearDatosRespuesta(msgRespuesta);
		SiamMensajeResponse mensaje = response.getMensaje();

		DatosRespuestaPagoTarjeta datosRespuesta = new DatosRespuestaPagoTarjeta(datosPeticion);
		Map<String, String> adicionales = new HashMap<>();
		adicionales.put("estado", response.getEstado());
		adicionales.put("mensajeOriginal", msgRespuesta);
		datosRespuesta.setCodResult(mensaje.getCodigoRespuesta());
		datosRespuesta.setMsgRespuesta(mensaje.getCodigoRespuesta());

		if (StringUtils.isNotBlank(datosRespuesta.getMsgRespuesta()) && datosRespuesta.getMsgRespuesta().equals(TefSiamManager.OK)) {

			datosRespuesta.setCodigoCentro(mensaje.getCentroAutorizador());
			adicionales.put("secuencia", mensaje.getSecuencia());
			datosRespuesta.setCodAutorizacion(mensaje.getAutorizacion());
			datosRespuesta.setComercio(mensaje.getComercio());
			datosRespuesta.setImporte(mensaje.getImporte() != null ? mensaje.getImporte().toString() : datosPeticion.getImporte().toString());
			datosRespuesta.setTipoTransaccion(mensaje.getTransaccion());
			datosRespuesta.setHoraBoleta(mensaje.getHora() != null ? convertirNumeroAHora(mensaje.getHora()) : formateoHora(new Date()));
			datosRespuesta.setFechaBoleta(formateoDia(new Date()));
			adicionales.put("nombreTitular", mensaje.getNombreTitular() != null ? mensaje.getNombreTitular() : "");
			adicionales.put("binTarjeta", mensaje.getBinTarjeta() != null ? mensaje.getBinTarjeta().toString() : "");
			datosRespuesta.setTipoLectura(mensaje.getIndicadorTipoLectura());
			adicionales.put("relleno", mensaje.getRelleno());
			datosRespuesta.setContactLess(mensaje.getTipoDispositivoContacless());
			datosRespuesta.setNombreEntidad(String.valueOf(mensaje.getIdentCentroProcesador()));
			datosRespuesta.setTarjeta(mensaje.getPanEnmascarado());
			adicionales.put("tarjetaEMV", mensaje.getTarjetaEMV());
			adicionales.put("modoEntradaDatos", mensaje.getModoEntradaDatos());
			datosRespuesta.setVerificacion(mensaje.getModoVerificacionUsuario());
			datosRespuesta.setARC(mensaje.getARC());
			datosRespuesta.setAID(mensaje.getAID());

			datosRespuesta.setFuc(mensaje.getComercio());
			datosRespuesta.setTerminalId(idTerminal);
			datosRespuesta.setSesionId(mensaje.getAutorizacion());
			datosRespuesta.setDocumento(datosPeticion.getIdTransaccion());
			datosRespuesta.setNumTransaccion(datosPeticion.getIdTransaccion());
			datosRespuesta.setEmpleado(sesion.getSesionUsuario().getUsuario().getDesusuario());
			
			switch (mensaje.getModoVerificacionUsuario()) {
				case "*":
					datosRespuesta.setAuthMode("0");
					break;
				
				case "P":
					datosRespuesta.setAuthMode("1");
					break;

				default:
					datosRespuesta.setAuthMode("2");
					break;
			}

			if (StringUtils.isNotBlank(mensaje.getTransaccion()) && mensaje.getTransaccion().equals(TefSiamManager.VENTA)) {
				adicionales.put("PANSequenceNumbre", mensaje.getPanSequenceNumber());
				adicionales.put("applicationLabel", mensaje.getApplicationLabel());
				datosRespuesta.setApplicationLabel(StringUtils.isNotBlank(mensaje.getApplicationLabel()) ? mensaje.getApplicationLabel() : "");
				datosRespuesta.setCodigoDivisa(mensaje.getCodigoMoneda());
				adicionales.put("indicadorCreditoDebito", mensaje.getIndicadorCreDeb());
				adicionales.put("transparencia", mensaje.getTransparencia());
				datosRespuesta.setDCC(mensaje.isDCC());
				if (datosRespuesta.isDCC()) {
					datosRespuesta.setImporteDivisa(mensaje.getImporteDCC().toString());
					datosRespuesta.setExchangeRate(mensaje.getExchangeRate());
					adicionales.put("markUp", mensaje.getMarkUp());
					datosRespuesta.setComision(mensaje.getCommision());
					adicionales.put("entidad", mensaje.getEntidad());
				}
				adicionales.put("textoTicket", mensaje.getTextoTicket());
				adicionales.put("fechaOriginal", datosRespuesta.getFechaBoleta());
				adicionales.put("importeOriginal", mensaje.getImporte().toString());
				adicionales.put("secuenciaOriginal", mensaje.getSecuencia());

			}
			else if (StringUtils.isNotBlank(mensaje.getTransaccion()) && mensaje.getTransaccion().equals(TefSiamManager.DEVOLUCION)) {
				datosRespuesta.setApplicationLabel(datosRespuestaOrigen!=null ? datosRespuestaOrigen.getApplicationLabel() : "");
			}

			datosRespuesta.setAdicionales(adicionales);
		}
		else {
			log.debug("procesarRespuesta() - procesando error recibido " + msgRespuesta);

			SiamError error = new SiamError();
			error.setearDatosRespuestaError(msgRespuesta);
			datosRespuesta.setCodResult(error.getMensaje().getCodigoRespuesta());
			datosRespuesta.setMsgRespuesta(error.getMensaje().getCodigoError());

			adicionales.put("errorEstado", error.getEstado());
			adicionales.put("errorMensajeCodRespuesta", error.getMensaje().getCodigoRespuesta());
			adicionales.put("errorMensajeCentroAutorizador", error.getMensaje().getCentroAutorizador());
			adicionales.put("errorMensajeCodError", error.getMensaje().getCodigoError());
			datosRespuesta.setAdicionales(adicionales);

			String valor = datosRespuesta != null ? datosRespuesta.getMsgRespuesta() : "N1";
			String msgeError = error.getMapaErrores().get(valor) != null ? error.getMapaErrores().get(valor) : error.getMapaErrores().get("N1");

			throw new TarjetaException(msgeError.toUpperCase(), datosRespuesta);
		}

		return datosRespuesta;
	}

	public DatosRespuestaPagoTarjeta procesarRespuestaCancelacionPago(String msgRespuesta, DatosPeticionPagoTarjeta datosPeticion) throws TarjetaException {
		log.debug("procesarRespuestaCancelacionPago() - procesando respuesta recibida " + msgRespuesta);

		SiamResponseCancelacionPagoParcial response = new SiamResponseCancelacionPagoParcial();
		response.setearDatosRespuestaCancelacionPago(msgRespuesta);
		SiamMensajeResponseCancelacionPagoParcial mensaje = response.getMensaje();

		DatosRespuestaPagoTarjeta datosRespuesta = new DatosRespuestaPagoTarjeta(datosPeticion);
		Map<String, String> adicionales = new HashMap<>();
		adicionales.put("estado", response.getEstado());
		adicionales.put("mensajeOriginal", msgRespuesta);
		datosRespuesta.setCodResult(mensaje.getCodigoRespuesta());
		datosRespuesta.setMsgRespuesta(mensaje.getCodigoRespuesta());

		if (StringUtils.isNotBlank(datosRespuesta.getMsgRespuesta()) && datosRespuesta.getMsgRespuesta().equals(TefSiamManager.OK)) {

			datosRespuesta.setCodigoCentro(mensaje.getCentroAutorizador());
			adicionales.put("secuencia", mensaje.getSecuencia());

			datosRespuesta.setAdicionales(adicionales);
		}
		else {
			SiamError error = new SiamError();
			error.setearDatosRespuestaError(msgRespuesta);
			datosRespuesta.setCodResult(error.getMensaje().getCodigoRespuesta());
			datosRespuesta.setMsgRespuesta(error.getMensaje().getCodigoError());

			adicionales.put("errorEstado", error.getEstado());
			adicionales.put("errorMensajeCodRespuesta", error.getMensaje().getCodigoRespuesta());
			adicionales.put("errorMensajeCentroAutorizador", error.getMensaje().getCentroAutorizador());
			adicionales.put("errorMensajeCodError", error.getMensaje().getCodigoError());
			datosRespuesta.setAdicionales(adicionales);

			String valor = datosRespuesta != null ? datosRespuesta.getMsgRespuesta() : "N1";
			String msgeError = error.getMapaErrores().get(valor) != null ? error.getMapaErrores().get(valor) : error.getMapaErrores().get("N1");

			throw new TarjetaException(msgeError.toUpperCase(), datosRespuesta);
		}

		return datosRespuesta;
	}

	public String crearConfirmacionRecepcionVenta(DatosRespuestaPagoTarjeta datosRespuesta) {
		log.debug("crearPeticionVenta() - enviando peticion a Siam");

		SiamVentaRequest request = new SiamVentaRequest();
		SiamVentaMensajeRequest mr = new SiamVentaMensajeRequest();
		request.setEstado(StringUtils.isNotBlank(datosRespuesta.getAdicional("estado")) ? datosRespuesta.getAdicional("estado") : "5");
		mr.setTransaccion(datosRespuesta.getTipoTransaccion());
		mr.setInterfazEMV(TefSiamManager.INTEFAZ_EMV);
		mr.setCodigoAplazado(Integer.valueOf("00"));
		mr.setImporte(new BigDecimal(datosRespuesta.getImporte()));

		if (StringUtils.isNotBlank(datosRespuesta.getFechaTransaccion())) {
			mr.setFechaHoraString(datosRespuesta.getFechaTransaccion());
		}
		else {
			mr.setFechahora(new Date());
		}

		mr.setCodigoTienda(sesion.getAplicacion().getTienda().getCodAlmacen());
		mr.setCodigoTPV(sesion.getAplicacion().getCodCaja());
		mr.setCodigoTicket(datosRespuesta.getTicket());
		mr.setCodigoCliente(cliente);

		request.setMensaje(mr);
		log.debug("crearPeticionVenta() - enviamos a SIAM esto" + request.toString());

		return request.toString();
	}

	public String crearConfirmacionRecepcionDevolucion(DatosRespuestaPagoTarjeta datosRespuesta) {
		log.debug("crearPeticionDevolucion() - enviando peticion a Siam");

		SiamDevolucionRequest request = new SiamDevolucionRequest();
		SiamDevolucionMensajeRequest mr = new SiamDevolucionMensajeRequest();
		request.setEstado(StringUtils.isNotBlank(datosRespuesta.getAdicional("estado")) ? datosRespuesta.getAdicional("estado") : "5");
		mr.setTransaccion(TefSiamManager.DEVOLUCION);
		mr.setInterfazEMV(TefSiamManager.INTEFAZ_EMV);
		mr.setCentroAutorizador("0");
		mr.setCodigoAutorizacion(StringUtils.isNotBlank(datosRespuesta.getCodAutorizacion()) ? datosRespuesta.getCodAutorizacion() : StringUtils.leftPad("", 6, "0"));
		mr.setImporte(new BigDecimal(datosRespuesta.getImporte()));

		if (StringUtils.isNotBlank(datosRespuesta.getFechaTransaccion())) {
			mr.setFechaHoraString(datosRespuesta.getFechaTransaccion());
		}
		else {
			mr.setFechahora(new Date());
		}

		mr.setCodigoTienda(sesion.getAplicacion().getTienda().getCodAlmacen());
		mr.setCodigoTPV(sesion.getAplicacion().getCodCaja());
		mr.setCodigoTicket(datosRespuesta.getTicket());
//		mr.setCodigoCliente(cliente);
		mr.setFechaOriginalAsString(datosRespuesta.getAdicional("fechaOriginal"));
		mr.setImporteOriginal(new BigDecimal(datosRespuesta.getAdicional("importeOriginal")));
		mr.setSecuenciaOriginal(datosRespuesta.getAdicional("secuenciaOriginal"));
		
		log.debug("crearPeticionDevolucion() - enviamos a SIAM esto" + request.toString());
		return request.toString();
	}

	// usamos este metodo para enviar una cancelacion del pago debido a un error en el procesamiento de la respuesta por
	// nuestra parte
	public String crearCancelacionErrorProcesamiento(String peticion) {
		log.debug("crearCancelacionErrorProcesamiento() - enviando peticion a Siam de cancelacion a siam");

		peticion.replaceFirst("3", "7");

		log.debug("crearCancelacionErrorProcesamiento() - enviamos a SIAM esto" + peticion);

		return peticion;
	}

	public String crearConfirmacionRecepcionAnulacionPago(DatosRespuestaPagoTarjeta datosRespuesta) {
		log.debug("crearConfirmacionRecepcionAnulacionPago() - enviando peticion a Siam");

		SiamCancelarPagoRequest request = new SiamCancelarPagoRequest();
		SiamCancelarPagoMensajeRequest mr = new SiamCancelarPagoMensajeRequest();

		request.setEstado(StringUtils.isNotBlank(datosRespuesta.getAdicional("estado")) ? datosRespuesta.getAdicional("estado") : "5");
		mr.setTransaccion(TefSiamManager.CANCEL_PAY);
		mr.setCentroAutorizador("0");
		mr.setSecuencia(datosRespuesta.getAdicional("secuencia"));

		request.setMensaje(mr);

		log.debug("crearPeticionDevolucion() - enviamos a SIAM esto" + request.toString());

		return request.toString();
	}

	public String formateoHora(Date fechaHora) {

		String hour = String.valueOf(fechaHora.getHours());
		if (hour.length() == 1) {
			hour = "0" + hour;
		}
		String min = String.valueOf(fechaHora.getMinutes());
		if (min.length() == 1) {
			min = "0" + min;
		}

		return hour + min;
	}

	public String formateoDia(Date fecha) {

		String year = String.valueOf(1900 + fecha.getYear());
		String month = String.valueOf(fecha.getMonth() + 1);
		if (month.length() == 1) {
			month = "0" + month;
		}
		String day = String.valueOf(fecha.getDate());
		if (day.length() == 1) {
			day = "0" + day;
		}

		return day + "/" + month + "/" + year;
	}

	public String convertirNumeroAHora(int hora) {
		// Convertir el número a formato de hora (HHmmss)
		String numeroComoString = String.format("%06d", hora);

		// Parsear el número como una fecha
		SimpleDateFormat formatoEntrada = new SimpleDateFormat("HHmmss");
		SimpleDateFormat formatoSalida = new SimpleDateFormat("HH:mm:ss");

		try {
			Date fecha = formatoEntrada.parse(numeroComoString);
			return formatoSalida.format(fecha);
		}
		catch (Exception e) {
			log.error("convertirNumeroAHora() - error parseando el dato de hora");
			return null;
		}
	}
}
