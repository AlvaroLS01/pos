package com.comerzzia.dinosol.pos.services.payments.methods.types.gtt;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.comerzzia.core.model.notificaciones.Notificacion;
import com.comerzzia.dinosol.librerias.gtt.client.GttException;
import com.comerzzia.dinosol.librerias.gtt.client.GttIOException;
import com.comerzzia.dinosol.librerias.gtt.client.GttServerException;
import com.comerzzia.dinosol.librerias.gtt.client.GttService;
import com.comerzzia.dinosol.librerias.gtt.client.dto.EstadoRequestDTO;
import com.comerzzia.dinosol.librerias.gtt.client.dto.EstadoResponseDTO;
import com.comerzzia.dinosol.librerias.gtt.client.dto.PagoTalonRequestDTO;
import com.comerzzia.dinosol.librerias.gtt.client.dto.PagoTalonResponseDTO;
import com.comerzzia.dinosol.librerias.gtt.client.dto.RestriccionDTO;
import com.comerzzia.dinosol.librerias.gtt.client.dto.RestriccionesResponseDTO;
import com.comerzzia.dinosol.librerias.gtt.client.dto.SaldoEstadoResponseDTO;
import com.comerzzia.dinosol.librerias.gtt.client.dto.SaldoRequestDTO;
import com.comerzzia.dinosol.librerias.gtt.client.dto.SaldoResponseDTO;
import com.comerzzia.dinosol.librerias.gtt.client.dto.SumaSaldoRequestDTO;
import com.comerzzia.dinosol.librerias.gtt.client.dto.SumaSaldoResponseDTO;
import com.comerzzia.dinosol.pos.services.payments.methods.types.gtt.excepciones.GttConfiguracionException;
import com.comerzzia.dinosol.pos.services.payments.methods.types.gtt.excepciones.GttRestriccionesException;
import com.comerzzia.dinosol.pos.util.xml.ObjectParseUtil;
import com.comerzzia.pos.services.notificaciones.Notificaciones;
import com.comerzzia.pos.services.payments.PaymentDto;
import com.comerzzia.pos.services.payments.PaymentException;
import com.comerzzia.pos.services.payments.configuration.ConfigurationPropertyDto;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.events.PaymentInitEvent;
import com.comerzzia.pos.services.payments.events.PaymentOkEvent;
import com.comerzzia.pos.services.payments.methods.types.BasicPaymentMethodManager;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

public abstract class GttManager extends BasicPaymentMethodManager {

	protected static final Logger log = Logger.getLogger(GttManager.class);

	public static final String PARAM_NUMERO_TARJETA_GTT = "numeroTarjeta";
	public static final String PARAM_RESPUESTA_GTT = "respuestaGtt";

	private String url;
	protected GttService service;

	protected RestriccionesResponseDTO categoriasRestringidas;

	class ArticuloRestringido {
		public String codart;
		public String desart;
		public String codcat;
		public BigDecimal cantidad;
	}

	@Override
	public List<ConfigurationPropertyDto> getConfigurationProperties() {
		List<ConfigurationPropertyDto> properties = new ArrayList<ConfigurationPropertyDto>();
		return properties;
	}

	@Override
	public void setConfiguration(PaymentMethodConfiguration configuration) {
		super.setConfiguration(configuration);
		url = configuration.getConfigurationProperty("url");
	}

	public void comprobarConfiguracion() throws GttConfiguracionException {
		if (!StringUtils.isNotBlank(url)) {
			String mensajeError = "La pasarela de GTT no está configurada correctamente.";
			log.error("comprobarConfiguracion() - " + mensajeError);
			throw new GttConfiguracionException(I18N.getTexto(mensajeError));
		}
	}

	@Override
	public void initialize() {
		super.initialize();
		service = new GttService();
		service.setServiceUrl(url);
		/* Cargamos los datos de las restricciones */
		try {
			categoriasRestringidas = GttCargaXmlRestricciones.cargarMapaRestricciones();
		} catch (GttRestriccionesException e) {
			log.error("initialize()" + e.getMessage());
			/* En caso de fallar la carga de restricciones creamos una notificación */
			Notificaciones.get().addNotification(
					new Notificacion(I18N.getTexto(e.getMessage()), Notificacion.Tipo.ERROR, new Date()));
		}
	}

	@Override
	public boolean pay(BigDecimal amount) throws PaymentException {
		log.debug("pay() - Iniciamos la petición para pagar " + amount + " de salgo");
		/* Iniciamos el evento con la clase */
		PaymentInitEvent initEvent = new PaymentInitEvent(this);
		getEventHandler().paymentInitProcess(initEvent);
		/* Rescatamos los datos enviados desde PagosController */
		String codigo = (String) parameters.get(PARAM_NUMERO_TARJETA_GTT);

		PagoTalonRequestDTO peticion = new PagoTalonRequestDTO();
		peticion.setCodigoCentro(ticket.getTienda().getCodAlmacen());
		String codeOperador = ticket.getCabecera().getCajero().getUsuario().toString();
		peticion.setCodigoOperario(StringUtils.leftPad(codeOperador, 9, '0'));
		peticion.setCodigoTalon(codigo);
		peticion.setCodigoTerminal(StringUtils.leftPad(ticket.getCabecera().getCodCaja(), 4, '0'));

		Long idTicketCortado = ticket.getIdTicket() % 10000;
		String codigoTransaccion = StringUtils.leftPad(idTicketCortado.toString(), 4, '0');
		peticion.setCodigoTransaccion(codigoTransaccion);
		log.debug("pay() - El código de transacción es " + codigoTransaccion);

		peticion.setImporteTalon(amount);
		peticion.setImporteTicket(ticket.getTotales().getTotal());
		// Recibimos los códigos de restricción que coinciden con las familias
		peticion.setRestricciones(obtenerRestricciones());
		peticion.setNumeroTalonesBPUsados(1);

		try {
			PagoTalonResponseDTO respuesta = service.pagarTalon(peticion);
			Map<String, Object> respuestaDto = ObjectParseUtil.introspect(respuesta);

			log.debug("pay() - Resultado de pago con talón : " + respuesta.getDescripcionRespuesta());
			PaymentOkEvent event = new PaymentOkEvent(this, paymentId, amount);
			event.addExtendedData(PARAM_NUMERO_TARJETA_GTT, codigo);
			for (String key : respuestaDto.keySet()) {
				if (!key.equals("class")) {
					event.addExtendedData(key, respuestaDto.get(key));
				}
			}
			setPagoEliminable(event);
			getEventHandler().paymentOk(event);

			log.debug("pay() - Finalizada la petición para pago con talón");
			return true;
		} catch (GttException e) {
			log.error("pay() - " + e.getMessage());

			String descripcionRestriccion = "";
			if (StringUtils.isNotBlank(e.getCodRespuesta())) {
				if (categoriasRestringidas != null) {
					for (RestriccionDTO restriccion : categoriasRestringidas.getRestricciones()) {
						if (restriccion.getCodigoResriccion().equals(e.getCodRespuesta())) {
							descripcionRestriccion = restriccion.getDescripcionRestriccion();
							break;
						}
					}
				}
			}

			throw new PaymentException(I18N.getTexto(e.getMessage()) + ": " + descripcionRestriccion, e, paymentId,
					this);
		} catch (GttIOException e) {
			throw new PaymentException(
					I18N.getTexto("No se ha podido establecer la comunicación con el servidor de GTT."), e);
		} catch (GttServerException e) {
			throw new PaymentException(
					I18N.getTexto("El servidor de GTT ha devuelto un error al realizar la petición."), e);
		}
	}

	protected void setPagoEliminable(PaymentOkEvent event) {
		event.setRemovable(true);
	}

	@Override
	public boolean returnAmount(BigDecimal amount) throws PaymentException {
		throw new PaymentException(I18N.getTexto("No se puede devolver en este medio de pago."));
	}

	@Override
	public boolean cancelPay(PaymentDto payment) throws PaymentException {
		PaymentOkEvent event = new PaymentOkEvent(this, payment.getPaymentId(), payment.getAmount());
		event.setCanceled(true);
		getEventHandler().paymentOk(event);
		return true;
	}

	@Override
	public boolean cancelReturn(PaymentDto payment) throws PaymentException {
		return false;
	}

	/**
	 * Consulta el saldo de un medio de pago GTT.
	 * 
	 * @param codigo
	 * @return respuesta
	 * @throws GttServerException
	 * @throws GttIOException
	 * @throws GttException
	 * @throws GttNotFoundException
	 * @throws GttConexionException
	 * @throws GttConfiguracionException
	 */
	public SaldoResponseDTO getSaldo(String codigo)
			throws GttException, GttIOException, GttServerException, GttConfiguracionException {
		log.debug("getSaldo() - Iniciamos la petición de consulta del saldo");

		/*
		 * Realizamos la comprobación de que la configuración de pantalla es correcta.
		 */
		try {
			comprobarConfiguracion();
		} catch (GttConfiguracionException e) {
			log.error("getSaldo() - " + e.getMessage());
			throw new GttConfiguracionException(I18N.getTexto(e.getMessage()));
		}

		SaldoRequestDTO peticion = new SaldoRequestDTO();
		peticion.setCodigoCentro(ticket.getTienda().getCodAlmacen());
		String codeOperador = ticket.getCabecera().getCajero().getUsuario();
		peticion.setCodigoOperario(StringUtils.leftPad(codeOperador, 9, '0'));
		peticion.setCodigoTalon(codigo);
		peticion.setCodigoTerminal(StringUtils.leftPad(ticket.getCabecera().getCodCaja(), 4, '0'));
		Integer x = new Random().nextInt(9999);
		peticion.setCodigoTransaccion(StringUtils.leftPad(x.toString(), 4, '0'));

		SaldoResponseDTO respuesta = service.getSaldo(peticion);
		log.debug("getSaldo() - Finalizada la petición de consulta del saldo");
		log.debug("getSaldo() - El saldo de la tarjeta es : " + respuesta.getSaldo().toString());
		return respuesta;
	}

	/**
	 * Consulta el estado de una tarjeta GTT.
	 */
	public EstadoResponseDTO getEstado(String codigo)
			throws GttException, GttIOException, GttServerException, GttConfiguracionException {
		log.debug("getEstado() - Iniciamos la petición de consulta de estado para la tarjeta: " + codigo);

		comprobarConfiguracion();

		EstadoRequestDTO peticion = new EstadoRequestDTO();
		peticion.setCodigoCentro(ticket.getTienda().getCodAlmacen());
		String codeOperador = ticket.getCabecera().getCajero().getUsuario();
		peticion.setCodigoOperario(StringUtils.leftPad(codeOperador, 9, '0'));
		peticion.setCodigoTalon(codigo);
		peticion.setCodigoTerminal(StringUtils.leftPad(ticket.getCabecera().getCodCaja(), 4, '0'));
		Integer x = new Random().nextInt(9999);
		peticion.setCodigoTransaccion(StringUtils.leftPad(x.toString(), 4, '0'));

		EstadoResponseDTO respuesta = service.getEstado(peticion);

		log.debug("getEstado() - Finalizada la petición de consulta de estado");
		log.debug("getEstado() - La tarjeta tiene restricción : " + respuesta.getRestriccion());
		return respuesta;
	}

	/**
	 * Consulta el saldo y estado de una tarjeta GTT.
	 */
	public SaldoEstadoResponseDTO getSaldoAndEstado(String codigo)
			throws GttException, GttIOException, GttServerException, GttConfiguracionException {
		log.debug("getSaldoAndEstado() - Iniciamos la petición de consulta de estado y saldo para la tarjeta: " + codigo);

		comprobarConfiguracion();

		SaldoRequestDTO peticion = new SaldoRequestDTO();
		peticion.setCodigoCentro(ticket.getTienda().getCodAlmacen());
		String codeOperador = ticket.getCabecera().getCajero().getUsuario();
		peticion.setCodigoOperario(StringUtils.leftPad(codeOperador, 9, '0'));
		peticion.setCodigoTalon(codigo);
		peticion.setCodigoTerminal(StringUtils.leftPad(ticket.getCabecera().getCodCaja(), 4, '0'));
		Integer x = new Random().nextInt(9999);
		peticion.setCodigoTransaccion(StringUtils.leftPad(x.toString(), 4, '0'));

		SaldoEstadoResponseDTO respuesta = service.getSaldoAndEstado(peticion);

		log.debug("getSaldoAndEstado() - Finalizada la petición de consulta de estado");
		log.debug("getSaldoAndEstado() - El saldo de la tarjeta es : " + respuesta.getSaldo().toString());
		log.debug("getSaldoAndEstado() - La tarjeta tiene restricción : " + respuesta.getRestriccion());
		return respuesta;
	}

	/**
	 * Compara los códigos de categorización de los articulos con los códigos de
	 * restricción existentes en un archivo XML.
	 * 
	 * @param codigosCategorias : Listado de códigos de categorización de los
	 *                          artículos
	 * @return noRestriccion
	 */
	@SuppressWarnings("unchecked")
	private Set<String> obtenerRestricciones() {
		Set<String> restricciones = new HashSet<>();

		// Como es una forma de pago provisional, quitamos la comprobación de las restricciones para 
		// este medio de pago ya que se compruebas las restriccciones de otra forma 
		if(!"0405".equals(paymentCode)) {
			// no hacer nada si no hay categorias restringidas
			if (categoriasRestringidas == null) {
				return restricciones;
			}
	
			// Sumar unidades por artículo para obtener los artículos que realmente se
			// compran
			HashMap<String, ArticuloRestringido> articulos = new HashMap<>();
	
			// Sumando unidades de las lineas por articulo
			for (LineaTicket linea : (List<LineaTicket>) ticket.getLineas()) {
				if (linea.getArticulo().getCodCategorizacion() != null) {
					ArticuloRestringido articulo;
	
					if (articulos.containsKey(linea.getArticulo().getCodArticulo())) {
						// sumar unidades
						articulo = articulos.get(linea.getArticulo().getCodArticulo());
						articulo.cantidad = articulo.cantidad.add(linea.getCantidad());
					} else {
						articulo = new ArticuloRestringido();
						articulo.codart = linea.getCodArticulo();
						articulo.desart = linea.getDesArticulo();
						articulo.codcat = linea.getArticulo().getCodCategorizacion();
						articulo.cantidad = linea.getCantidad();
					}
					articulos.put(linea.getArticulo().getCodArticulo(), articulo);
				}
			}
	
			// tomando categorias de los articulos en los que el cliente se lleve unidades
			Set<String> codigosCategorias = new HashSet<>();
	
			for (Map.Entry<String, ArticuloRestringido> articulo : articulos.entrySet()) {
				if (articulo.getValue().cantidad.compareTo(BigDecimal.ZERO) > 0) {
					codigosCategorias.add(articulo.getValue().codcat);
				}
			}
	
			// buscar restricciones de las categorias
			if (!codigosCategorias.isEmpty()) {
				log.debug("Evaluando restricciones de categorias: " + codigosCategorias);
				// Comparamos los código de categoría de los articulos con las restricciones que
				// hemos leido del XML.
				// La comparacion es "empieza por"
				for (String codCategia : codigosCategorias) {
					for (RestriccionDTO restriccion : categoriasRestringidas.getRestricciones()) {
						for (String familia : restriccion.getFamilias()) {
							if (codCategia.startsWith(familia.trim())) {
								/* Introducimos en la lista el código de la restricción */
								restricciones.add(restriccion.getCodigoResriccion());
							}
						}
					}
				}
				log.debug("Restricciones de detectadas: " + restricciones);
			}
		}
		else {
			log.debug("obtenerRestricciones() - Para el medio de pago 0405 no se añaden las restricciones en la petición.");
		}

		return restricciones;
	}

	@SuppressWarnings("unchecked")
	public void checkRestricciones(String codigoRestriccionTarjeta) throws PaymentException {
		if (codigoRestriccionTarjeta == null || 
		    (codigoRestriccionTarjeta != null && (codigoRestriccionTarjeta.equals("XXXX") || codigoRestriccionTarjeta.equals("0000"))))
			return;

		Set<String> restriccionesTicket = obtenerRestricciones();

		if (!restriccionesTicket.contains(codigoRestriccionTarjeta))
			return;

		// buscar artículos restringidos
		HashMap<String, ArticuloRestringido> articulos = new HashMap<>();

		// Sumando unidades de las lineas por articulo
		for (LineaTicket linea : (List<LineaTicket>) ticket.getLineas()) {
			if (linea.getArticulo().getCodCategorizacion() != null) {
				ArticuloRestringido articulo;

				if (articulos.containsKey(linea.getArticulo().getCodArticulo())) {
					// sumar unidades
					articulo = articulos.get(linea.getArticulo().getCodArticulo());
					articulo.cantidad = articulo.cantidad.add(linea.getCantidad());
				} else {
					articulo = new ArticuloRestringido();
					articulo.codart = linea.getCodArticulo();
					articulo.desart = linea.getDesArticulo();
					articulo.codcat = linea.getArticulo().getCodCategorizacion();
					articulo.cantidad = linea.getCantidad();
				}
				articulos.put(linea.getArticulo().getCodArticulo(), articulo);
			}
		}

		// Buscar restriccion detectada
		List<RestriccionDTO> restricciones = categoriasRestringidas.getRestricciones();
		RestriccionDTO restriccionDetectada = null;

		for (RestriccionDTO restriccion : restricciones) {
			if (restriccion.getCodigoResriccion().equals(codigoRestriccionTarjeta)) {
				restriccionDetectada = restriccion;
				break;
			}
		}

		// no debería de pasar
		if (restriccionDetectada == null)
			return;

		// obtener articulos restringidos
		List<ArticuloRestringido> articulosRestringidos = new ArrayList<ArticuloRestringido>();

		for (Map.Entry<String, ArticuloRestringido> articulo : articulos.entrySet()) {
			if (articulo.getValue().cantidad.compareTo(BigDecimal.ZERO) > 0) {
				String codcat = articulo.getValue().codcat;

				if (codcat != null) {
					for (String familia : restriccionDetectada.getFamilias()) {
						if (codcat.startsWith(familia.trim())) {
							articulosRestringidos.add(articulo.getValue());
						}
					}
				}
			}
		}
		
		// no debería de pasar
		if (articulosRestringidos.size() == 0) return;
		
		String mensajeError = "Artículos no permitidos con este medio de pago:\n";
		int stop = 0;
		
		for (ArticuloRestringido articulo : articulosRestringidos) {
			stop++;
			mensajeError += "\n" + articulo.codart + "/" + articulo.desart;
			
			if (stop == 5) {
				mensajeError += "\n...";
				break;
			}
		}
		
		throw new PaymentException(mensajeError);

	}
	
	@SuppressWarnings("unchecked")
	public BigDecimal getImporteLineasNoRestringidas(String codigoRestriccionTarjeta) {
		if (codigoRestriccionTarjeta == null || (codigoRestriccionTarjeta != null && (codigoRestriccionTarjeta.equals("XXXX") || codigoRestriccionTarjeta.equals("0000")))) {
			return ticket.getCabecera().getTotales().getTotalAPagar();
		}

		// buscar artículos restringidos
		HashMap<String, ArticuloRestringido> articulos = new HashMap<>();

		// Sumando unidades de las lineas por articulo
		for (LineaTicket linea : (List<LineaTicket>) ticket.getLineas()) {
			if (linea.getArticulo().getCodCategorizacion() != null) {
				ArticuloRestringido articulo;

				if (articulos.containsKey(linea.getArticulo().getCodArticulo())) {
					// sumar unidades
					articulo = articulos.get(linea.getArticulo().getCodArticulo());
					articulo.cantidad = articulo.cantidad.add(linea.getCantidad());
				}
				else {
					articulo = new ArticuloRestringido();
					articulo.codart = linea.getCodArticulo();
					articulo.desart = linea.getDesArticulo();
					articulo.codcat = linea.getArticulo().getCodCategorizacion();
					articulo.cantidad = linea.getCantidad();
				}
				articulos.put(linea.getArticulo().getCodArticulo(), articulo);
			}
		}

		// Buscar restriccion detectada
		List<RestriccionDTO> restricciones = categoriasRestringidas.getRestricciones();
		RestriccionDTO restriccionDetectada = null;

		for (RestriccionDTO restriccion : restricciones) {
			if (restriccion.getCodigoResriccion().equals(codigoRestriccionTarjeta)) {
				restriccionDetectada = restriccion;
				break;
			}
		}

		// no debería de pasar
		if (restriccionDetectada == null) {
			return BigDecimal.ZERO;
		}

		// obtener articulos restringidos
		List<String> articulosRestringidos = new ArrayList<String>();

		for (Map.Entry<String, ArticuloRestringido> articulo : articulos.entrySet()) {
			if (articulo.getValue().cantidad.compareTo(BigDecimal.ZERO) > 0) {
				String codcat = articulo.getValue().codcat;

				if (codcat != null) {
					for (String familia : restriccionDetectada.getFamilias()) {
						if (codcat.startsWith(familia.trim())) {
							articulosRestringidos.add(articulo.getValue().codart);
						}
					}
				}
			}
		}

		BigDecimal total = BigDecimal.ZERO;
		for(LineaTicket linea : (List<LineaTicket>) ticket.getLineas()) {
			if(!articulosRestringidos.contains(linea.getCodArticulo())) {
				log.debug("getImporteLineasNoRestringidas() - Artículo no restringido, se sumará el importe de la línea: " + linea.getCodArticulo() + " (" + FormatUtil.getInstance().formateaImporteMoneda(linea.getImporteTotalConDto()) + ")");
				total = total.add(linea.getImporteTotalConDto());
			}
			else {
				log.debug("getImporteLineasNoRestringidas() - Artículo restringido: " + linea.getCodArticulo() + " (" + FormatUtil.getInstance().formateaImporteMoneda(linea.getImporteTotalConDto()) + ")");
			}
		}
		
		log.debug("getImporteLineasNoRestringidas() - Importe de líneas sin restricción: " + FormatUtil.getInstance().formateaImporteMoneda(total));
		
		return total;
	}
	
	public SumaSaldoResponseDTO sumarSaldo(String numeroTarjeta, BigDecimal importe) throws GttException, GttIOException, GttServerException {
		SumaSaldoRequestDTO peticion = new SumaSaldoRequestDTO();
		peticion.setCodigoCentro(ticket.getTienda().getCodAlmacen());
		String codeOperador = ticket.getCabecera().getCajero().getUsuario();
		peticion.setCodigoOperario(StringUtils.leftPad(codeOperador, 9, '0'));
		peticion.setCodigoTalon(numeroTarjeta);
		peticion.setCodigoTerminal(StringUtils.leftPad(ticket.getCabecera().getCodCaja(), 4, '0'));
		Integer x = new Random().nextInt(9999);
		peticion.setCodigoTransaccion(StringUtils.leftPad(x.toString(), 4, '0'));
		peticion.setImporte(importe);
		
		return service.sumaSaldo(peticion);
	}

}
