package com.comerzzia.dinosol.pos.services.payments.methods.types.descuentosespeciales;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.librerias.descuentosespeciales.client.DescuentosEspecialesService;
import com.comerzzia.dinosol.librerias.descuentosespeciales.client.dto.AnularDescuentoRequestDTO;
import com.comerzzia.dinosol.librerias.descuentosespeciales.client.dto.AnularDescuentoResponseDTO;
import com.comerzzia.dinosol.librerias.descuentosespeciales.client.dto.AplicarDescuentoRequestDTO;
import com.comerzzia.dinosol.librerias.descuentosespeciales.client.dto.AplicarDescuentoResponseDTO;
import com.comerzzia.dinosol.librerias.descuentosespeciales.client.dto.LineaVentaDTO;
import com.comerzzia.dinosol.librerias.descuentosespeciales.client.exception.DescuentosEspecialesConexionException;
import com.comerzzia.dinosol.librerias.descuentosespeciales.client.exception.DescuentosEspecialesConfiguracionException;
import com.comerzzia.dinosol.librerias.descuentosespeciales.client.exception.DescuentosEspecialesResponseException;
import com.comerzzia.dinosol.librerias.descuentosespeciales.client.exception.DescuentosEspecialesSocketException;
import com.comerzzia.dinosol.librerias.descuentosespeciales.client.utils.ConexionDescuentosEspecialesBean;
import com.comerzzia.dinosol.librerias.descuentosespeciales.keys.DescuentoKeys;
import com.comerzzia.pos.services.payments.PaymentDto;
import com.comerzzia.pos.services.payments.PaymentException;
import com.comerzzia.pos.services.payments.configuration.ConfigurationPropertyDto;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.events.PaymentInitEvent;
import com.comerzzia.pos.services.payments.events.PaymentOkEvent;
import com.comerzzia.pos.services.payments.methods.types.BasicPaymentMethodManager;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.util.i18n.I18N;

@Component
@Scope("prototype")
public class DescuentosEspecialesManager extends BasicPaymentMethodManager {

	public static final String PARAM_NUMERO_TARJETA_DESCUENTOS = "numeroTarjeta";
	public static final String PARAM_DESCUENTO_DESCUENTOS = "descuento";
	public static final String PARAM_CODIGO_VENTA_DESCUENTOS = "codigoVenta";
	public static final String PARAM_PUNTO_VENTA_DESCUENTOS = "puntoVenta";
	public static final String PARAM_NUMERO_OPERACION_DESCUENTOS = "numeroOperacion";
	public static final String PARAM_FECHA_DESCUENTOS = "fecha";

	private Logger log = Logger.getLogger(DescuentosEspecialesManager.class);

	/* Variables de configuración */
	private String servidorTCP;
	private String puertoTCP;
	private String timeout;
	private String empresa;
	private ConexionDescuentosEspecialesBean conexion;

	/* Servicio para aplicar los descuentos especiales */
	protected DescuentosEspecialesService service;

	public DescuentosEspecialesService getService() {
		return service;
	}

	public DescuentosEspecialesManager() {
		service = new DescuentosEspecialesService();
	}

	@Override
	public List<ConfigurationPropertyDto> getConfigurationProperties() {
		List<ConfigurationPropertyDto> properties = new ArrayList<ConfigurationPropertyDto>();
		return properties;
	}

	@Override
	public void setConfiguration(PaymentMethodConfiguration configuration) {
		super.setConfiguration(configuration);
		servidorTCP = configuration.getConfigurationProperty("servidor_TCP");
		puertoTCP = configuration.getConfigurationProperty("puerto_TCP");
		timeout = configuration.getConfigurationProperty("timeout");
		empresa = configuration.getConfigurationProperty("empresa");
		
		if(StringUtils.isNotBlank(servidorTCP) && StringUtils.isNotBlank(puertoTCP) && StringUtils.isNotBlank(timeout)) {
			conexion = new ConexionDescuentosEspecialesBean(servidorTCP, puertoTCP, timeout);
			service.setDatosConexion(conexion);
		}
	}

	/**
	 * Realiza la comprobación de que la configuración de la pasarela está rellena.
	 * 
	 * @throws DescuentosEspecialesConfiguracionException
	 */
	public void comprobarConfiguracion() throws PaymentException {
		if (conexion != null) {
			if (!StringUtils.isNotBlank(conexion.getPuertoTCP()) || !StringUtils.isNotBlank(conexion.getServidorTCP()) 
					|| !StringUtils.isNotBlank(conexion.getTimeout())) {
				String mensajeError = "La pasarela de Descuentos Especiales no está configurada correctamente.";
				log.error("comprobarConfiguracion() - " + mensajeError);
				throw new PaymentException(I18N.getTexto(mensajeError));
			}
		}
		else {
			String mensajeError = "La pasarela de Descuentos Especiales no está configurada correctamente.";
			log.error("comprobarConfiguracion() - " + mensajeError);
			throw new PaymentException(I18N.getTexto(mensajeError));
		}
	}

	@Override
	public boolean pay(BigDecimal amount) throws PaymentException {
		/* Iniciamos el evento con la clase */
		PaymentInitEvent initEvent = new PaymentInitEvent(this);
		getEventHandler().paymentInitProcess(initEvent);

		/* Rescatamos los datos enviados desde PagosController */
		String numeroTarjeta = (String) parameters.get(PARAM_NUMERO_TARJETA_DESCUENTOS);
		String codigoVenta = (String) parameters.get(PARAM_CODIGO_VENTA_DESCUENTOS);
		String puntoVenta = (String) parameters.get(PARAM_PUNTO_VENTA_DESCUENTOS);
		String numOperacion = (String) parameters.get(PARAM_NUMERO_OPERACION_DESCUENTOS);
		Date fecha = (Date) parameters.get(PARAM_FECHA_DESCUENTOS);

		/*
		 * Generamos el evento de OK y insertamos los datos necesarios para una anulación en el extendedData
		 */
		PaymentOkEvent event = new PaymentOkEvent(this, paymentId, amount);
		event.addExtendedData(PARAM_NUMERO_TARJETA_DESCUENTOS, numeroTarjeta);
		event.addExtendedData(PARAM_CODIGO_VENTA_DESCUENTOS, codigoVenta);
		event.addExtendedData(PARAM_PUNTO_VENTA_DESCUENTOS, puntoVenta);
		event.addExtendedData(PARAM_NUMERO_OPERACION_DESCUENTOS, numOperacion);
		event.addExtendedData(PARAM_FECHA_DESCUENTOS, fecha);
		getEventHandler().paymentOk(event);

		return true;
	}

	@Override
	public boolean cancelPay(PaymentDto payment) throws PaymentException {
		log.debug("cancelPay() - Iniciamos la anulación de un descuento especial.");

		/*
		 * Realizamos la comprobación de configuración en esta parte también porque para entrar aquí no pasa por el
		 * cargar saldo.
		 */
		comprobarConfiguracion();

		AnularDescuentoRequestDTO anularDescuento = new AnularDescuentoRequestDTO();
		anularDescuento.setCodigoEmpresa(empresa);
		anularDescuento.setCodigoTienda(ticket.getTienda().getCodAlmacen());
		anularDescuento.setCodigoPOS(StringUtils.leftPad(ticket.getCabecera().getCodCaja(), 4, "0"));
		anularDescuento.setCodigoOperador(ticket.getCabecera().getCajero().getIdUsuario().toString());
		anularDescuento.setNumeroTarjeta((String) payment.getExtendedData().get(PARAM_NUMERO_TARJETA_DESCUENTOS));

		/* Rellenamos los datos de la petición origen */
		anularDescuento.setCodigoTiendaOrigen((String) payment.getExtendedData().get(PARAM_CODIGO_VENTA_DESCUENTOS));
		anularDescuento.setCodigoPOSOrigen((String) payment.getExtendedData().get(PARAM_PUNTO_VENTA_DESCUENTOS));
		anularDescuento.setNumeroOperacionOrigen((String) payment.getExtendedData().get(PARAM_NUMERO_OPERACION_DESCUENTOS));
		anularDescuento.setFechaOrigen((Date) payment.getExtendedData().get(PARAM_FECHA_DESCUENTOS));

		try {
			AnularDescuentoResponseDTO respuestaDTO = service.anularDescuento(anularDescuento);

			log.debug("cancelPay() - El resultado de anular el descuento es : " + respuestaDTO.getTextoRespuesta());
			PaymentOkEvent event = new PaymentOkEvent(this, payment.getPaymentId(), payment.getAmount());
			event.setCanceled(true);
			getEventHandler().paymentOk(event);
			return true;
		}
		catch (Exception e) {
			throw new PaymentException(I18N.getTexto(e.getMessage()), e, paymentId, this);
		}
	}

	@Override
	public boolean cancelReturn(PaymentDto arg0) throws PaymentException {
		return false;
	}

	@Override
	public boolean returnAmount(BigDecimal arg0) throws PaymentException {
		return false;
	}

	/**
	 * Pide el descuento que va tener para los articulos comprados.
	 * 
	 * @param numeroTarjeta
	 *            : Número de tarjeta para aplicar el descuento.
	 * @return mapaDatos
	 * @throws DescuentosEspecialesConexionException
	 * @throws DescuentosEspecialesResponseException
	 * @throws DescuentosEspecialesSocketException
	 * @throws DescuentosEspecialesConfiguracionException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getDescuento(String numeroTarjeta) throws PaymentException, DescuentosEspecialesConexionException, DescuentosEspecialesResponseException, DescuentosEspecialesSocketException,
	        DescuentosEspecialesConfiguracionException {
		log.debug("getDescuento() - Iniciamos la aplicación de un descuento especial.");

		/*
		 * Realizamos la comprobación de que la configuración esta correcta. La realizamos en esta parte porque es el
		 * método que se lanza siempre antes de los otros métodos como el de pago.
		 */
		comprobarConfiguracion();

		getEventHandler().paymentInitProcess(new PaymentInitEvent(this));

		AplicarDescuentoRequestDTO aplicarDescuento = new AplicarDescuentoRequestDTO();
		aplicarDescuento.setCodigoEmpresa(empresa);
		aplicarDescuento.setCodigoTienda(ticket.getTienda().getCodAlmacen());
		aplicarDescuento.setCodigoPOS(StringUtils.leftPad(ticket.getCabecera().getCodCaja(), 4, "0"));
		aplicarDescuento.setCodigoOperador(ticket.getCabecera().getCajero().getIdUsuario().toString());
		aplicarDescuento.setNumeroTarjeta(numeroTarjeta);
		aplicarDescuento.setCodigoDocumentoCliente(ticket.getCabecera().getCodTicket());
		aplicarDescuento.setImporte(ticket.getCabecera().getTotales().getTotalAPagar());		

		List<LineaVentaDTO> ventas = new ArrayList<LineaVentaDTO>();
		for (LineaTicket linea : (List<LineaTicket>) ticket.getLineas()) {
			if (linea.getCantidad().compareTo(BigDecimal.ZERO) != 0) {
				LineaVentaDTO lineaVenta = new LineaVentaDTO();
				lineaVenta.setCodigoArticulo(DescuentoKeys.ARTICULO_GENERICO_DINOSOL);
				lineaVenta.setUnidades(linea.getCantidad());
				lineaVenta.setImporte(linea.getImporteTotalConDto());
				lineaVenta.setPrecioUnitario(linea.getPrecioTotalConDto());
	
				ventas.add(lineaVenta);
			}
		}
		
		aplicarDescuento.setListaArticulosOperacion(ventas);

		try {
			AplicarDescuentoResponseDTO respuestaDTO = service.aplicarDescuento(aplicarDescuento);

			BigDecimal descuento = respuestaDTO.getTotalDescuento();

			log.debug("getDescuento() - Terminada la aplicación de un descuento especial.");
			/* Creamos el mapa con los datos que debemos devolver */
			Map<String, Object> mapaDatos = new HashMap<String, Object>();
			mapaDatos.put(PARAM_DESCUENTO_DESCUENTOS, descuento);
			mapaDatos.put(PARAM_CODIGO_VENTA_DESCUENTOS, aplicarDescuento.getCodigoTienda());
			mapaDatos.put(PARAM_PUNTO_VENTA_DESCUENTOS, aplicarDescuento.getCodigoPOS());
			mapaDatos.put(PARAM_NUMERO_OPERACION_DESCUENTOS, aplicarDescuento.getNumeroOperacion());
			mapaDatos.put(PARAM_FECHA_DESCUENTOS, aplicarDescuento.getFechaHoraOperacion());

			return mapaDatos;
		}
		catch (Exception e) {
			log.error("getDescuento() - " + e.getMessage());
			throw new DescuentosEspecialesConexionException(I18N.getTexto(e.getMessage()));
		}
	}

	
	@Override
	public boolean isUniquePayment() {
		return true;
	}

}
