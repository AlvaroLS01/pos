package com.comerzzia.cardoso.pos.gui.ventas.tickets;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.rest.client.tickets.ResponseGetTicketDev;
import com.comerzzia.cardoso.pos.devices.dispositivo.tarjeta.conexflow.ConexFlowDatosPeticionPagoTarjeta;
import com.comerzzia.cardoso.pos.gui.ventas.tickets.pagos.rest.empleados.CARDOSOClientRestPromocionEmpleados;
import com.comerzzia.cardoso.pos.persistence.balanza.TicketBalanzaBean;
import com.comerzzia.cardoso.pos.persistence.fidelizacion.CardosoFidelizacionBean;
import com.comerzzia.cardoso.pos.persistence.promociones.PromoCuentaBean;
import com.comerzzia.cardoso.pos.persistence.promociones.PromocionCandidataBean;
import com.comerzzia.cardoso.pos.services.balanza.TicketBalanzaService;
import com.comerzzia.cardoso.pos.services.balanza.exception.TicketBalanzaServiceException;
import com.comerzzia.cardoso.pos.services.promociones.CardosoPromocionesService;
import com.comerzzia.cardoso.pos.services.promociones.empleado.AnulacionPromocionEmpleadoBean;
import com.comerzzia.cardoso.pos.services.promociones.empleado.ServicioAnulacionPromocionEmpleado;
import com.comerzzia.cardoso.pos.services.rest.PosRestService;
import com.comerzzia.cardoso.pos.services.sesion.CARDOSOSesionPromociones;
import com.comerzzia.cardoso.pos.services.ticket.cabecera.CARDOSOCabeceraTicket;
import com.comerzzia.cardoso.pos.services.ticket.cabecera.adicionales.DatosDescuentoGea;
import com.comerzzia.cardoso.pos.services.ticket.cabecera.adicionales.DatosOrigenTicketBean;
import com.comerzzia.cardoso.pos.services.ticket.cabecera.adicionales.PromocionEmpleadosCabeceraTicket;
import com.comerzzia.cardoso.pos.services.ticket.lineas.CARDOSOLineaTicket;
import com.comerzzia.cardoso.pos.services.ticket.lineas.CARDOSOLineaTicketProfesional;
import com.comerzzia.cardoso.pos.services.ticket.lineas.CardosoDatosDevolucionBean;
import com.comerzzia.cardoso.pos.services.ticket.lineas.CardosoLineaTicketService;
import com.comerzzia.cardoso.pos.utils.CardosoConstantes;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.core.util.xml.XMLDocumentException;
import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.firma.pt.HashSaftPt;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.balanza.BalanzaNoConfig;
import com.comerzzia.pos.core.dispositivos.dispositivo.balanza.IBalanza;
import com.comerzzia.pos.core.dispositivos.dispositivo.fidelizacion.ConsultaTarjetaFidelizadoException;
import com.comerzzia.pos.core.dispositivos.dispositivo.tarjeta.TarjetaCallback;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.balanza.SolicitarPesoArticuloController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.balanza.SolicitarPesoArticuloView;
import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.persistence.codBarras.CodigoBarrasBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.persistence.fidelizacion.CustomerCouponDTO;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoBean;
import com.comerzzia.pos.services.articulos.ArticuloNotFoundException;
import com.comerzzia.pos.services.articulos.ArticulosService;
import com.comerzzia.pos.services.articulos.tarifas.ArticuloTarifaNotFoundException;
import com.comerzzia.pos.services.articulos.tarifas.ArticulosTarifaService;
import com.comerzzia.pos.services.articulos.tarifas.TarifaArticuloDto;
import com.comerzzia.pos.services.codBarrasEsp.CodBarrasEspecialesServices;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.sesion.SesionImpuestos;
import com.comerzzia.pos.services.core.sesion.SesionPromociones;
import com.comerzzia.pos.services.core.usuarios.UsuarioNotFoundException;
import com.comerzzia.pos.services.core.usuarios.UsuariosService;
import com.comerzzia.pos.services.core.usuarios.UsuariosServiceException;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.cupones.CuponAplicationException;
import com.comerzzia.pos.services.cupones.CuponUseException;
import com.comerzzia.pos.services.cupones.CuponesServiceException;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionDescuentoDetalles;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.aparcados.TicketsAparcadosService;
import com.comerzzia.pos.services.ticket.cabecera.FirmaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketException;
import com.comerzzia.pos.services.ticket.pagos.IPagoTicket;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosPeticionPagoTarjeta;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosRespuestaPagoTarjeta;
import com.comerzzia.pos.services.ticket.profesional.TicketVentaProfesional;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;

import javafx.stage.Stage;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Component
@Scope("prototype")
@Primary
public class CARDOSOTicketManager extends TicketManager {

	private static final Logger log = Logger.getLogger(CARDOSOTicketManager.class.getName());

	/* GAP XX - REALIZAR DEVOLUCIONES SIN DOCUMENTO ORIGEN */
	public static final String UID_TICKET_FALSO = "DEVOLUCION_CARDOSO";
	public static final String VARIABLE_DEVOLUCION_SIN_ORIGEN = "POS.X_DEVOLUCION_SIN_ORIGEN";

	@Autowired
	private VariablesServices variablesServices;
	@Autowired
	private SesionPromociones sesionPromociones;
	@Autowired
	private CardosoLineaTicketService lineasTicketServices;
	@Autowired
	private CodBarrasEspecialesServices codBarrasEspecialesServices;
	@Autowired
	private TicketBalanzaService ticketBalanzaService;
	@Autowired
	private ArticulosService articulosServices;
	@Autowired
	private com.comerzzia.pos.services.ticket.TicketsService ticketsService;
	@Autowired
	private ServicioAnulacionPromocionEmpleado servicioAnulacionPromocionEmpleado;
	@Autowired
	private PosRestService posRestService;
	@Autowired
	private CardosoPromocionesService cardosoPromocionesService;
	@Autowired
	protected ArticulosTarifaService articulosTarifaService;
	@Autowired
	private MediosPagosService mediosPagosService;
	@Autowired
	private UsuariosService usuariosService;
	@Autowired
    private TicketsAparcadosService ticketsAparcadosService;

	@Override
	public List<Class<?>> getTicketClasses(TipoDocumentoBean tipoDocumento) {
		List<Class<?>> clases = super.getTicketClasses(tipoDocumento);
		clases.add(CARDOSOCabeceraTicket.class);
		clases.add(CARDOSOLineaTicket.class);
		clases.add(CARDOSOLineaTicketProfesional.class);
		return clases;
	}

	@Override
	protected LineaTicketAbstract createLinea() {
		return SpringContext.getBean(CARDOSOLineaTicket.class);
	}

	@Override
	protected void recuperarDatosPersonalizadosLinea(LineaTicket lineaRecuperada, LineaTicket nuevaLineaArticulo) {
		super.recuperarDatosPersonalizadosLinea(lineaRecuperada, nuevaLineaArticulo);

		/* GAP - PERSONALIZACIONES V3 - LOTES */
		((CARDOSOLineaTicket) nuevaLineaArticulo).setLotes(((CARDOSOLineaTicket) lineaRecuperada).getLotes());

		/* GAP - PERSONALIZACIONES V3 - NÚMEROS DE SERIE */
		((CARDOSOLineaTicket) nuevaLineaArticulo).setNumerosSerie(((CARDOSOLineaTicket) lineaRecuperada).getNumerosSerie());

		/* GAP - PERSONALIZACIONES V3 - INTEGRACIÓN BALANZA BIZERBA */
		((CARDOSOLineaTicket) nuevaLineaArticulo).setCodigoBarras(((CARDOSOLineaTicket) lineaRecuperada).getCodigoBarras());
		((CARDOSOLineaTicket) nuevaLineaArticulo).setUidTicketBalanza(((CARDOSOLineaTicket) lineaRecuperada).getUidTicketBalanza());

		/* GAP - PERSONALIZACIONES V3 - TRATAMIENTO DE PROMOCIONES NXM/DESCUENTO/TARIFA */
		((CARDOSOLineaTicket) nuevaLineaArticulo).setIdPromocionAplicable((((CARDOSOLineaTicket) lineaRecuperada).getIdPromocionAplicable()));

		/* GAP - PERSONALIZACIONES V3 - DESCUENTO TARIFA */
		((CARDOSOLineaTicket) nuevaLineaArticulo).setDescuentoTarifa((((CARDOSOLineaTicket) lineaRecuperada).getDescuentoTarifa()));
		((CARDOSOLineaTicket) nuevaLineaArticulo).setPrecioVentaSinDtoTarifa((((CARDOSOLineaTicket) lineaRecuperada).getPrecioVentaSinDtoTarifa()));
		((CARDOSOLineaTicket) nuevaLineaArticulo).setPrecioVentaTotalSinDtoTarifa((((CARDOSOLineaTicket) lineaRecuperada).getPrecioVentaTotalSinDtoTarifa()));
		((CARDOSOLineaTicket) nuevaLineaArticulo).setImporteDescuentoTarifa((((CARDOSOLineaTicket) lineaRecuperada).getImporteDescuentoTarifa()));

		((CARDOSOLineaTicket) nuevaLineaArticulo).setBackupPrecioTarifaOrigen(((CARDOSOLineaTicket) lineaRecuperada).getPrecioTarifaOrigen());
		((CARDOSOLineaTicket) nuevaLineaArticulo).setBackupPrecioTotalTarifaOrigen(((CARDOSOLineaTicket) lineaRecuperada).getPrecioTotalTarifaOrigen());

		/* GAP - PERSONALIZACIONES V3 - tratamiento promocion descuento */
		/*
		 * En caso de que la linea recuperada tenga una promocion de tipo descuento, debemos cargar los valores de los
		 * precios de la linea recuperada.
		 */
		for (PromocionLineaTicket promocionLinea : lineaRecuperada.getPromociones()) {
			if (CardosoPromocionesService.TIPO_PROMOCION_DESCUENTO.equals(promocionLinea.getIdTipoPromocion())) {
				nuevaLineaArticulo.setPrecioTarifaOrigen(lineaRecuperada.getPrecioTarifaOrigen());
				nuevaLineaArticulo.setPrecioTotalTarifaOrigen(lineaRecuperada.getPrecioTotalTarifaOrigen());
				nuevaLineaArticulo.setPrecioSinDto(lineaRecuperada.getPrecioSinDto());
				nuevaLineaArticulo.setPrecioTotalSinDto(lineaRecuperada.getPrecioTotalSinDto());
				nuevaLineaArticulo.setPrecioConDto(lineaRecuperada.getPrecioConDto());
				nuevaLineaArticulo.setPrecioTotalConDto(lineaRecuperada.getPrecioTotalConDto());
				nuevaLineaArticulo.setDescuento(BigDecimal.ZERO);
			}
		}
	}

	/**
	 * ######################################################################################## GAP XX - REALIZAR
	 * DEVOLUCIONES SIN DOCUMENTO ORIGEN Consultamos la variable "POS.X_DEVOLUCION_SIN_ORIGEN" para comprobar si
	 * realizamos este proceso.
	 */

	private DatosOrigenTicketBean datosOrigenDevolucionSinOrigen;

	public void setTicketOrigen(TicketVenta ticket) {
		this.ticketOrigen = ticket;
	}

	public DatosOrigenTicketBean getDatosOrigenDevolucionSinOrigen() {
		return datosOrigenDevolucionSinOrigen;
	}

	public void setDatosOrigenDevolucionSinOrigen(DatosOrigenTicketBean datosOrigenDevolucionSinOrigen) {
		this.datosOrigenDevolucionSinOrigen = datosOrigenDevolucionSinOrigen;
	}

	public boolean getRealizarDevolucionSinDocumento() {
		log.debug("getRealizarDevolucionSinDocumento() : GAP XX - REALIZAR DEVOLUCIONES SIN DOCUMENTO ORIGEN...");

		boolean resultado = false;
		try {
			resultado = variablesServices.getVariableAsString(VARIABLE_DEVOLUCION_SIN_ORIGEN).equals("S") ? true : false;
		}
		catch (Exception e) {
			String msgError = "Error al consultar la variable " + VARIABLE_DEVOLUCION_SIN_ORIGEN + " no se encontró, o no está bien configurada.";
			log.debug("getRealizarDevolucionSinDocumento() - " + msgError + " : " + e.getMessage(), e);
		}
		return resultado;
	}

	@Override
	public boolean comprobarTratamientoFiscalDev() {
		Long idTratamientoImpuestosOrigen = this.ticketOrigen.getCabecera().getTienda().getIdTratamientoImpuestos();
		if (idTratamientoImpuestosOrigen == null) {
			return true; // Por compatibilidad con versiones anteriores
		}
		Long idTratamientoImpuestosTicket = sesion.getAplicacion().getTienda().getCliente().getIdTratImpuestos();
		return idTratamientoImpuestosOrigen.equals(idTratamientoImpuestosTicket);
	}

	@Override
	public boolean recuperarTicketDevolucion(String codigo, String codAlmacen, String codCaja, Long idTipoDoc) throws TicketsServiceException {
		boolean resultadoEstandar = super.recuperarTicketDevolucion(codigo, codAlmacen, codCaja, idTipoDoc);
		if (!resultadoEstandar && getRealizarDevolucionSinDocumento()) {
			setTicketOrigenFalso(codigo, codAlmacen, codCaja, idTipoDoc);
		}
		return resultadoEstandar;
	}

	@Override
	public boolean recuperarTicketDevolucion(String codigo, String codAlmacen, String codCaja, Long idTipoDoc, boolean controlarPlazoMaximoDevolucion) throws TicketsServiceException {
		try {
			log.debug("recuperarTicketDevolucion() - Recuperando ticket...");
			byte[] xmlTicketOrigen = null;
			ResponseGetTicketDev datosDevolucion = null;

			// Si es localizador
			// Obtenemos por localizador desde central
			try {
				xmlTicketOrigen = obtenerTicketDevolucionCentralLocalizador(codigo, false, idTipoDoc);
			}
			catch (LineaTicketException e) {
				log.warn("recuperarTicketDevolucion() - Error al obtener ticket devolución desde central - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			}

			if (xmlTicketOrigen != null) {
				// Si no null, buscamos datos devolucion
				tratarTicketRecuperado(xmlTicketOrigen);

				datosDevolucion = obtenerDatosDevolucion(ticketOrigen.getUidTicket());
			}
			else {
				// Si null, obtenemos por localizador desde local
				List<TicketBean> tickets = ticketsService.consultarTicketLocalizador(codigo, Arrays.asList(idTipoDoc));
				if (!tickets.isEmpty()) {
					xmlTicketOrigen = tickets.get(0).getTicket();
					tratarTicketRecuperado(xmlTicketOrigen);
				}
			}

			// Si no tenemos ticket, consultamos como id de documento en lugar de como localizador
			if (xmlTicketOrigen == null) {
				// por codigo desde central
				try {
					xmlTicketOrigen = obtenerTicketDevolucionCentral(codCaja, codAlmacen, codigo, idTipoDoc);
				}
				catch (Exception e) {
					log.warn("recuperarTicketDevolucion() - Error al obtener ticket devolución desde central - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
				}

				if (xmlTicketOrigen != null) {
					// Si no null, buscamos datos devolucion
					tratarTicketRecuperado(xmlTicketOrigen);
					datosDevolucion = obtenerDatosDevolucion(ticketOrigen.getUidTicket());
				}
				else {
					// Si null, obtenemos por codigo desde local
					TicketBean ticketA = ticketsService.consultarTicketAbono(codAlmacen, codCaja, codigo, idTipoDoc);
					if (ticketA != null) {
						xmlTicketOrigen = ticketA.getTicket();
						tratarTicketRecuperado(xmlTicketOrigen);
					}
					else {
						throw new TicketsServiceException("No se ha encontrado ticket con codigo: " + codigo);
					}
				}
			}

			asignarLineasDevueltas(datosDevolucion);

			// descontarLineasNegativasTicketOrigen();

		}
		catch (TicketsServiceException e) {
			log.error("recuperarTicketDevolucion() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			return false;
		}

		if (controlarPlazoMaximoDevolucion) {
			controlarPlazoMaximoDevolucion();
		}

		return true;
	}

	public void setTicketOrigenFalso(String codigo, String codAlmacen, String codCaja, Long idTipoDoc) {
		log.debug("setTicketOrigenFalso() : GAP XX - REALIZAR DEVOLUCIONES SIN DOCUMENTO ORIGEN...");

		try {
			Long idTicket = new Long(0);

			// DATOS INICIALES TICKET FALSO
			TicketVentaAbono ticketOrigenFalso = new TicketVentaAbono();
			ticketOrigenFalso.setFecha(new Date());
			ticketOrigenFalso.setCajero(sesion.getSesionUsuario().getUsuario());
			ticketOrigenFalso.setCliente(sesion.getAplicacion().getTienda().getCliente());
			ticketOrigenFalso.setIdTicket(idTicket);

			// CABECERA TICKET FALSO
			ticketOrigenFalso.getCabecera().inicializarCabecera(ticketOrigenFalso);
			ticketOrigenFalso.getCabecera().setFecha(new Date());
			ticketOrigenFalso.getCabecera().setCajero(sesion.getSesionUsuario().getUsuario());
			ticketOrigenFalso.getCabecera().setCliente(sesion.getAplicacion().getTienda().getCliente());
			ticketOrigenFalso.getCabecera().setTienda(sesion.getAplicacion().getTienda());

			TipoDocumentoBean documentoActivoTicketFalso = sesion.getAplicacion().getDocumentos().getDocumento(idTipoDoc);
			TipoDocumentoBean documentoAbonoTicketFalso = documentos.getDocumentoAbono(documentoActivoTicketFalso.getCodtipodocumento());
			ticketOrigenFalso.getCabecera().setDocumento(documentoAbonoTicketFalso);
			ticketOrigenFalso.getCabecera().setTipoDocumento(documentoAbonoTicketFalso.getIdTipoDocumento());
			ticketOrigenFalso.getCabecera().setCodTipoDocumento(documentoAbonoTicketFalso.getCodtipodocumento());
			ticketOrigenFalso.getCabecera().setFormatoImpresion(documentoAbonoTicketFalso.getFormatoImpresion());

			ticketOrigenFalso.getCabecera().setIdTicket(idTicket);
			ticketOrigenFalso.getCabecera().setCodTicket(codigo);
			ticketOrigenFalso.getCabecera().setUidTicket(UID_TICKET_FALSO);
			ticketOrigenFalso.getCabecera().setSerieTicket(codigo);

			ticketOrigenFalso.inicializarTotales();

			String fechaFormateada = FormatUtil.getInstance().formateaFechaHoraTicket(ticketOrigenFalso.getFecha());
			SimpleDateFormat formateaFechaSimple = new SimpleDateFormat("yyyy-MM-dd");
			String fechaSimple = formateaFechaSimple.format(ticketOrigenFalso.getFecha());
			DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
			simbolos.setDecimalSeparator('.');
			DecimalFormat formateaSeparadorDecimal = new DecimalFormat("0.00", simbolos);
			String totalTicket = formateaSeparadorDecimal.format(BigDecimal.ZERO);

			String firmaString = fechaSimple + ";" + fechaFormateada + ";" + ticketOrigenFalso.getCabecera().getCodTicket() + ";" + totalTicket + ";" + "";
			firmaString = HashSaftPt.firma(firmaString);
			FirmaTicket firma = new FirmaTicket();
			firma.setFirma(firmaString);
			ticketOrigenFalso.getCabecera().setFirma(firma);

			setTicketOrigen(ticketOrigenFalso);

			// Generamos los datos de documento origen con los datos de CARDOSO y con el codTicket para Portugal.
			DatosOrigenTicketBean datosOrigenDevolucionFalsos = new DatosOrigenTicketBean();
			datosOrigenDevolucionFalsos.setUidTicket(UID_TICKET_FALSO);
			datosOrigenDevolucionFalsos.setNumFactura(idTicket);
			datosOrigenDevolucionFalsos.setCodCaja(sesion.getAplicacion().getCodCaja());
			datosOrigenDevolucionFalsos.setCodAlmacen(sesion.getAplicacion().getTienda().getCodAlmacen());

			// Formamos el código de ticket y la serie como Portugal : COTIPODOC + " " + PERIODOACTUAL + CODTIENDA +
			// CODCAJA
			SimpleDateFormat format = new SimpleDateFormat("yyyy");
			String serie = documentoActivoTicketFalso.getCodtipodocumento() + " " + format.format(new Date()) + sesion.getAplicacion().getTienda().getCodAlmacen()
			        + sesion.getAplicacion().getCodCaja();
			datosOrigenDevolucionFalsos.setSerie(serie);
			// A partir de la serie, generamos el código del ticket : SERIE + / + 8 DIGITOS CON DOCUMENTO INCLUIDO
			// (00000063)
			datosOrigenDevolucionFalsos.setCodTicket(serie + "/" + StringUtils.leftPad(codigo, 8, "0"));

			/*
			 * Sacamos los datos del tipo de documento que vamos a usar para el ticket, en nuestro caso es factura
			 * simplificada
			 */
			datosOrigenDevolucionFalsos.setIdTipoDocumento(documentoActivoTicketFalso.getIdTipoDocumento());
			datosOrigenDevolucionFalsos.setCodTipoDocumento(documentoActivoTicketFalso.getCodtipodocumento());
			datosOrigenDevolucionFalsos.setDesTipoDocumento(documentoActivoTicketFalso.getDestipodocumento());

			this.datosOrigenDevolucionSinOrigen = datosOrigenDevolucionFalsos;
		}
		catch (Exception e) {
			String msgError = "Error al generar la firma del ticket falso para devoluciones sin documento origen.";
			log.error("setTicketOrigenFalso() - " + msgError + " : " + e.getMessage(), e);
		}
	}

	/**
	 * ######################################################################################## GAP - PERSONALIZACIONES
	 * V3 - DESCUENTO TARIFA Recalculos necesarios en las lineas y las promociones de dicha linea para poder sacar los
	 * datos de la tabla de tarifas anexa.
	 */
	@Override
	public void recalcularConPromociones() {
		limpiarDescuentosGea();
		recalcularConPromocionesCARDOSO();
		recalcularDescuentosGea();
		ticketPrincipal.getTotales().recalcular();
		// super.recalcularConPromociones();
	}

	private void limpiarDescuentosGea() {
		// Limpiamos descuentos gea para volver a carcularlos
		for (CARDOSOLineaTicket linea : (List<CARDOSOLineaTicket>) ticketPrincipal.getLineas()) {

			if (linea.getDatosDescuentoGea() != null) {
				linea.setDatosDescuentoGea(null);
				linea.setDescuentoManual(BigDecimal.ZERO);
			}
		}
	}

	/* ########################################### CAR-44 Fidelización Descuento #################################### */
	private void recalcularDescuentosGea() {

		try {
			List<String> colectivosAplicables = consultarSplitVariables(CardosoConstantes.ID_VAR_COLECTIVO_FIDELIZADOS);
			List<String> empresasAplicables = consultarSplitVariables(CardosoConstantes.ID_VAR_EMPRESAS);
			List<String> proveedoresExcluidos = consultarSplitVariables(CardosoConstantes.ID_VAR_PROVEEDORES_EXCLUIDOS);
			BigDecimal descuentoTarifaMax = variablesServices.getVariableAsBigDecimal(CardosoConstantes.ID_VAR_DESCUENTO_TARIFA_MAX);
			BigDecimal descuentoFidelizacion = variablesServices.getVariableAsBigDecimal(CardosoConstantes.ID_VAR_DESCUENTO);
			BigDecimal limiteMensual = variablesServices.getVariableAsBigDecimal(CardosoConstantes.ID_VAR_LIMITE_MENSUAL);

			if (ticketPrincipal.getCabecera().getDatosFidelizado() != null && ticketPrincipal.getCabecera().getDatosFidelizado() instanceof CardosoFidelizacionBean) {

				CardosoFidelizacionBean fidelizado = (CardosoFidelizacionBean) ticketPrincipal.getCabecera().getDatosFidelizado();

				// Primero vemos si aplica al fidelizado y empresa, luego por línea vemos si podemos aplicar el
				// descuento a esta
				if (aplicaDescuentoGEAaFidelizadoEmpresa(colectivosAplicables, empresasAplicables, limiteMensual, fidelizado)) {

					log.debug("recalcularDescuentosGea() - El fidelizado tiene la posibilidad de recibir descuento GEA, se procede a comprobar si es aplicable a las líneas");
					for (CARDOSOLineaTicket linea : (List<CARDOSOLineaTicket>) ticketPrincipal.getLineas()) {

						boolean aplicaDescuentoFidelizacion = false;

						// Si no están activos en promo se intenta aplicar
						if ((linea.getPromociones() == null || (linea.getPromociones() != null && linea.getPromociones().isEmpty()))
						        && (linea.getPromocionesAplicables() == null || (linea.getPromocionesAplicables() != null && linea.getPromocionesAplicables().isEmpty()))
						        && linea.getPromocionMonograficaAplicada() == null) {
							log.debug("recalcularDescuentosGea() - El artículo no tiene promocion, intentando aplicar descuento GEA");
							// PROVEEDOR y que no supere el DESCUENTO DE TARIFA MAXIMA
							if (!proveedoresExcluidos.contains(linea.getArticulo().getCodProveedor()) && BigDecimalUtil.isMenor(linea.getDescuentoTarifa(), descuentoTarifaMax)) {

								descuentoFidelizacion = calcularDtoGea(linea, descuentoFidelizacion, fidelizado, limiteMensual);

								// Si es igual a CERO, significa que en este ticket ya se ha llegado al limite mensual
								// por lo que no se aplica
								if (!BigDecimalUtil.isIgualACero(descuentoFidelizacion)) {
									aplicaDescuentoFidelizacion = true;
								}

								/*
								 * #################################### APLICACIÓN DESCUENTO
								 * ####################################
								 */
								if (aplicaDescuentoFidelizacion && !linea.tieneDescuentoManual()) {

									linea.setDescuentoManual(descuentoFidelizacion);
									linea.recalcularImporteFinal();

									if (linea.getDatosDescuentoGea() == null) {
										linea.setDatosDescuentoGea(new DatosDescuentoGea());
									}

									DatosDescuentoGea datosDescuento = linea.getDatosDescuentoGea();

									datosDescuento.setPorcentajeDescuento(descuentoFidelizacion.setScale(2, RoundingMode.HALF_UP));
									datosDescuento.setImporteDescuentoLinea(datosDescuento.getImporteDescuentoLinea().add(linea.getImporteDescuento()));

									log.debug("recalcularDescuentosGea() - Se ha aplicado a la línea " + linea.getIdLinea() + " un descuento del " + descuentoFidelizacion + "%");
								}

							}

						}
						else {
							log.debug("recalcularDescuentosGea() - Al tener promoción activa, no podemos aplicar DTO gea");
						}
					}

				}
			}
		}
		catch (Exception e) {
			if (e instanceof NumberFormatException) {
				log.warn("recalcularDescuentosGea() - Formato del valor de las variables de GEA erróneo, revise los valores de las variables, no se aplicará el descuento GEA." + e.getMessage(), e);
			}
			else {
				log.warn("recalcularDescuentosGea() - No ha sido posible aplicar el descuento GEA " + e.getMessage(), e);
			}
		}
	}

	/*
	 * Se comprueba que aplique al fidelizado: - Que no supere el limite mensual - Que pertenezca a un colectivo GEA Que
	 * la empresa aplique este descuento
	 */
	private boolean aplicaDescuentoGEAaFidelizadoEmpresa(List<String> colectivosAplicables, List<String> empresasAplicables, BigDecimal limiteMensual, CardosoFidelizacionBean fidelizado) {
		log.debug("aplicaDescuentoGEAaFidelizadoEmpresa() - Comprobando si es posible aplicar el descuento GEA al fidelizado");
		boolean aplicaDTO = true;

		// COLECTIVOS
		if (!fidelizado.getCodColectivos().stream().anyMatch(colectivosAplicables::contains)) {
			aplicaDTO = false;
		}
		// LIMITE MENSUAL
		if (BigDecimalUtil.isMayorOrIgual(fidelizado.getImporteDescuentoAcumulado(), limiteMensual)) {
			aplicaDTO = false;
		}
		// EMPRESAS
		if (!empresasAplicables.contains(sesion.getAplicacion().getEmpresa().getCodEmpresa())) {
			aplicaDTO = false;
		}

		return aplicaDTO;
	}

	private BigDecimal calcularDtoGea(CARDOSOLineaTicket linea, BigDecimal descuentoFidelizacion, CardosoFidelizacionBean fidelizado, BigDecimal limiteMensual) {
		log.debug("calcularDtoGea() - calculando descuento gea para la linea");
		// Hacemos el calculo del descuento para ver si este superaria el limite mensual del fidelizado
		BigDecimal importeTotalConDtoSinRedondear = BigDecimalUtil.menosPorcentajeR4(linea.getPrecioTotalSinDto(), descuentoFidelizacion).multiply(linea.getCantidad());

		// BigDecimal importeTotalConDto = BigDecimalUtil.redondear(importeTotalConDtoSinRedondear, 2);

		BigDecimal importeDescuento = linea.getPrecioTotalSinDto().multiply(linea.getCantidad()).subtract(importeTotalConDtoSinRedondear);

		BigDecimal dtoRestanteAux = limiteMensual.subtract(fidelizado.getImporteDescuentoAcumulado());

		// Si hay descuentos GEA ya aplicados en el ticket lo restamos del dtoRestante disponible que le queda al
		// fidelizado
		for (CARDOSOLineaTicket lineaTicket : (List<CARDOSOLineaTicket>) ticketPrincipal.getLineas()) {

			if (lineaTicket.getDatosDescuentoGea() != null) {
				dtoRestanteAux = dtoRestanteAux.subtract(lineaTicket.getDatosDescuentoGea().getImporteDescuentoLinea());
			}

		}

		if (BigDecimalUtil.isMenorACero(dtoRestanteAux)) {
			return BigDecimal.ZERO;
		}

		BigDecimal importeDtoRestanteFuturo = dtoRestanteAux.subtract(importeDescuento);

		if (BigDecimalUtil.isMenorACero(importeDtoRestanteFuturo)) {
			BigDecimal importeDescuentoPermitidoRestante = dtoRestanteAux;

			// Calcular el nuevo porcentaje de descuento necesario para ajustarse al límite mensual
			descuentoFidelizacion = importeDescuentoPermitidoRestante.divide(linea.getPrecioTotalSinDto().multiply(linea.getCantidad()), MathContext.DECIMAL128).multiply(BigDecimal.valueOf(100));
		}

		return descuentoFidelizacion;
	}

	private List<String> consultarSplitVariables(String variableConstante) {

		List<String> listaString = new ArrayList<String>();

		String colectivosAplicables = variablesServices.getVariableAsString(variableConstante);

		if (StringUtils.isNotBlank(colectivosAplicables)) {
			listaString = Arrays.asList(colectivosAplicables.split(";"));
		}

		return listaString;
	}

	public void recalcularConPromocionesCARDOSO() {
		log.debug("insertarLineaVenta() : GAP - PERSONALIZACIONES V3 - DESCUENTO TARIFA");
		BigDecimal puntosAnteriores = BigDecimal.ZERO;
		if (isEsFacturacionVentaCredito()) {
			puntosAnteriores = ticketPrincipal.getTotales().getPuntos();
		}
		/* Rescatamos los datos de Backup guardado en la parte de facturación. */
		for (CARDOSOLineaTicket linea : (List<CARDOSOLineaTicket>) getTicket().getLineas()) {
			if (!linea.tieneCambioPrecioManual() && linea.getBackupPrecioTarifaOrigen() != null) {
				for (PromocionLineaTicket promocionLinea : linea.getPromociones()) {
					if (CardosoPromocionesService.TIPO_PROMOCION_DESCUENTO.equals(promocionLinea.getIdTipoPromocion())) {
						linea.setPrecioTarifaOrigen(linea.getBackupPrecioTarifaOrigen());
						linea.setPrecioTotalTarifaOrigen(linea.getBackupPrecioTotalTarifaOrigen());
						linea.setPrecioSinDto(linea.getBackupPrecioTarifaOrigen());
						linea.setPrecioTotalSinDto(linea.getBackupPrecioTotalTarifaOrigen());
						linea.setPrecioConDto(linea.getBackupPrecioTarifaOrigen());
						linea.setPrecioTotalConDto(linea.getBackupPrecioTotalTarifaOrigen());
						linea.setDescuento(BigDecimal.ZERO);
						linea.recalcularImporteFinal();
					}
				}
			}
		}
		sesionPromociones.aplicarPromociones((TicketVentaAbono) ticketPrincipal);
		/*
		 * Después de realizar la aplicación de promociones, seteamos los datos referentes a la tabla anexa de tarifas,
		 * y además recalculamos los datos de importe de descuento de la promoción, para que pueda realizar el recalculo
		 * del precio de la linea correctamente.
		 */
		for (CARDOSOLineaTicket linea : (List<CARDOSOLineaTicket>) getTicket().getLineas()) {
			if (!linea.tieneCambioPrecioManual()) {
				for (PromocionLineaTicket promocionLinea : linea.getPromociones()) {
					if (CardosoPromocionesService.TIPO_PROMOCION_DESCUENTO.equals(promocionLinea.getIdTipoPromocion())) {

						// LUSTRUM-114718 Se comenta ya que los datos de la promocion se aplican correctamente sin él y
						// se aplican correctamente el ticket

						// linea.setPrecioTarifaOrigen(linea.getPrecioVentaSinDtoTarifa());
						// linea.setPrecioTotalTarifaOrigen(linea.getPrecioVentaTotalSinDtoTarifa());
						// linea.setPrecioSinDto(linea.getPrecioVentaSinDtoTarifa());
						// linea.setPrecioTotalSinDto(linea.getPrecioVentaTotalSinDtoTarifa());
						// linea.setPrecioConDto(linea.getPrecioVentaSinDtoTarifa());
						// linea.setPrecioTotalConDto(linea.getPrecioVentaTotalSinDtoTarifa());
						/* Recalculamos el importe de descuento de la promoción */
						// BigDecimal porcentajeDescuento = linea.getDescuento().divide(new
						// BigDecimal(100)).setScale(2);
						// BigDecimal importeDescuentoPromocion =
						// linea.getPrecioTotalConDto().multiply(porcentajeDescuento);
						// importeDescuentoPromocion =
						// BigDecimalUtil.redondear(linea.getCantidad().multiply(importeDescuentoPromocion));
						// promocionLinea.setImporteTotalDto(importeDescuentoPromocion);
						// promocionLinea.setImporteTotalDtoMenosMargen(importeDescuentoPromocion);
						// linea.recalcularImporteFinal();
					}
				}
			}
		}
		ticketPrincipal.getTotales().recalcular();
		ticketPrincipal.getTotales().addPuntos(puntosAnteriores);
	}

	/**
	 * ######################################################################################## GAP - CAJERO AUXILIAR
	 * Comprobamos si la variable "POS.CONTROL_BORRADO" es igual a "S". En ese caso, deberemos pedir la autorización
	 * para realizar la acción.
	 */
	public static final String VARIABLE_AUTORIZAR_ACCIONES = "POS.CONTROL_BORRADO";

	public Boolean necesitaAutorizacion() {
		log.debug("necesitaAutorizacion() : GAP - CAJERO AUXILIAR");

		return variablesServices.getVariableAsString(VARIABLE_AUTORIZAR_ACCIONES).equals("S");
	}

	/**
	 * ######################################################################################## GAP - PERSONALIZACIONES
	 * V3 - SERIE ALBARÁN Al generar el ticket por primera vez, cargamos el código de almacen de la tienda principal en
	 * la serie albarán.
	 */

	public static final String VARIABLE_USA_SERIE_ESPECIAL = "FACTURACION.USA_SERIE_ESPECIAL";

	@Override
	protected void crearTicket() throws PromocionesServiceException, DocumentoException {
		super.crearTicket();
		estableceSerieAlbaran();
	}

	/**
	 * Solo tenemos en cuenta la variable en las facturas completas, si la veriable indica que debemos utilizar series
	 * especiales de facturación las asignamos, si no establecemos como serie del albarán el almacén.
	 */
	public void estableceSerieAlbaran() {
		log.debug("estableceSerieAlbaran() : GAP - PERSONALIZACIONES V3 - SERIE ALBARÁN");

		String variable = variablesServices.getVariableAsString(VARIABLE_USA_SERIE_ESPECIAL);
		String serieAlbaran = getTicket().getTienda().getCodAlmacen();

		String sCodTipoDocumentoOrigen = "";
		if (getTicketOrigen() != null) {
			sCodTipoDocumentoOrigen = getTicketOrigen().getCabecera().getCodTipoDocumento();
		}
		if ((getTicket() instanceof TicketVentaProfesional || sCodTipoDocumentoOrigen.equals("FT")) && variable.equals("S")) {
			/* Si los todos los pagos son de contado utilizamos la serie 1, si no utilizamos la 4 */
			boolean sonContados = true;
			for (Object pago : getTicket().getPagos()) {
				sonContados = sonContados && ((PagoTicket) pago).getMedioPago().getContado();
			}
			if (sonContados) {
				serieAlbaran = "1";
			}
			else {
				serieAlbaran = "4";
			}
		}
		((CARDOSOCabeceraTicket) getTicket().getCabecera()).setSerieAlbaran(serieAlbaran);
	}

	/**
	 * ######################################################################################## GAP - CAJERO AUXILIAR Al
	 * generar el ticket por primera vez, cargamos el código de almacen de la tienda principal en la serie albarán.
	 */

	private UsuarioBean cajeroAutoriza;
	private CardosoDatosDevolucionBean datosDevolucionAutoriza;

	public CARDOSOTicketManager() {
		super();
		cajeroAutoriza = new UsuarioBean();
		datosDevolucionAutoriza = new CardosoDatosDevolucionBean();
	}

	public void setDatosAutorizacion(String usuario, String nombre, String documento, String tienda) {
		cajeroAutoriza.setUsuario(usuario);
		cajeroAutoriza.setDesusuario(nombre);
		datosDevolucionAutoriza.setTicketDevolucion(documento);
		datosDevolucionAutoriza.setTiendaDevolucion(tienda);
	}

	@Override
	protected void addLinea(LineaTicketAbstract linea) throws LineaTicketException {
		if (!(cajeroAutoriza.getUsuario() == null)) {
			((CARDOSOLineaTicket) linea).setCajeroAux(cajeroAutoriza);
		}
		super.addLinea(linea);
	}

	/**
	 * ######################################################################################## GAP - PERSONALIZACIONES
	 * V3 - INTEGRACIÓN BALANZA BIZERBA Tratamos el código de barras especial para poder leer el ticket de balanza.
	 */

	@Override
	protected LineaTicket tratarCodigoBarraEspecialTicket(CodigoBarrasBean codBarrasEspecial) throws LineaTicketException {
		log.debug("tratarCodigoBarraEspecialTicket() :  GAP - INTEGRACIÓN BALANZA BIZERBA");

		String codigoTicketBalanza = codBarrasEspecial.getCodticket();
		String LINEAS = "lineas";
		String LINEA = "linea";
		String ARTICULO = "articulo";
		String PRECIO = "precio";
		String PESO = "peso";
		String CANTIDAD = "cantidad";

		Long numeroTicketBalanza = 0L;
		try {
			numeroTicketBalanza = Long.valueOf(codigoTicketBalanza);
		}
		catch (Exception e) {
			String mensajeError = "Error al transformar el código de barras especial para consultar el ticket de balanza : " + e.getMessage();
			log.error("tratarCodigoBarraEspecialTicket() - " + mensajeError);
			numeroTicketBalanza = 0L;
		}

		/* Cargamos el uid_ticket_balanza y el xml de la base de datos */
		CARDOSOLineaTicket lineaTicket = new CARDOSOLineaTicket();
		try {
			/* Leemos el xml del ticket de balanza */
			TicketBalanzaBean ticketBalanzaBean = ticketBalanzaService.consultarTicketBalanza(codigoTicketBalanza);
			if (StringUtils.isNotBlank(ticketBalanzaBean.getUidTicket())) {
				throw new Exception(I18N.getTexto("tratarCodigoBarraEspecialTicket() - El ticket de balanza indicado ya ha sido procesado."));
			}

			/* Comprobamos que el ticket introducido no está en la venta actual */
			if (getTicket().getLineas() != null && !getTicket().getLineas().isEmpty()) {
				for (CARDOSOLineaTicket lineaTicketActual : (List<CARDOSOLineaTicket>) getTicket().getLineas()) {
					if (StringUtils.isNotBlank(lineaTicketActual.getUidTicketBalanza())) {
						if (ticketBalanzaBean.getUidTicketBalanza().equals(lineaTicketActual.getUidTicketBalanza())) {
							String mensajeError = "El Ticket Balanza " + numeroTicketBalanza + " ya esta incluido en la venta actual";
							throw new Exception(I18N.getTexto(mensajeError));
						}
					}
				}
			}

			log.info("tratarCodigoBarraEspecialTicket() - Leyendo xml del ticket de balanza. Código: " + numeroTicketBalanza);
			XMLDocument xmlDocument = new XMLDocument(ticketBalanzaBean.getTicketBalanza());
			XMLDocumentNode nodeRoot = xmlDocument.getRoot();
			XMLDocumentNode Nodolineas = nodeRoot.getNodo(LINEAS);
			List<XMLDocumentNode> lineas = Nodolineas.getHijos(LINEA);

			String mensajeError = "";
			List<Integer> lineasInsertadas = new ArrayList<Integer>();
			for (XMLDocumentNode linea : lineas) {
				String codart = linea.getNodo(ARTICULO).getValue();
				/*
				 * Comprobar si existe el artículo , en caso de no existir insertamos 0 a la izquierda hasta llegar a 6.
				 */
				codart = articuloCorrecto(codart);
				try {
					BigDecimal precio = new BigDecimal(linea.getNodo(PRECIO).getValue());

					/* Cantidad, si el peso viene a cero tomamos la cantidad */
					BigDecimal cantidad = new BigDecimal(linea.getNodo(PESO).getValue());
					if (BigDecimalUtil.isIgualACero(cantidad)) {
						cantidad = new BigDecimal(linea.getNodo(CANTIDAD).getValue());
					}

					/* Insertamos el articulo en la venta */
					lineaTicket = (CARDOSOLineaTicket) nuevaLineaArticulo(codart, "*", "*", cantidad, null);
					lineaTicket.setGenerico(false);
					/* Guaradamos la linea que se ha insertado por si tenemos que borrarla */
					lineasInsertadas.add(lineaTicket.getIdLinea());

					((CARDOSOLineaTicket) lineaTicket).setUidTicketBalanza(ticketBalanzaBean.getUidTicketBalanza());
					((CARDOSOLineaTicket) lineaTicket).setCantidad(cantidad);

					/* Calculamos el precio */
					((CARDOSOLineaTicket) lineaTicket).setPrecioTotalSinDto(precio);
					SesionImpuestos sesionImpuestos = sesion.getImpuestos();
					Long idTratImpuestos = null;
					if (lineaTicket.getCabecera().getCliente() != null) {
						idTratImpuestos = lineaTicket.getCabecera().getCliente().getIdTratImpuestos();
					}
					else {
						idTratImpuestos = ticketPrincipal.getCabecera().getCliente().getIdTratImpuestos();
					}

					if (!BigDecimalUtil.isIgual(precio, lineaTicket.getPrecioTotalTarifaOrigen())) {
						BigDecimal precioSinDto = sesionImpuestos.getPrecioSinImpuestos(lineaTicket.getCodImpuesto(), precio, idTratImpuestos);
						lineaTicket.setPrecioSinDto(precioSinDto);
					}

					ticketPrincipal.getTotales().recalcular();
				}
				catch (Exception e) {
					mensajeError = mensajeError + "\n\r" + e.getMessage() + " - " + codart;
				}
			}

			if (StringUtils.isNotBlank(mensajeError)) {
				/* Antes de lanzar el error borramos las lineas insertadas */
				for (Integer idLinea : lineasInsertadas) {
					eliminarLineaArticulo(idLinea);
				}
				ticketPrincipal.getTotales().recalcular();

				mensajeError = "Error al registrar el Ticket de Balanza (IMPORTE TOTAL: " + ticketBalanzaBean.getTotal().setScale(2, RoundingMode.UP) + ") : " + mensajeError;
				throw new Exception(mensajeError);
			}

			return (lineaTicket);
		}
		catch (Exception e) {
			String mensajeError = "";
			if (e instanceof TicketBalanzaServiceException || e.getCause() instanceof XMLDocumentException) {
				mensajeError = "Error al cargar el ticket de balanza de la base de datos";
			}
			else if (e instanceof XMLDocumentException || e.getCause() instanceof XMLDocumentException) {
				mensajeError = "Error al carga el xml del ticket de balanza.";
			}
			else {
				mensajeError = e.getMessage();
			}
			log.error("tratarCodigoBarraEspecialTicket() - " + I18N.getTexto(mensajeError) + " - " + I18N.getTexto(e.getMessage()));
			throw new LineaTicketException(I18N.getTexto(mensajeError), e);
		}
	}

	/**
	 * Este método es necesario porque la balanza trata los códigos como numéricos, el cliente tiene al principio del
	 * código 0.
	 * 
	 * @return String
	 */
	public String articuloCorrecto(String codArt) {
		log.debug("articuloCorrecto() :  GAP - INTEGRACIÓN BALANZA BIZERBA");

		/* Comprobamos si existe el artículo. */
		ArticuloBean articulo = null;
		try {
			articulo = articulosServices.consultarArticulo(codArt);
		}
		catch (Exception e) {
			String mensajeError = "";
			if (e instanceof ArticuloNotFoundException || e.getCause() instanceof ArticuloNotFoundException) {
				mensajeError = "No se ha encontrado el artículo indicado en el ticket balanza: " + codArt;
			}
			else {
				mensajeError = "Error al consultar el artículo indicado en el ticket balanza: " + codArt;
			}
			log.error("articuloCorrecto() - " + mensajeError + " : " + e.getMessage());
		}

		/* En caso de no existir devolvemos el string formateado a 6 dígitos con 0 a la izquierda. */
		if (articulo == null) {
			return StringUtils.leftPad(codArt, 6, "0");
		}
		return codArt;
	}

	/**
	 * ######################################################################################## GAP - PROMOCION
	 * CANDIDATA Tratamos diferentes promociones para aplicar una lógica diferente dependiento del tipo de promoción.
	 */

	@Override
	public synchronized LineaTicket nuevaLineaArticulo(String codArticulo, String desglose1, String desglose2, BigDecimal cantidad, Stage stage, Integer idLineaDocOrigen,
	        boolean esLineaDevolucionPositiva) throws LineaTicketException {
		log.debug("nuevaLineaArticulo() :  GAP - TRATAMIENTO DE PROMOCIONES NXM/DESCUENTO");

		CARDOSOLineaTicket lineaTicket = (CARDOSOLineaTicket) nuevaLineaArticuloCardoso(codArticulo, desglose1, desglose2, cantidad, stage, idLineaDocOrigen, esLineaDevolucionPositiva, true);
		return (LineaTicket) lineaTicket;
	}

	public synchronized LineaTicket nuevaLineaArticuloCardoso(String codArticulo, String desglose1, String desglose2, BigDecimal cantidad, Stage stage, Integer idLineaDocOrigen,
	        boolean esLineaDevolucionPositiva, boolean applyDUN14Factor) throws LineaTicketException {
		log.debug("nuevaLineaArticuloCardoso() :  GAP - TRATAMIENTO DE PROMOCIONES NXM/DESCUENTO");

		log.debug("nuevaLineaArticulo() - Creando nueva línea de artículo...");
		LineaTicketAbstract linea = null;
		boolean isCupon = sesionPromociones.isCouponWithPrefix(codArticulo);
		if (isCupon) {
			try {
				CustomerCouponDTO customerCouponDTO = new CustomerCouponDTO(codArticulo, true);
				if (!sesionPromociones.aplicarCupon(customerCouponDTO, (TicketVentaAbono) ticketPrincipal)) {
					throw new LineaTicketException(I18N.getTexto("No se ha podido aplicar el cupón."));
				}
				ticketPrincipal.getTotales().recalcular();
			}
			catch (CuponAplicationException | CuponUseException | CuponesServiceException ex) {
				log.warn("nuevaLineaArticulo() - Error en la aplicación del cupón -" + ex.getMessageI18N());
				throw new LineaTicketException(ex.getMessageI18N(), ex);
			}
		}
		else {
			BigDecimal precio = null;
			boolean pesarArticulo = stage != null;
			String codBarras = null;
			try {
				CodigoBarrasBean codBarrasEspecial = codBarrasEspecialesServices.esCodigoBarrasEspecial(codArticulo);
				if (codBarrasEspecial != null) {
					codBarras = codArticulo;
					pesarArticulo = false;

					if (codBarrasEspecial.getCodticket() != null) {
						return tratarCodigoBarraEspecialTicket(codBarrasEspecial);
					}

					codArticulo = codBarrasEspecial.getCodart();
					String cantCodBar = codBarrasEspecial.getCantidad();
					if (cantCodBar != null) {
						cantidad = FormatUtil.getInstance().desformateaBigDecimal(cantCodBar, 3);
					}
					else {
						cantidad = BigDecimal.ONE;
					}
					String precioCodBar = codBarrasEspecial.getPrecio();
					if (precioCodBar != null) {
						precio = FormatUtil.getInstance().desformateaBigDecimal(codBarrasEspecial.getPrecio(), 2);

						// creamos una linea auxiliar para obtener el precio
						LineaTicket linea_aux = evaluarPromocionBarrasEspecial(codArticulo, desglose1, desglose2);
						BigDecimal precioLinea = BigDecimal.ZERO;
						if (linea_aux != null) {
							precioLinea = linea_aux.getPrecioTotalConDto();
						}

						// si el precio de la linea es distinto de cero, calculamos la cantidad
						if (!precioLinea.equals(BigDecimal.ZERO)) {
							cantidad = precio.divide(precioLinea, 3, RoundingMode.HALF_UP);
							precio = linea_aux.getPrecioTotalSinDto();
						}

						// comprobamos si la tarifa está configurada como precio con impuestos
						TarifaArticuloDto tarifaArticuloDto = null;
						ArticuloBean articuloAux = null;
						SqlSession sqlSession = new SqlSession();

						try {
							sqlSession.openSession(SessionFactory.openSession());
							tarifaArticuloDto = articulosTarifaService.consultarArticuloTarifa(sqlSession, codArticulo, ((TicketVentaAbono) ticketPrincipal).getCliente(), desglose1, desglose2,
							        new Date());

							articuloAux = articulosServices.consultarArticulo(codArticulo);
						}
						catch (ArticuloTarifaNotFoundException e) {
							log.error("mensaje", e);
						}
						catch (ArticuloNotFoundException e) {
							throw e;
						}
						finally {
							sqlSession.close();
						}

						// comprobamos la configuración de la tarifa
						if (tarifaArticuloDto != null && tarifaArticuloDto.getCabecera() != null) {
							boolean isPrecioConImpuestos = tarifaArticuloDto.getCabecera().getPrecioConImpuestos() != null && tarifaArticuloDto.getCabecera().getPrecioConImpuestos().equals("S");

							if (!isPrecioConImpuestos) {
								SesionImpuestos sesionImpuestos = sesion.getImpuestos();
								precio = sesionImpuestos.getPrecioSinImpuestos(articuloAux.getCodImpuesto(), precio, ((TicketVentaAbono) ticketPrincipal).getCliente().getIdTratImpuestos());
							}
						}

						// fin personalizacion cardoso
					}
					else {
						precio = null;
					}

					if (codArticulo == null) {
						log.error(String.format("nuevaLineaArticulo() - El código de barra especial obtenido no es válido. CodArticulo: %s, cantidad: %s, precio: %s", codArticulo, cantidad, precio));
						throw new LineaTicketException(I18N.getTexto("Error procesando el código de barras. Revise configuración."));
					}
				}
			}
			catch (LineaTicketException e) {
				throw e;
			}
			catch (Exception e) {
				log.error("Error procesando el código de barras especial : " + codArticulo, e);
				throw new LineaTicketException(I18N.getTexto("Error procesando el código de barras. Revise configuración."));
			}

			try {
				linea = lineasTicketServices.createLineaArticulo((TicketVenta) ticketPrincipal, codArticulo, desglose1, desglose2, cantidad, precio, createLinea(), applyDUN14Factor);
				linea.setCantidad(tratarSignoCantidad(linea.getCantidad(), linea.getCabecera().getCodTipoDocumento()));
				if (esLineaDevolucionPositiva) {
					linea.setCantidad(linea.getCantidad().abs());
				}

				if (codBarras != null) {
					linea.setCodigoBarras(codBarras);
				}

				if (pesarArticulo && StringUtils.isNotBlank(linea.getArticulo().getBalanzaTipoArticulo()) && linea.getArticulo().getBalanzaTipoArticulo().trim().toUpperCase().equals(PESAR_ARTICULO)) {
					IBalanza balanza = Dispositivos.getInstance().getBalanza();
					if (!(balanza instanceof BalanzaNoConfig)) {
						HashMap<String, Object> params = new HashMap<String, Object>();
						POSApplication.getInstance().getMainView().showModalCentered(SolicitarPesoArticuloView.class, params, stage);
						if (params.containsKey(SolicitarPesoArticuloController.PARAM_PESO)) {
							BigDecimal peso = (BigDecimal) params.get(SolicitarPesoArticuloController.PARAM_PESO);
							if (peso == null || BigDecimalUtil.isMenorOrIgualACero(peso)) {
								throw new LineaTicketException(I18N.getTexto("No se ha podido pesar el artículo, compruebe la configuración de la balanza."));
							}
							linea.setCantidad(peso);
						}
						else {
							throw new LineaTicketException(I18N.getTexto("Este artículo no puede ser introducido sin ser pesado previamente."));
						}
					}
				}

				if (esDevolucion && ticketOrigen != null && !esLineaDevolucionPositiva) {
					// GAP - DEVOLUCIÓN SIN DOCUMENTO ORIGEN
					if (UID_TICKET_FALSO.equalsIgnoreCase(ticketOrigen.getUidTicket())) {
						linea.setLineaDocumentoOrigen(1);
					}
					else {
						if (idLineaDocOrigen == null) {
							idLineaDocOrigen = getIdLineaTicketOrigen(linea.getCodArticulo(), linea.getDesglose1(), linea.getDesglose2(), linea.getCantidad().abs());
						}
						LineaTicketAbstract lineaOrigen = ticketOrigen.getLinea(idLineaDocOrigen);
						lineaOrigen.setPrecioTotalConDto(lineaOrigen.getImporteTotalConDto().setScale(6, BigDecimal.ROUND_HALF_UP)
						        .divide(lineaOrigen.getCantidad().setScale(6, BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP));
						linea.resetPromociones();
						linea.setPrecioSinDto(lineaOrigen.getPrecioConDto());
						linea.setPrecioTotalSinDto(lineaOrigen.getPrecioTotalConDto());
						linea.recalcularImporteFinal();
						linea.setLineaDocumentoOrigen(lineaOrigen.getIdLinea());

						// Se deja comentado por si en un futuro en las devoluciones se quiere devolver el descuentoq ue
						// se hizo de GEA
						// // CAR-44 Ponemos a negativo (contemplando dev positiva y negativa)
						// // el importe de descuento de la linea, para compensar el acumulado del fidelizado
						// if(((CARDOSOLineaTicket) lineaOrigen).getDatosDescuentoGea() != null) {
						// ((CARDOSOLineaTicket)linea).setDatosDescuentoGea(((CARDOSOLineaTicket)
						// lineaOrigen).getDatosDescuentoGea());
						// BigDecimal importeDescuentoLinea;
						// if(BigDecimalUtil.isMenorACero(linea.getCantidad())) {
						// importeDescuentoLinea =
						// ((CARDOSOLineaTicket)linea).getDatosDescuentoGea().getImporteDescuentoLinea();
						// } else {
						// importeDescuentoLinea =
						// ((CARDOSOLineaTicket)linea).getDatosDescuentoGea().getImporteDescuentoLinea().negate();
						// }
						// //Se divide entre la cantidad original y se multiplica por la cantidad a devolver para sacar
						// el importe acumulado a negar
						// ((CARDOSOLineaTicket)linea).getDatosDescuentoGea().setImporteDescuentoLinea(importeDescuentoLinea.divide(lineaOrigen.getCantidad()).multiply(linea.getCantidad()));
						// }

						actualizarCantidadesOrigenADevolver(lineaOrigen, lineaOrigen.getCantidadADevolver().add(linea.getCantidad().abs()));
					}
				}

				addLinea(linea);
				ticketPrincipal.getTotales().recalcular();

			}
			catch (ArticuloNotFoundException e) {
				linea = null;
				try {
					CustomerCouponDTO coupon = new CustomerCouponDTO(codArticulo, false);
					isCupon = sesionPromociones.aplicarCupon(coupon, (TicketVentaAbono) ticketPrincipal);
					if (!isCupon) {
						log.warn("nuevaLineaArticulo() - Artículo no encontrado " + codArticulo);
						throw new LineaTicketException(e.getMessageI18N());
					}
					ticketPrincipal.getTotales().recalcular();
				}
				catch (CuponAplicationException | CuponUseException | CuponesServiceException ex) {
					log.warn("nuevaLineaArticulo() - Error en la aplicación del cupón -" + ex.getMessageI18N());
					throw new LineaTicketException(ex.getMessageI18N(), e);
				}
			}
		}
		return (LineaTicket) linea;
	}

	protected LineaTicket evaluarPromocionBarrasEspecial(String sCodart, String sDesglose1, String sDesglose2) {
		try {
			ITicket ticketAux = SpringContext.getBean(getTicketClass(documentoActivo));
			ticketAux.getCabecera().inicializarCabecera(ticketPrincipal);
			((TicketVentaAbono) ticketAux).inicializarTotales();
			ticketAux.setCliente(sesion.getAplicacion().getTienda().getCliente().clone());
			ticketAux.setCajero(sesion.getSesionUsuario().getUsuario());
			ticketAux.getCabecera().getTotales().setCambio(SpringContext.getBean(PagoTicket.class, MediosPagosService.medioPagoDefecto));
			ticketAux.getTotales().recalcular();

			// Establecemos los parámetros de tipo de documento del ticket
			ticketAux.getCabecera().setDocumento(documentoActivo);

			// Asignamos el cliente
			ticketAux.setCliente(ticketPrincipal.getCliente());
			ticketAux.getCabecera().setDatosFidelizado(ticketPrincipal.getCabecera().getDatosFidelizado());

			// Insertamos el articulo en el ticket con cantidad 10 para las promociones de NxM
			// nuevaLineaArticulo(sCodart, sDesglose1, sDesglose2, new BigDecimal(10), null);
			try {
				// ticketAux.getLineas().add(lineasTicketServices.createLineaArticulo((TicketVenta) ticketAux, sCodart,
				// sDesglose1, sDesglose2, new BigDecimal(10), null, createLinea()));

				CARDOSOLineaTicket lineaAux = (CARDOSOLineaTicket) lineasTicketServices.createLineaArticulo((TicketVenta) ticketAux, sCodart, sDesglose1, sDesglose2, new BigDecimal(10), null,
				        createLinea());

				if (!esDevolucion) {
					// Buscamos la promoción aplicable a esta línea y la guardamos en la línea
					try {
						// Se devuelve un array con dos posiciones. La primera posicion corresponde a la promoción de
						// Dto y la segunda a la aplicable. La primera posición solo será distinta de null si la segunda
						// promoción es de NxM
						PromocionCandidataBean[] promocionesAplicables = cardosoPromocionesService.consultarPromocionAplicableLinea((LineaTicket) lineaAux,
						        ticketAux.getCabecera().getDatosFidelizado() != null);

						tratarPromocionesAplicables(lineaAux, promocionesAplicables);

						Long idPromoDto = null;
						Long idPromoAplicable = null;

						if (promocionesAplicables[0] != null) {
							idPromoDto = promocionesAplicables[0].getIdPromocion();
						}
						if (promocionesAplicables[1] != null) {
							idPromoAplicable = promocionesAplicables[1].getIdPromocion();
						}

						lineaAux.setIdPromocionDtoAplicable(idPromoDto);
						lineaAux.setIdPromocionAplicable(idPromoAplicable);
					}
					catch (PromocionesServiceException e) {
						log.error("nuevaLineaArticulo() - Ha habido un error al consultar la promoción aplicable de la línea: " + e.getMessage(), e);
						// throw new LineaTicketException(I18N.getTexto("Ha habido un error al insertar la línea."), e);
					}
				}

				ticketAux.getLineas().add(lineaAux);
			}
			catch (ArticuloNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// recalcularConPromociones();
			sesionPromociones.aplicarPromociones((TicketVentaAbono) ticketAux);
			ticketAux.getTotales().recalcular();

			return (((LineaTicket) ticketAux.getLineas().get(0)));
		}
		catch (LineaTicketException e) {
			LineaTicketException ex = new LineaTicketException(I18N.getTexto("Error insertando línea."), e);
			log.error("evaluarPromocionBarrasEspecial() - Error insertando línea", ex);
		}
		return null;
	}

	private void tratarPromocionesAplicables(CARDOSOLineaTicket lineaTicket, PromocionCandidataBean[] promocionesAplicables) {

		String codArticulo = ((LineaTicket) lineaTicket).getCodArticulo();
		String desglose1 = ((LineaTicket) lineaTicket).getDesglose1();
		String desglose2 = ((LineaTicket) lineaTicket).getDesglose2();

		PromocionCandidataBean promoDto = null;
		PromocionCandidataBean promoAplicable = null;

		if (promocionesAplicables[0] != null) {
			promoDto = promocionesAplicables[0];
		}
		if (promocionesAplicables[1] != null) {
			promoAplicable = promocionesAplicables[1];
		}

		// Venta normal
		if (lineaTicket instanceof LineaTicket) {
			// Si existe una promoción de Descuento y otra de NxM. Modificamos el precio de venta de la línea por el
			// precio de tarifa de la NxM con el dto de la otra promoción aplicado
			if (promoDto != null && promoAplicable != null) {

				lineaTicket.setIdPromocionAplicable(promoAplicable.getIdPromocion());
				lineaTicket.setIdPromocionDtoAplicable(promoDto.getIdPromocion());

				BigDecimal dto = ((PromocionDescuentoDetalles) ((CARDOSOSesionPromociones) sesionPromociones).getPromocionActiva(promoDto.getIdPromocion())).getDescuento(codArticulo, desglose1,
				        desglose2);

				trataDtoMasTarifa(lineaTicket, promoAplicable.getPrecioTarifa(), dto);

			}
			else if (promoDto == null && promoAplicable != null) { // Si solo se ha seleccionado una promoción
			                                                       // comprobamos si tenemos que hacer algo

				lineaTicket.setIdPromocionAplicable(promoAplicable.getIdPromocion());

				if (promoAplicable.getIdTipoPromocion().equals(2)) { // Promo de NxM

					BigDecimal dto = lineaTicket.getDescuentoTarifa();

					trataDtoMasTarifa(lineaTicket, promoAplicable.getPrecioTarifa(), dto);

				}
				else if (promoAplicable.getIdTipoPromocion().equals(3)) { // Promo de Descuento

					BigDecimal dto = ((PromocionDescuentoDetalles) ((CARDOSOSesionPromociones) sesionPromociones).getPromocionActiva(promoAplicable.getIdPromocion())).getDescuento(codArticulo,
					        desglose1, desglose2);
					BigDecimal precioVentaSinDto = ((CARDOSOLineaTicket) lineaTicket).getPrecioVentaSinDtoTarifa();

					trataDto(lineaTicket, precioVentaSinDto, dto);
					lineaTicket.setIdPromocionAplicable(null);
				}
			}
		}
	}

	private void trataDtoMasTarifa(CARDOSOLineaTicket lineaTicket, BigDecimal precioTarifa, BigDecimal dto) {
		if (dto != null && precioTarifa != null) {
			String codImpuestos = ((LineaTicket) lineaTicket).getCodImpuesto();
			Long idTratImp = ((LineaTicket) lineaTicket).getCabecera().getCliente().getIdTratImpuestos();
			BigDecimal precioImpuestos = sesion.getImpuestos().getImpuestos(codImpuestos, idTratImp, precioTarifa);

			BigDecimal precioTotalTarifaPromo = BigDecimalUtil.redondear(precioTarifa.add(precioImpuestos));
			BigDecimal precioTotalTarifaPromoConDto = BigDecimalUtil.redondear(precioTotalTarifaPromo.subtract(BigDecimalUtil.porcentaje(precioTotalTarifaPromo, dto)));

			((LineaTicket) lineaTicket).setPrecioSinDto(precioTarifa);
			((LineaTicket) lineaTicket).setPrecioTotalSinDto(precioTotalTarifaPromoConDto);
			((LineaTicket) lineaTicket).setPrecioTarifaOrigen(precioTarifa);
			((LineaTicket) lineaTicket).setPrecioTotalTarifaOrigen(((LineaTicket) lineaTicket).getPrecioTotalSinDto());
			((LineaTicket) lineaTicket).recalcularImporteFinal();
			lineaTicket.setDescuentoTarifa(dto);
		}
	}

	private void trataDto(CARDOSOLineaTicket lineaTicket, BigDecimal precioTarifa, BigDecimal dto) {
		if (dto != null && precioTarifa != null) {
			String codImpuestos = ((LineaTicket) lineaTicket).getCodImpuesto();
			Long idTratImp = ((LineaTicket) lineaTicket).getCabecera().getCliente().getIdTratImpuestos();
			BigDecimal precioImpuestos = sesion.getImpuestos().getImpuestos(codImpuestos, idTratImp, precioTarifa);

			BigDecimal precioTotalTarifaPromo = BigDecimalUtil.redondear(precioTarifa.add(precioImpuestos));

			((LineaTicket) lineaTicket).setPrecioSinDto(precioTarifa);
			((LineaTicket) lineaTicket).setPrecioTotalSinDto(precioTotalTarifaPromo);
			((LineaTicket) lineaTicket).setDescuentoManual(dto);
			((LineaTicket) lineaTicket).setPrecioTarifaOrigen(precioTarifa);
			((LineaTicket) lineaTicket).setPrecioTotalTarifaOrigen(((LineaTicket) lineaTicket).getPrecioTotalSinDto());
			((LineaTicket) lineaTicket).recalcularImporteFinal();
			lineaTicket.setDescuentoTarifa(dto);
			lineaTicket.setImporteDescuentoTarifa(((LineaTicket) lineaTicket).getPrecioTotalSinDto().subtract(((LineaTicket) lineaTicket).getPrecioTotalConDto()));
		}
	}

	/**
	 * ######################################################################################## GAP - PERSONALIZACIONES
	 * V3 - PROMOCIÓN EMPLEADO Registro o anulación de usos de la promoción
	 */

	@Override
	public void salvarTicketSeguridad(Stage stage, SalvarTicketCallback callback) {
		new CardosoSalvarTicketSeguridadTask(stage, callback).start();
	}

	public class CardosoSalvarTicketSeguridadTask extends BackgroundTask<Void> {

		protected Stage stage;
		protected SalvarTicketCallback callback;

		public CardosoSalvarTicketSeguridadTask(Stage stage, SalvarTicketCallback callback) {
			this.stage = stage;
			this.callback = callback;
		}

		@Override
		protected Void call() throws Exception {
			sesionPromociones.aplicarPromocionesFinales((TicketVentaAbono) ticketPrincipal);

			sesionPromociones.generarCuponesDtoFuturo(ticketPrincipal);

			ticketPrincipal.getTotales().recalcular();

			if (ticketPrincipal.getIdTicket() == null) {
				ticketsService.setContadorIdTicket((Ticket) ticketPrincipal);
			}
			guardarCopiaSeguridadTicket();

			log.debug("SalvarTicketTask() - Contador obtenido: idTicket = " + ticketPrincipal.getIdTicket());

			return null;
		}

		@Override
		protected void succeeded() {
			super.succeeded();

			// Generamos el uso de la promoción de empleado en caso de haberla
			generarUsoPromocionEmpleado();

			// Generamos los DatosPeticionTarjeta
			List<DatosPeticionPagoTarjeta> datosPeticionTarjeta = new LinkedList<>();
			// Añadimos todos los pagos
			final List<IPagoTicket> pagosTarjeta = new LinkedList<>(ticketPrincipal.getPagos());
			for (ListIterator<IPagoTicket> iterator = pagosTarjeta.listIterator(); iterator.hasNext();) {
				IPagoTicket pagoTicket = iterator.next();
				// Quitamos los pagos que no acepta el dispositivo
				if (!Dispositivos.getInstance().getTarjeta().isCodMedPagoAceptado(pagoTicket.getCodMedioPago())) {
					iterator.remove();
					continue;
				}
				ConexFlowDatosPeticionPagoTarjeta datoPeticion = new ConexFlowDatosPeticionPagoTarjeta(ticketPrincipal.getCabecera().getCodTicket(), ticketPrincipal.getCabecera().getIdTicket(),
				        pagoTicket.getImporte()); // TODO ??
				datosPeticionTarjeta.add(datoPeticion);
				if (ticketOrigen != null) {
					// Asociamos los datos respuesta de los pagos origen
					List<IPagoTicket> pagosTarjetaOrig = new LinkedList<>(ticketOrigen.getPagos());
					for (IPagoTicket pagoTicketOrigen : pagosTarjetaOrig) {
						if (pagoTicket.getCodMedioPago().equals(pagoTicketOrigen.getCodMedioPago())) {
							DatosRespuestaPagoTarjeta datosRespuestaPagoTarjeta = pagoTicketOrigen.getDatosRespuestaPagoTarjeta();
							if (datosRespuestaPagoTarjeta != null) {
								datoPeticion.setCodAutorizacion(datosRespuestaPagoTarjeta.getCodAutorizacion());
								datoPeticion.setNumOpBanco(datosRespuestaPagoTarjeta.getNumOperacionBanco());
								datoPeticion.setNumOperacion(datosRespuestaPagoTarjeta.getNumOperacion());
								datoPeticion.setIdDocumentoOrigen(datosRespuestaPagoTarjeta.getNumTransaccion());
								datoPeticion.setFechaDocumentoOrigen(datosRespuestaPagoTarjeta.getFechaTransaccion());

								for (String clave : datosRespuestaPagoTarjeta.getAdicionales().keySet()) {
									datoPeticion.addAdiccional(clave, datosRespuestaPagoTarjeta.getAdicionales().get(clave));
								}

								// Parche para pasar los datos necesarios para devoluciones referenciadas en ConexFlow
								datoPeticion.setEmpleado(datosRespuestaPagoTarjeta.getCodigoCentro());
								datoPeticion.setNumOpBanco(datosRespuestaPagoTarjeta.getPos());
								// Parche para pasar los datos necesarios para devoluciones referenciadas en ConexFlow
							}
						}
					}
				}
			}

			TarjetaCallback<List<DatosRespuestaPagoTarjeta>> dispositivoCallback = new TarjetaCallback<List<DatosRespuestaPagoTarjeta>>(){

				@Override
				public void onSuccess(List<DatosRespuestaPagoTarjeta> datosRespuesta) {
					// Asignamos los datos respuesta a los pagos
					for (DatosRespuestaPagoTarjeta datoRespuesta : datosRespuesta) {
						pagosTarjeta.get(datosRespuesta.indexOf(datoRespuesta)).setDatosRespuestaPagoTarjeta(datoRespuesta);
					}
					crearClaseRegistrarTicketTask(stage, callback, datosRespuesta).start();
				}

				@Override
				public void onFailure(List<DatosRespuestaPagoTarjeta> datosRespuesta, Throwable caught) {
					anularPagos(datosRespuesta, stage);
					// TODO ?? anularMovimientoTarjetaRegalo(stage);

					salvarTicketVacio();

					callback.onFailure((Exception) caught);
				}
			};

			boolean venta = (!ticketPrincipal.getCabecera().getCodTipoDocumento().equals("NC")) && BigDecimalUtil.isMayorOrIgualACero(ticketPrincipal.getTotales().getTotal());
			if (venta) {
				Dispositivos.getInstance().getTarjeta().solicitarPagosTarjeta(datosPeticionTarjeta, stage, dispositivoCallback);
			}
			else {
				Dispositivos.getInstance().getTarjeta().solicitarDevolucion(datosPeticionTarjeta, stage, dispositivoCallback);
			}
		}

		@Override
		protected void failed() {
			super.failed();
			Throwable ex = getException();
			if (ticketPrincipal.getIdTicket() != null) {
				salvarTicketVacio();
			}

			log.info("Se anula el uso de la promoción");
			anularUsoPromocionEmpleado();

			callback.onFailure((Exception) ex);
		}

	}

	private void anularUsoPromocionEmpleado() {
		// Hacemos la llamada al REST, si hay alguna excepción no hacemos nada
		if (((CARDOSOCabeceraTicket) ticketPrincipal.getCabecera()).getDatosDescuentoPromocionEmpleados() != null
		        && !((CARDOSOCabeceraTicket) ticketPrincipal.getCabecera()).getDatosDescuentoPromocionEmpleados().getIdPromocion().equals(new Long(-1))) {
			String uidTransaccion = ticketPrincipal.getUidTicket();
			new AnulacionUsoPromocionEmpleadoTask(uidTransaccion).start();
		}
	}

	private void generarUsoPromocionEmpleado() {
		if (((CARDOSOCabeceraTicket) ticketPrincipal.getCabecera()).getDatosDescuentoPromocionEmpleados() != null
		        && !((CARDOSOCabeceraTicket) ticketPrincipal.getCabecera()).getDatosDescuentoPromocionEmpleados().getIdPromocion().equals(new Long(-1))) {
			// Añadimos el uid de transacción al ticket
			String uidTransaccion = ticketPrincipal.getUidTicket();
			((CARDOSOCabeceraTicket) ticketPrincipal.getCabecera()).getDatosDescuentoPromocionEmpleados().setUidTransaccion(uidTransaccion);
			PromocionEmpleadosCabeceraTicket datosDescuento = ((CARDOSOCabeceraTicket) ticketPrincipal.getCabecera()).getDatosDescuentoPromocionEmpleados();

			// Hacemos la llamada al REST, si hay alguna excepción no hacemos nada
			new UsoPromocionEmpleadoTask(datosDescuento, ticketPrincipal.getCabecera().getDatosFidelizado().getNumTarjetaFidelizado(), ticketPrincipal.getTotales().getTotal()).start();
		}
	}

	public class UsoPromocionEmpleadoTask extends BackgroundTask<Void> {

		private PromocionEmpleadosCabeceraTicket datosDescuento;
		private String numeroTarjeta;
		private BigDecimal total;

		public UsoPromocionEmpleadoTask(PromocionEmpleadosCabeceraTicket datosDescuento, String numeroTarjeta, BigDecimal total) {
			super();
			this.datosDescuento = datosDescuento;
			this.numeroTarjeta = numeroTarjeta;
			this.total = total;
		}

		@Override
		protected Void call() throws Exception {
			log.debug("UsoPromocionEmpleadoTask - Realizando operación de generación de uso");
			try {
				String apiKey = variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
				String uidActividad = sesion.getAplicacion().getUidActividad();
				PromoCuentaBean promo = new PromoCuentaBean();
				promo.setIdPromocion(datosDescuento.getIdPromocion());
				promo.setNumeroTarjeta(numeroTarjeta);
				promo.setUidTransaccion(datosDescuento.getUidTransaccion());
				promo.setReferenciaUso(datosDescuento.getUidTransaccion());
				promo.setFechaUso(new Date());
				promo.setImporteUso(datosDescuento.getImporteTotalAhorro());
				promo.setImporteTotal(total);

				CARDOSOClientRestPromocionEmpleados.generarUso(posRestService.getUrlApiV1(), apiKey, uidActividad, promo.getIdPromocion(), promo.getNumeroTarjeta(), promo.getUidTransaccion(),
				        promo.getReferenciaUso(), promo.getFechaUso(), promo.getImporteUso(), promo.getImporteTotal());
			}
			catch (Exception e) {
				log.error("UsoPromocionEmpleadoTask() - Ha habido un error al realizar la conexión al REST: " + e.getMessage());
			}
			return null;
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			log.info("succeeded() - Se ha generado el uso de la promoción de empleados sin errores");

		}

		@Override
		protected void failed() {
			super.failed();
			log.info("failed() - Error al generar uso de la promoción de empleados");
		}

	}

	public class AnulacionUsoPromocionEmpleadoTask extends BackgroundTask<Void> {

		private String uidTransaccion;

		public AnulacionUsoPromocionEmpleadoTask(String uidTransaccion) {
			super();
			this.uidTransaccion = uidTransaccion;
		}

		@Override
		protected Void call() throws Exception {
			log.debug("AnulacionUsoPromocionEmpleadoTask - Realizando operación de anulación de uso");
			PromocionEmpleadosCabeceraTicket datosDescuento = ((CARDOSOCabeceraTicket) ticketPrincipal.getCabecera()).getDatosDescuentoPromocionEmpleados();
			AnulacionPromocionEmpleadoBean anulacion = new AnulacionPromocionEmpleadoBean();
			anulacion.setIdPromocion(datosDescuento.getIdPromocion());
			anulacion.setNumTarjeta(ticketPrincipal.getCabecera().getDatosFidelizado().getNumTarjetaFidelizado());
			anulacion.setUidTransaccion(uidTransaccion);
			anulacion.setReferenciaUso(uidTransaccion);
			anulacion.setImporteUso(datosDescuento.getDescuento());
			anulacion.setImporteTotal(ticketPrincipal.getTotales().getTotal());

			try {
				servicioAnulacionPromocionEmpleado.enviar(anulacion);
			}
			catch (Exception e) {
				log.error("AnulacionUsoPromocionEmpleadoTask() - Ha habido un error al realizar la conexión al REST: " + e.getMessage());
				servicioAnulacionPromocionEmpleado.crearDocumento(anulacion);
			}
			return null;
		}

	}

	@Override
	protected BackgroundTask<Void> crearClaseRegistrarTicketTask(Stage stage, SalvarTicketCallback callback, List<DatosRespuestaPagoTarjeta> datosRespuesta) {
		return new CardosoRegistrarTicketTask(stage, callback, datosRespuesta);
	}

	protected class CardosoRegistrarTicketTask extends BackgroundTask<Void> {

		protected Stage stage;
		protected SalvarTicketCallback callback;
		protected List<DatosRespuestaPagoTarjeta> pagosAutorizados;

		public CardosoRegistrarTicketTask(Stage stage, SalvarTicketCallback callback, List<DatosRespuestaPagoTarjeta> pagosAutorizados) {
			this.stage = stage;
			this.callback = callback;
			this.pagosAutorizados = pagosAutorizados;
		}

		@Override
		protected Void call() throws Exception {
			if (esOperacionTarjetaRegalo) {
				procesarTarjetaRegalo(stage);
			}

			redondearImportesTicket();

			boolean processTicket = esDevolucion || esFacturacionVentaCredito || !ticketPrincipal.getCuponesAplicados().isEmpty();

			ticketsService.registrarTicket((Ticket) ticketPrincipal, documentoActivo, processTicket);

			confirmarPagosTarjeta(pagosAutorizados, stage);

			return null;
		}

		@Override
		protected void failed() {
			super.failed();
			Exception ex = (Exception) getException();

			log.error("salvarTicket() Error salvando ticket : " + ex.getMessage(), ex);

			anularPagos(pagosAutorizados, stage);
			anularUsoPromocionEmpleado();
			anularPromocionesFinales(stage);

			if (ticketPrincipal.getIdTicket() != null) {
				salvarTicketVacio();
			}

			callback.onFailure(ex);

		}

		@Override
		protected void succeeded() {
			super.succeeded();
			callback.onSucceeded();
		}
	}

	@Override
	public boolean tratarTicketRecuperado(byte[] ticketRecuperado) throws TicketsServiceException {

		boolean ticketTratado = super.tratarTicketRecuperado(ticketRecuperado);

		return ticketTratado;

	}

	@Override
	public void recuperarTicket(Stage stage, TicketAparcadoBean ticketAparcado) throws TicketsServiceException, PromocionesServiceException, DocumentoException, LineaTicketException {
		log.debug("recuperarTicket() - Recuperando ticket...");

		nuevoTicket();
		// Realizamos el unmarshall
		log.debug("Ticket recuperado:\n" + new String(ticketAparcado.getTicket()));
		TicketVenta ticketRecuperado = (TicketVentaAbono) MarshallUtil.leerXML(ticketAparcado.getTicket(), getTicketClasses(documentoActivo).toArray(new Class[] {}));

		ticketPrincipal.getCabecera().setIdTicket(ticketRecuperado.getIdTicket());
		ticketPrincipal.getCabecera().setUidTicket(ticketRecuperado.getUidTicket());
		ticketPrincipal.getCabecera().setCodTicket(ticketRecuperado.getCabecera().getCodTicket());
		ticketPrincipal.getCabecera().setSerieTicket(ticketRecuperado.getCabecera().getSerieTicket());

		String tipoDocumentoFacturaDirecta = getDocumentoActivo().getTipoDocumentoFacturaDirecta();
		if (ticketRecuperado.getCabecera().getCodTipoDocumento().equals(tipoDocumentoFacturaDirecta)) {
			setDocumentoActivo(sesion.getAplicacion().getDocumentos().getDocumento(tipoDocumentoFacturaDirecta));
		}

		if (ticketAparcado.getUsuario() == null || !ticketAparcado.getUsuario().equals("FASTPOS")) {
			// Recuperamos el cliente del ticket aparcado
			ticketPrincipal.getCabecera().setCliente(ticketRecuperado.getCabecera().getCliente());
		}
		String uidDiarioCaja = sesion.getSesionCaja().getUidDiarioCaja();
		ticketPrincipal.getCabecera().setUidDiarioCaja(uidDiarioCaja);

		recuperarDatosPersonalizados(ticketRecuperado);

		List<LineaTicket> lineas = ticketRecuperado.getLineas();
		for (LineaTicket lineaRecuperada : lineas) {
			String codigo = lineaRecuperada.getCodigoBarras();
			String desglose1 = lineaRecuperada.getDesglose1();
			String desglose2 = lineaRecuperada.getDesglose2();
			if (StringUtils.isBlank(codigo)) {
				codigo = lineaRecuperada.getCodArticulo();
			}
			else {
				desglose1 = null;
				desglose2 = null;
			}
			LineaTicket nuevaLineaArticulo = nuevaLineaArticuloCardoso(codigo, desglose1, desglose2, lineaRecuperada.getCantidad(), null, null, false, false);

			nuevaLineaArticulo.setDocumentoOrigen(lineaRecuperada.getDocumentoOrigen());

			nuevaLineaArticulo.setDesArticulo(lineaRecuperada.getDesArticulo());
			nuevaLineaArticulo.setDescuentoManual(lineaRecuperada.getDescuentoManual());
			BigDecimal nuevoPrecio = lineaRecuperada.getPrecioTotalSinDto();
			nuevaLineaArticulo.setPrecioTotalSinDto(nuevoPrecio);
			BigDecimal precioSinDto = lineaRecuperada.getPrecioSinDto();
			nuevaLineaArticulo.setPrecioSinDto(precioSinDto);
			nuevaLineaArticulo.setCodigoBarras(lineaRecuperada.getCodigoBarras());
			nuevaLineaArticulo.setNumerosSerie(lineaRecuperada.getNumerosSerie());
			nuevaLineaArticulo.setEditable(lineaRecuperada.isEditable());

			String sellerName = lineaRecuperada.getVendedor().getUsuario();
			try {
				UsuarioBean seller = usuariosService.consultarUsuario(sellerName);
				nuevaLineaArticulo.setVendedor(seller);
			}
			catch (UsuarioNotFoundException e) {
				// active user
				log.warn("recuperarTicket() - No se ha encontrado el usuario: " + sellerName);
			}
			catch (UsuariosServiceException e) {
				// active user
				log.warn("recuperarTicket() - Se ha producido un error al consultar el: " + sellerName);
			}
			recuperarDatosPersonalizadosLinea(lineaRecuperada, nuevaLineaArticulo);
		}

		FidelizacionBean datosFidelizado = ticketRecuperado.getCabecera().getDatosFidelizado();
		if (datosFidelizado != null) {
			try {
				FidelizacionBean tarjetaFidelizado = Dispositivos.getInstance().getFidelizacion().consultarTarjetaFidelizado(stage, datosFidelizado.getNumTarjetaFidelizado(),
				        ticketPrincipal.getCabecera().getUidActividad());
				ticketPrincipal.getCabecera().setDatosFidelizado(tarjetaFidelizado);
			}
			catch (ConsultaTarjetaFidelizadoException e) {
				log.debug("recuperarTicket() - Error al consultar fidelizado", e);
				FidelizacionBean fidelizacionBean = new FidelizacionBean();
				fidelizacionBean.setNumTarjetaFidelizado(datosFidelizado.getNumTarjetaFidelizado());
				ticketPrincipal.getCabecera().setDatosFidelizado(fidelizacionBean);
			}
		}

		for (PagoTicket pago : (List<PagoTicket>) ticketRecuperado.getPagos()) {
			pago.setMedioPago(mediosPagosService.getMedioPago(pago.getCodMedioPago()));
			ticketPrincipal.getPagos().add(pago);
		}

		recalcularConPromociones();

		// Establecemos el contador
		contadorLinea = ticketPrincipal.getLineas().size() + 1;
		// Eliminamos el ticket recuperado de la lista de tickets aparcados.
		ticketsAparcadosService.eliminarTicket(ticketAparcado.getUidTicket());
	}
}
