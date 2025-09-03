package com.comerzzia.iskaypet.pos.gui.ventas.devoluciones;

import com.comerzzia.core.model.clases.parametros.valoresobjetos.ValorParametroObjeto;
import com.comerzzia.core.servicios.clases.parametros.valoresobjeto.ValoresParametrosObjetosService;
import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.iskaypet.pos.devices.proformas.seleccion.SeleccionProformaController;
import com.comerzzia.iskaypet.pos.devices.proformas.seleccion.SeleccionProformaView;
import com.comerzzia.iskaypet.pos.gui.autorizacion.AutorizacionGerenteUtils;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.IskaypetTicketManager;
import com.comerzzia.iskaypet.pos.services.proformas.rest.ProformaRestService;
import com.comerzzia.iskaypet.pos.services.proformas.rest.classes.ProformaDTO;
import com.comerzzia.iskaypet.pos.services.proformas.rest.classes.ProformaHeaderDTO;
import com.comerzzia.iskaypet.pos.util.formatter.IskaypetFormatter;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.dispositivo.fidelizacion.busqueda.BusquedaFidelizadoView;
import com.comerzzia.pos.gui.ventas.devoluciones.DevolucionesController;
import com.comerzzia.pos.gui.ventas.devoluciones.IntroduccionArticulosView;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoBean;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoMapper;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.aparcados.TicketsAparcadosService;
import com.comerzzia.pos.services.ticket.copiaSeguridad.CopiaSeguridadTicketService;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;
import com.ibm.icu.util.Calendar;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.comerzzia.iskaypet.pos.devices.fidelizacion.busqueda.IskaypetBusquedaFidelizadoController.PARAMETRO_MOSTRAR_ALTA;
import static com.comerzzia.iskaypet.pos.devices.fidelizacion.gestion.GestionTicketsFidelizadoController.PARAMETRO_LOCALIZADOR_DEVOLUCION;
import static com.comerzzia.iskaypet.pos.devices.proformas.seleccion.SeleccionProformaController.PARAM_ACCION_CANCELAR;
import static com.comerzzia.iskaypet.pos.devices.proformas.seleccion.SeleccionProformaController.PARAM_PROFORMA_SELECCIONADA;

@Component
@Primary
public class IskaypetDevolucionesController extends DevolucionesController {

	protected Logger log = Logger.getLogger(getClass());

	 @Autowired
	 private Sesion sesion;

	@FXML
	protected Button btBuscarFid;

	@FXML
	protected Button btBuscarProformas;

	@Autowired
	protected VariablesServices variablesServices;

	@Autowired
	protected TicketsAparcadosService ticketsAparcadosService;

	@Autowired
	private CopiaSeguridadTicketService copiaSeguridadTicketService;

	@Autowired
	protected TicketAparcadoMapper ticketAparcadoMapper;

	@Autowired
	private ProformaRestService proformaRestService;

	@Autowired
	protected ValoresParametrosObjetosService valoresParametrosObjetosService;

	public static final String PARAMETRO_TARJETA_FIDELIZADO = "PARAMETRO_TARJETA_FIDELIZADO";
	public static final String PARAMETRO_DEVOLUCION_FIDELIZADO = "PARAMETRO_DEVOLUCION_FIDELIZADO";
	public static final String PARAMETRO_TICKET_FIDELIZADO = "PARAMETRO_TICKET_FIDELIZADO";
	public static final String ES_DEVOLUCION = "ES_DEVOLUCION";
	public static final String DEVOLUCIONES_PLAZO_MAXIMO = "X_DEVOLUCIONES.PLAZO_MAXIMO";
	public static final String TIPO_ESTABLECIMIENTO_CLINICA = "CLINICA";

	@Override
	public void initializeComponents() {
		IskaypetFormatter.setUpperCaseFormatter(tfOperacion);
		super.initializeComponents();
	}

	@Override
	protected void recuperarTicketDevolucionSucceeded(boolean encontrado) {
		log.debug("recuperarTicketDevolucionSucceeded()");

		// ISK-182 GAP-63 - DEVOLUCIÓN SIN DOCUMENTO ORIGEN
		boolean esDevolucionSinOrigen = false;
		if (!encontrado && ((IskaypetTicketManager) ticketManager).getRealizarDevolucionSinDocumento()) {

			TipoDocumentoBean documento = getTipoDocumentoDevolucionActual();
			if (documento != null && documento.getCodtipodocumento().equals(Documentos.FACTURA_COMPLETA)) {
				String msgError = I18N.getTexto("No se ha encontrado el documento de origen y no se permite realizar devoluciones libres de factura completa");
				VentanaDialogoComponent.crearVentanaAviso(msgError, this.getStage());
				return;
			}

			String msgError = I18N.getTexto("No se ha encontrado el documento de origen, se va a acceder a una DEVOLUCIÓN LIBRE. ¿Está seguro de continuar?");
			if (!VentanaDialogoComponent.crearVentanaConfirmacion(msgError, this.getStage())) {
				return;
			}
			encontrado = true;
			esDevolucionSinOrigen = true;
		}

		try {
			// Confirmación gerente
			HashMap<String, Object> datos = new HashMap<>();
			datos.put(AutorizacionGerenteUtils.PARAMETRO_REQUIERE_GERENTE, true);
			AutorizacionGerenteUtils.muestraPantallaAutorizacion(getApplication().getMainView(), getStage(), datos);
			if (encontrado) {

				// Comprobar plazo maximo
				comprobarPlazoMaximo();

				//Parte estándar
				boolean esMismoTratamientoFiscal = ticketManager.comprobarTratamientoFiscalDev();
				if (!esMismoTratamientoFiscal) {
					try {
						ticketManager.eliminarTicketCompleto();
					}
					catch (Exception e) {
						log.error("recuperarTicketDevolucionSucceeded() - Ha habido un error al eliminar los tickets: " + e.getMessage(), e);
					}

					lbMensajeError.setText(I18N.getTexto("El ticket fue realizando en una tienda con un tratamiento fiscal diferente al de esta tienda. No se puede realizar esta devolución."));
					return;
				}
				else {
					try {
						getDatos().put(FacturacionArticulosController.TICKET_KEY, ticketManager);
						getView().changeSubView(IntroduccionArticulosView.class);
					}
					catch (InitializeGuiException e) {
						if (e.isMostrarError()) {
							log.error("accionCambiarArticulo() - Error abriendo ventana", e);
							VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Error cargando pantalla. Para mas información consulte el log."), e);
						}
					}
				}

				//GAP-64 DEVOLUCIONES. En caso de no encontrar documento origen se tiene que evitar esta parte, ya que no existe ticket origen.
				if (!esDevolucionSinOrigen) {
					boolean recoveredOnline = ticketManager.getTicket().getCabecera().getDatosDocOrigen().isRecoveredOnline();
					if (!recoveredOnline) {
						VentanaDialogoComponent.crearVentanaAviso(
								I18N.getTexto("No se han podido recuperar las líneas devueltas desde la central. Por favor, compruebe el ticket impreso para ver las líneas ya devueltas."),
								getStage());
					}
				}
			}
			else {
				lbMensajeError.setText(I18N.getTexto("No se ha encontrado ningún ticket con esos datos"));
			}
			// Fin parte estándar
		}
		catch (InitializeGuiException e) {
			if (e.isMostrarError()) {
				log.error("recuperarTicketDevolucionSucceeded() - Ha habido un error en el proceso de autorización: " + e.getMessage(), e);
				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error en el proceso de autorización. Contacte con un administrador."), e);
			}
		}
		catch (Exception e) {
			log.error("recuperarTicketDevolucionSucceeded() - Ha habido un error al comprobar el plazo máximo de la devolución: " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al consultar el plazo de devolución. Contacte con un administrador."), e);
		}
	}

	@Override
		public void initializeForm() throws InitializeGuiException {

			super.initializeForm();

			try {
				((IskaypetTicketManager) ticketManager).comprobarGeneracionATCUD();

				String msg = comprobarTicketAparcadosConIdTicket();
				if(StringUtils.isNotBlank(msg)) {
					throw new InitializeGuiException(msg);
				}

				getDatos().remove(PARAM_PROFORMA_SELECCIONADA);
			} catch (Exception e) {
				throw new InitializeGuiException(e);
			}

			String tipoEstablecimiento = getTipoEstablecimiento();
			if (StringUtils.isBlank(tipoEstablecimiento)) {
				btBuscarProformas.setVisible(false);
			} else {
				btBuscarProformas.setVisible(tipoEstablecimiento.equals(TIPO_ESTABLECIMIENTO_CLINICA));
			}
		}

	private String comprobarTicketAparcadosConIdTicket() throws DocumentoException, TicketsServiceException {
		String msg = "";
		TipoDocumentoBean tipoDocumentoActivo = sesion.getAplicacion().getDocumentos().getDocumento(Documentos.FACTURA_SIMPLIFICADA);
		final TicketAparcadoBean copiaSeguridad = copiaSeguridadTicketService.consultarCopiaSeguridadTicket(tipoDocumentoActivo);

		if (copiaSeguridad != null) {
			log.debug("consultarCopiaSeguridad() - Se ha encontrado una copia de seguridad");
			try {
				log.debug("comprobarTicketAparcadosConIdTicket() - Copia de seguridad: "+new String(copiaSeguridad.getTicket(),"UTF-8"));
			} catch (Exception e) {
				log.error("comprobarTicketAparcadosConIdTicket() - Ha ocurrido un error parseando la copia de seguridad: "+e.getMessage(), e);
			}
			TicketVentaAbono ticketRecuperado = (TicketVentaAbono) MarshallUtil.leerXML(copiaSeguridad.getTicket(), ticketManager.getTicketClasses(tipoDocumentoActivo).toArray(new Class[] {}));

			if (ticketRecuperado != null) {
				if (ticketRecuperado.getIdTicket() != null) {

					log.info("comprobarTicketAparcadosConIdTicket() - El id ticket de la copia de seguridad es "+ticketRecuperado.getIdTicket());
					if(ticketRecuperado.getPagos().isEmpty()) {
						msg = I18N.getTexto("Existe un ticket sin finalizar. Se tiene que terminar ese ticket antes de poder realizar una devolución.");
					}
					else {
						msg = I18N.getTexto("Existe un ticket guardado con pagos realizados. Se tiene que terminar ese ticket antes de poder realizar una devolución.");
					}
				}
			}
		}
		return msg;
	}

	private TipoDocumentoBean getTipoDocumentoDevolucionActual() {
		try {
			String codDoc = this.frConsultaTicket.getCodDoc();
			TipoDocumentoBean documento = this.documentos.getDocumento(codDoc);
			log.debug("getDocumento() - Documento recuperado: " + documento.getCodtipodocumento());
			return documento;
		}
		catch (Exception e) {
			log.error("Error al obtener el documento", e);
		}
		return null;
	}

	private void comprobarPlazoMaximo() throws Exception {
		log.debug("comprobarPlazoMaximo() - Comprobando que la devolución está dentro del plazo.");

		Integer plazoMaximoDevolucion = variablesServices.getVariableAsInteger(DEVOLUCIONES_PLAZO_MAXIMO);

		log.debug("comprobarPlazoMaximo() - Plazo máximo de devolución configurado: " + plazoMaximoDevolucion);

		boolean devolucionFueraDePlazo = compararFechasTicketOrigen(plazoMaximoDevolucion);

		if (devolucionFueraDePlazo) {
			log.debug("comprobarPlazoMaximo() - Plazo máximo de devolución de " + plazoMaximoDevolucion + " días superado.");
			String msg = I18N.getTexto("La venta asociada a este ticket ha superado un periodo de {0} días. ¿Está seguro de querer hacer la devolución?", plazoMaximoDevolucion);

			if (VentanaDialogoComponent.crearVentanaConfirmacion(msg, getStage())) {
				log.info("comprobarPlazoMaximo() - Devolución fuera de plazo iniciada.");
			}
			else {
				log.debug("comprobarPlazoMaximo() - Devolución fuera de plazo cancelada.");
				throw new IllegalAccessError();
			}
		}
	}

	private boolean compararFechasTicketOrigen(Integer plazoMaximoDevolucion) {
		log.debug("compararFechasTicketOrigen - Comprobando si la devolución está dentro del plazo máximo.");

		Calendar hoy = Calendar.getInstance();
		hoy.setTime(new Date());
		Calendar fechaMaximaDevolucion = Calendar.getInstance();
		fechaMaximaDevolucion.setTime(ticketManager.getTicketOrigen().getCabecera().getFecha());
		fechaMaximaDevolucion.add(Calendar.DATE, plazoMaximoDevolucion);
		boolean devolucionFueraDePlazo = hoy.getTime().after(fechaMaximaDevolucion.getTime());
		return devolucionFueraDePlazo;
	}

	//DEVOLUCION PARA FIDELIZADOS

	@FXML
	public void abrirBuscarFidelizado() {
		log.debug("abrirBuscarFidelizado - Abriendo búsqueda de fidelizados");

		getDatos().put(ES_DEVOLUCION, true);
		getDatos().put(PARAMETRO_MOSTRAR_ALTA, false);
		getApplication().getMainView().showModalCentered(BusquedaFidelizadoView.class, getDatos(), getStage());
		if (datos.containsKey(PARAMETRO_LOCALIZADOR_DEVOLUCION)) {
			String localizador = (String) getDatos().get(PARAMETRO_LOCALIZADOR_DEVOLUCION);
			log.debug("abrirBuscarFidelizado() - Se ha recuperado este localidador: " + localizador);
			tfOperacion.setText(localizador);
			accionAceptar();
		}
	}

	@Override
	protected boolean validarFormularioConsultaCliente() {
		Boolean res = super.validarFormularioConsultaCliente();
		if (frConsultaTicket.getCodOperacion().length() > 60) {
			String msg = I18N.getTexto("El localizador introducido es demasiado largo y supera el límite de caracteres");
			lbMensajeError.setText(msg);
			res = false;
		}

		return res;
	}

    @FXML
    public void accionBuscarProformas() {
		log.info("accionBuscarProformas() - Iniciando búsqueda de proformas");

		try {
			List<ProformaHeaderDTO> listaProformas = proformaRestService.listarProformas(sesion, ProformaRestService.TIPO_DEVOLUCION);

			if (listaProformas == null || listaProformas.isEmpty()) {
				log.error("accionBuscarProformas() - No se han encontrado proformas");
				VentanaDialogoComponent.crearVentanaConfirmacionUnBoton(I18N.getTexto("No se han encontrado proformas"), getStage());
				return;
			}

			ProformaDTO proformaDTO;
			if (listaProformas.size() == 1) {
				log.debug("accionBuscarProformas() - Se ha encontrado una única proforma, se selecciona automáticamente");
				ProformaHeaderDTO proformaSeleccionada = listaProformas.get(0);
				proformaDTO = proformaRestService.obtenerProformaCompleta(sesion, proformaSeleccionada.getIdProforma());
				getDatos().put(PARAM_PROFORMA_SELECCIONADA, proformaDTO);
			} else {
				log.debug("accionBuscarProformas() - Se han encontrado varias proformas, se muestra la ventana de selección");
				getDatos().put(SeleccionProformaController.PARAM_PROFORMAS_SELECCION, listaProformas);
				getApplication().getMainView().showModalCentered(SeleccionProformaView.class, getDatos(), getStage());
				proformaDTO = (ProformaDTO) getDatos().getOrDefault(PARAM_PROFORMA_SELECCIONADA, null);

				if ((boolean) getDatos().getOrDefault(PARAM_ACCION_CANCELAR, false)) {
					log.debug("accionBuscarProformas() - Se ha cancelado la selección de proforma");
					return;
				}
			}
			log.info("accionBuscarProformas() - Proforma seleccionada: " + proformaDTO);

			if (proformaDTO == null) {
				log.error("accionBuscarProformas() - No se ha recuperado  la proforma seleccionada");
				VentanaDialogoComponent.crearVentanaError("No se ha recuperado la proforma seleccionada", getStage());
				return;
			}

			String localizador = StringUtils.isNotBlank(proformaDTO.getLocalizador()) ? proformaDTO.getLocalizador() : proformaDTO.getDocumentoOrigen();
			log.debug("accionBuscarProformas() - Se ha recuperado este localizador: " + localizador);
			tfOperacion.setText(localizador);
			accionAceptar();
		} catch (Exception e) {
			log.error("accionBuscarProformas() - Error al obtener proformas: " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Error al obtener proformas"), e);
		}
	}

	private String getTipoEstablecimiento() {
		try {
			DatosSesionBean datosSesion = new DatosSesionBean();
			datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());
			ValorParametroObjeto parametro = valoresParametrosObjetosService.consultar("D_ALMACENES_TBL.CODALM", sesion.getAplicacion().getCodAlmacen(), "TIPO_ESTABLECIMIENTO", datosSesion);
			log.debug("getTipoEstablecimiento() - Tipo de establecimiento: " + parametro.getValor());
			return parametro.getValor();
		} catch (Exception e) {
			log.error("getTipoEstablecimiento() - Error al consultar el tipo de establecimiento: " + e.getMessage(), e);
			return null;
		}
	}
}
