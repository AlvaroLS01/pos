/**
 * ComerZZia 3.0 Copyright (c) 2008-2015 Comerzzia, S.L. All Rights Reserved. THIS WORK IS SUBJECT TO SPAIN AND
 * INTERNATIONAL COPYRIGHT LAWS AND TREATIES. NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED COPIED,
 * DISTRIBUTED, REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED, LINKED, RECAST,
 * TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION OF THIS WORK
 * WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. CONSULT THE END USER LICENSE
 * AGREEMENT FOR INFORMATION ON ADDITIONAL RESTRICTIONS.
 */

package com.comerzzia.pos.gui.ventas.cajas.cierre;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import javax.validation.ConstraintViolation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.datepicker.DatePicker;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.core.gui.exception.ValidationException;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.inicio.InicioView;
import com.comerzzia.pos.gui.ventas.cajas.CajasView;
import com.comerzzia.pos.gui.ventas.cajas.recuentos.RecuentoCajaView;
import com.comerzzia.pos.persistence.cajas.acumulados.CajaLineaAcumuladoBean;
import com.comerzzia.pos.persistence.cajas.recuentos.CajaLineaRecuentoBean;
import com.comerzzia.pos.persistence.core.empresas.EmpresaBean;
import com.comerzzia.pos.services.cajas.Caja;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionCaja;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Controller
public class CierreCajaController extends WindowController implements Initializable, IContenedorBotonera {

	// <editor-fold desc="Declaración de variables">
	private static final Logger log = Logger.getLogger(CierreCajaController.class.getName());
	public static final String PERMISO_ARQUEO_VISIBLE = "ARQUEO VISIBLE";

	protected SesionCaja cajaSesion;
	protected Integer reintentosCierre;

	// Variables de componente
	// botonera de acciones de tabla Movimientos
	protected BotoneraComponent botoneraAccionesTablaMov;
	// botonera de acciones de tabla Ventas
	BotoneraComponent botoneraAccionesTablaVen;

	// Componentes de pantalla
	@FXML
	protected Label lbCaja, lbEntradasTickets, lbSalidasTickets;
	@FXML
	protected DatePicker tfFechaCierre;
	@FXML
	protected TextField tfUsuarioCajero, tfNombreCajero, tfTotalES, tfTotalRecuento, tfDescuadre, tfEntradasVentas, tfSalidasDevolucion, tfEntradasMovimientos, tfSalidasMovimientos, tfEntradasTotal,
	        tfSalidasTotal, tfFechaApertura;
	@FXML
	protected AnchorPane panelBotoneraTabla;
	@FXML
	protected TableView tbRecuento;
	@FXML
	protected TableColumn tcRecuentoMedioPago, tcRecuentoEntrada, tcRecuentoSalida, tcRecuentoTotalES, tcRecuentoRecuento, tcRecuentoDescuadre;
	@FXML
	protected VBox grupoDescuadre;
	@FXML
	protected Button btCerrarCaja;

	@Autowired
	private VariablesServices variablesServices;

	// Lineas de cierre de pantalla de cierre de caja
	protected ObservableList<CierreCajaGui> lineasCierre;

	// formulario de cierre de caja
	protected CierreCajaFormularioGui formularioCierreCaja;

	@Autowired
	private Sesion sesion;

	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Creación e inicialización">
	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {

		// Mensaje sin contenido para tabla. los establecemos a vacio
		tbRecuento.setPlaceholder(new Label(""));

		tcRecuentoMedioPago.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbRecuento", "tcRecuentoMedioPago", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcRecuentoEntrada.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbRecuento", "tcRecuentoEntrada", 2, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcRecuentoSalida.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbRecuento", "tcRecuentoSalida", 2, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcRecuentoTotalES.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbRecuento", "tcRecuentoTotalES", 2, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcRecuentoRecuento.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbRecuento", "tcRecuentoRecuento", 2, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcRecuentoDescuadre.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbRecuento", "tcRecuentoDescuadre", 2, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));

		tcRecuentoMedioPago.setCellValueFactory(new PropertyValueFactory<CierreCajaGui, String>("medioPago"));
		tcRecuentoEntrada.setCellValueFactory(new PropertyValueFactory<CierreCajaGui, BigDecimal>("entrada"));
		tcRecuentoSalida.setCellValueFactory(new PropertyValueFactory<CierreCajaGui, BigDecimal>("salida"));
		tcRecuentoTotalES.setCellValueFactory(new PropertyValueFactory<CierreCajaGui, BigDecimal>("total"));
		tcRecuentoRecuento.setCellValueFactory(new PropertyValueFactory<CierreCajaGui, BigDecimal>("recuento"));
		tcRecuentoDescuadre.setCellValueFactory(new PropertyValueFactory<CierreCajaGui, BigDecimal>("descuadre"));

		// Inicializamos el formulario
		formularioCierreCaja = SpringContext.getBean(CierreCajaFormularioGui.class);

		// Asignamos un componente a cada elemento del formulario. (Para establecer foco o estilos de error)
		formularioCierreCaja.setFormField("fechaCierre", tfFechaCierre);

		// tfFechaCierre.focusedProperty().addListener(new ChangeListener<Boolean>() {
		//
		// @Override
		// public void changed(ObservableValue<? extends Boolean> paramObservableValue, Boolean paramT1, Boolean
		// paramT2) {
		// Date desformateaFecha = FormatUtil.getInstance().desformateaFecha(tfFechaCierre.getTexto());
		// if(desformateaFecha != null){
		// tfFechaCierre.setText(FormatUtil.getInstance().formateaFecha(desformateaFecha));
		// }
		// }
		// });
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		log.debug("accionNegarRegistroTabla() - ");
		try {
			cajaSesion = sesion.getSesionCaja();
			cajaSesion.actualizarRecuentoCaja();

			// Poner cada lista de movimientos en cada tabla de la pestaña correspondiente
			refrescarDatosPantalla();
			Date fechaCierre = new Date();
			tfFechaCierre.setSelectedDate(fechaCierre);

			// Si la variable cierre diario obligatorio está activa, la fecha de cierre será la misma de apertura y no
			// se puede editar
			if (variablesServices.getVariableAsBoolean(VariablesServices.CAJA_CIERRE_CAJA_DIARIO_OBLIGATORIO, true)) {
				tfFechaCierre.setSelectedDate(cajaSesion.getCajaAbierta().getFechaApertura());
				tfFechaCierre.setDisable(true);
			}
			else {
				tfFechaCierre.setDisable(false);
			}

			if (cajaSesion.isCajaAbierta()) {
				lbCaja.setText(I18N.getTexto("Caja ABIERTA"));
			}
			else {
				lbCaja.setText(I18N.getTexto("Caja CERRADA"));
			}

			// cajero
			tfNombreCajero.setText(sesion.getSesionUsuario().getUsuario().getDesusuario());
			tfUsuarioCajero.setText(sesion.getSesionUsuario().getUsuario().getUsuario());

		}
		catch (CajasServiceException e) {
			log.error("initializeForm() - Error de caja. : " + e.getMessageI18N(), e);
			throw new InitializeGuiException(e.getMessageI18N(), e);
		}
		catch (Exception e) {
			log.error("initializeForm() - Error inesperado inicializando formulario. : " + e.getMessage(), e);
			throw new InitializeGuiException(e);
		}
	}

	@Override
	public void initializeFocus() {
		btCerrarCaja.requestFocus();
	}

	@Override
	public void comprobarPermisosUI() {
		super.comprobarPermisosUI();
		// Comprobamos que el usuario pueda ver el arqueo
		try {
			super.compruebaPermisos(PERMISO_ARQUEO_VISIBLE);
			grupoDescuadre.setVisible(true);
		}
		catch (SinPermisosException ex) {
			log.debug("initializeForm() - El usuario no puede ver el arqueo");
			grupoDescuadre.setVisible(false);
		}
	}

	/**
	 * Inicializa componetes personalizados de pantalla: botoneras, etc
	 * 
	 * @throws InitializeGuiException
	 */
	@Override
	public void initializeComponents() throws InitializeGuiException {
		// Comprobamos que existe el tipo de documento asociado
		try {
			sesion.getAplicacion().getDocumentos().getDocumento(Documentos.CIERRE_CAJA);
		}
		catch (DocumentoException e) {
			throw new InitializeGuiException(I18N.getTexto("No está configurado el tipo de documento cierre de caja en el entorno."));
		}
		try {
			log.debug("inicializarComponentes() - Inicialización de componentes");

			log.debug("inicializarComponentes() - Carga de acciones de botonera de tabla de movimientos");
			List<ConfiguracionBotonBean> listaAccionesTablaMov = cargarAccionesTabla();
			botoneraAccionesTablaMov = new BotoneraComponent(3, 1, this, listaAccionesTablaMov, panelBotoneraTabla.getPrefWidth(), panelBotoneraTabla.getPrefHeight(),
			        BotonBotoneraSimpleComponent.class.getName());
			panelBotoneraTabla.getChildren().clear();
			panelBotoneraTabla.getChildren().add(botoneraAccionesTablaMov);
			registrarAccionCerrarVentanaEscape();
		}
		catch (CargarPantallaException ex) {
			log.error("inicializarComponentes() - Error inicializando pantalla de gestiónd e cajas");
			VentanaDialogoComponent.crearVentanaError("Error cargando pantalla. Para mas información consulte el log.", getStage());
		}
	}

	protected List<ConfiguracionBotonBean> cargarAccionesTabla() {
		List<ConfiguracionBotonBean> listaAcciones = new ArrayList<>();
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/recuento-caja-32.png", null, null, "ACCION BOTONERA RECUENTO DE CAJA", "VENTANA"));
		return listaAcciones;
	}

	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Funciones relacionadas con interfaz GUI y manejo de pantalla">
	// </editor-fold>
	/**
	 * Ejecuta la acción pasada por parámetros.
	 *
	 * @param botonAccionado
	 *            botón que ha sido accionado
	 * @throws CajasServiceException 
	 */
	@Override
	public void realizarAccion(BotonBotoneraComponent botonAccionado) throws CajasServiceException {
		log.debug("realizarAccion() - Realizando la acción : " + botonAccionado.getClave() + " de tipo : " + botonAccionado.getTipo());
		switch (botonAccionado.getClave()) {
		// BOTONERA TABLA MOVIMIENTOS
			case "ACCION_TABLA_ANTERIOR_REGISTRO":
				accionTablaMovAnteriorRegistro();
				break;
			case "ACCION_TABLA_SIGUIENTE_REGISTRO":
				accionTablaMovSiguienteRegistro();
				break;
			case "ACCION BOTONERA RECUENTO DE CAJA":
				abrirRecuentoCaja();
				break;
			default:
				log.error("No se ha especificado acción en pantalla para la operación :" + botonAccionado.getClave());
				break;
		}
	}

	/**
	 * Acción de cierre de caja
	 */
	@FXML
	public void accionCerrarCaja() {
		accionCierreCaja();
	}

	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="AccionesMenu">
	protected void abrirRecuentoCaja() throws CajasServiceException {
		log.debug("abrirRecuentoCaja() - Abrir pantalla de recuento de caja.");
		if (getApplication().getMainView().getSubViews().size() > 2) {
			if (!VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Para cerrar la caja se deben cerrar todas las pantallas abiertas. ¿Desea continuar?"), getStage())) {
				return;
			}
			boolean couldClose = getApplication().getMainView().closeAllViewsExcept(InicioView.class, CajasView.class, CierreCajaView.class);
			if (!couldClose) {
				return;
			}
			limpiarRecuentos();
		} else {
			limpiarRecuentos();
		}

		getApplication().getMainView().showModal(RecuentoCajaView.class);
		refrescarDatosPantalla();
	}

	private void limpiarRecuentos() throws CajasServiceException {
	    if (cajaSesion.existeRecuento(cajaSesion.getCajaAbierta())) {
	    	if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Ya existe un recuento de la caja. ¿Desea borrarlo y empezar de nuevo?"), getStage())) {
	    		if (cajaSesion.getCajaAbierta().getLineasRecuento() != null && !cajaSesion.getCajaAbierta().getLineasRecuento().isEmpty()) {
	    			cajaSesion.getCajaAbierta().setLineasRecuento(new ArrayList<CajaLineaRecuentoBean>());
	    		}
	    		cajaSesion.limpiarRecuentos(cajaSesion.getCajaAbierta());
	    	}
	    }
    }

	protected void accionCierreCaja() {
		// No necesitamos comprobar que se pulse 2 veces seguidas el botón porque se muestra una ventana de confirmación
		log.debug("accionCierreCaja()");
		try {
			if (getApplication().getMainView().getSubViews().size() > 2) {
				if (!VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Para cerrar la caja se deben cerrar todas las pantallas abiertas. ¿Desea continuar?"), getStage())) {
					return;
				}
				boolean couldClose = getApplication().getMainView().closeAllViewsExcept(InicioView.class, CajasView.class, CierreCajaView.class);
				if (!couldClose) {
					return;
				}
			}

			boolean tieneDescuadres = cajaSesion.tieneDescuadres();
			Integer reintentosMax = variablesServices.getVariableAsInteger(VariablesServices.CAJA_REINTENTOS_CIERRE);
			
			if (tieneDescuadres) {
				if(reintentosCierre == null){
					reintentosCierre = 0;
				}
				Integer reintentosRestantes = reintentosMax - reintentosCierre;
				if (reintentosRestantes > 0) {
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Caja descuadrada con un importe mayor que el permitido. Revise recuento."), getStage());
					reintentosCierre++;
					return;
				}
				else {
					if (!VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Se va a cerrar la caja con descuadres mayores al valor permitido, ¿Desea continuar?"), getStage())) {
						return;
					}
				}
			}
			if (!VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Seguro de realizar el Cierre?"), getStage())) {
				return;
			}

			// Procedemos al cierre de la caja
			// Actualizamos el formulario
			formularioCierreCaja.setFechaCierre(tfFechaCierre.getTexto());
			// validamos el formulario
			accionValidarForm();

			cajaSesion.guardarCierreCaja(formularioCierreCaja.getDateCierre());

			try {
				imprimirCierre(cajaSesion.getCajaAbierta());
			}
			catch (CajasServiceException ex) {
				log.error("accionCierreCaja() - No se pudo realizar la impresión del cierre de caja. ");
				VentanaDialogoComponent.crearVentanaError(getStage(), ex.getMessageI18N(), ex);
			}
			cajaSesion.cerrarCaja();
			reintentosCierre = 0;
			if (tieneDescuadres) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Caja cerrada con descuadres"), getStage());
			}
			getStage().close();

		}
		catch (ValidationException ex) {
			log.debug("accionCierreCaja() - La validación no fué exitosa"); // La validación ya se encarga de mostrar el
			                                                                // error
		}
		catch (CajasServiceException e) {
			log.error("accionCierreCaja() - Error al tratar de realizar cierre de caja: " + e.getCause(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessageI18N(), e);
		}
	}

	public void imprimirCierre(Caja caja) throws CajasServiceException {
		try {
			log.debug("imprimirCierre() - Imprimiendo ticket: " + caja.getUidDiarioCaja());
			// String printTicket = VelocityServices.getInstance().getPrintCierreCaja(caja);

			// Rellenamos los parametros
			EmpresaBean empresa = sesion.getAplicacion().getEmpresa();
			Date fechaImpresion = new Date();

			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("empresa", empresa);
			parametros.put("caja", caja);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(caja.getFechaApertura());
			if (calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 0 && calendar.get(Calendar.SECOND) == 0 && calendar.get(Calendar.MILLISECOND) == 0) {
				parametros.put("fechaApertura", FormatUtil.getInstance().formateaFechaCorta(caja.getFechaApertura()));
			}
			else {
				parametros.put("fechaApertura", FormatUtil.getInstance().formateaFechaCorta(caja.getFechaApertura()) + " " + FormatUtil.getInstance().formateaHora(caja.getFechaApertura()));
			}
			calendar.setTime(caja.getFechaCierre());
			if (calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 0 && calendar.get(Calendar.SECOND) == 0 && calendar.get(Calendar.MILLISECOND) == 0) {
				parametros.put("fechaCierre", FormatUtil.getInstance().formateaFechaCorta(caja.getFechaCierre()));
			}
			else {
				parametros.put("fechaCierre", FormatUtil.getInstance().formateaFechaCorta(caja.getFechaCierre()) + " " + FormatUtil.getInstance().formateaHora(caja.getFechaCierre()));
			}
			parametros.put("fechaImpresion", FormatUtil.getInstance().formateaFechaCorta(fechaImpresion) + " " + FormatUtil.getInstance().formateaHora(fechaImpresion));
			parametros.put("acumulados", new ArrayList(caja.getAcumulados().values()));

			// Llamamos al servicio de impresión
			ServicioImpresion.imprimir(ServicioImpresion.PLANTILLAS_CIERRE_CAJA, parametros);

		}
		catch (Exception e) {
			log.error("imprimirCierre() - Error imprimiendo  cierre de caja. Error inesperado: " + e.getMessage(), e);
			throw new CajasServiceException(I18N.getTexto("Lo sentimos, ha ocurrido un error al imprimir."), e);
		}
	}

	protected void accionTablaMovAnteriorRegistro() {
		log.debug("accionTablaMovAnteriorRegistro()");
		accionTablaIrAnteriorRegistro(tbRecuento);
	}

	protected void accionTablaMovSiguienteRegistro() {
		log.debug("accionTablaMovSiguienteRegistro()");
		accionTablaIrSiguienteRegistro(tbRecuento);
	}

	// </editor-fold>
	protected void refrescarDatosPantalla() {
		log.debug("refrescarDatosPantalla()");

		lineasCierre = FXCollections.observableList(new ArrayList<CierreCajaGui>());

		// Líneas de la tabla de descuadres
		for (CajaLineaAcumuladoBean cajaLineaAcumulado : cajaSesion.getCajaAbierta().getAcumulados().values()) {
			lineasCierre.add(new CierreCajaGui(cajaLineaAcumulado));
		}

		// Cuadros de texto
		// -Totales
		tfTotalES.setText(FormatUtil.getInstance().formateaNumero(cajaSesion.getCajaAbierta().getTotal(), 2));
		tfTotalRecuento.setText(FormatUtil.getInstance().formateaNumero(cajaSesion.getCajaAbierta().getTotalRecuento(), 2));
		tfDescuadre.setText(FormatUtil.getInstance().formateaNumero(cajaSesion.getCajaAbierta().getDescuadre(), 2));

		// -Fechas
		tfFechaApertura.setText(FormatUtil.getInstance().formateaFecha(cajaSesion.getCajaAbierta().getFechaApertura()));

		// -Entradas/salidas
		tfEntradasVentas.setText(FormatUtil.getInstance().formateaNumero(cajaSesion.getCajaAbierta().getTotalVentasEntrada(), 2));
		tfEntradasMovimientos.setText(FormatUtil.getInstance().formateaNumero(cajaSesion.getCajaAbierta().getTotalApuntesEntrada(), 2));
		tfEntradasTotal.setText(FormatUtil.getInstance().formateaNumero(cajaSesion.getCajaAbierta().getTotalEntradas(), 2));
		tfSalidasDevolucion.setText(FormatUtil.getInstance().formateaNumero(cajaSesion.getCajaAbierta().getTotalVentasSalida(), 2));
		tfSalidasMovimientos.setText(FormatUtil.getInstance().formateaNumero(cajaSesion.getCajaAbierta().getTotalApuntesSalida(), 2));
		tfSalidasTotal.setText(FormatUtil.getInstance().formateaNumero(cajaSesion.getCajaAbierta().getTotalSalidas(), 2));

		lbEntradasTickets.setText(I18N.getTexto("En ") + " " + cajaSesion.getCajaAbierta().getNumTicketsEntrada() + " " + I18N.getTexto("Tickets"));
		lbSalidasTickets.setText(I18N.getTexto("En ") + " " + cajaSesion.getCajaAbierta().getNumTicketsSalida() + " " + I18N.getTexto("Tickets"));

		// Asignamos las lineas a la tabla
		tbRecuento.setItems(lineasCierre);
	}

	/**
	 * Validación del formulario
	 *
	 * @return
	 */
	protected void accionValidarForm() throws ValidationException {
		// Limpiamos los errores que pudiese tener el formulario
		boolean hayErrorValidacion = false;
		formularioCierreCaja.clearErrorStyle();
		formularioCierreCaja.setDateCierre(null);

		// Validamos el formulario de login
		Set<ConstraintViolation<CierreCajaFormularioGui>> constraintViolations = ValidationUI.getInstance().getValidator().validate(formularioCierreCaja);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<CierreCajaFormularioGui> next = constraintViolations.iterator().next();
			formularioCierreCaja.setErrorStyle(next.getPropertyPath(), true);
			formularioCierreCaja.setFocus(next.getPropertyPath());
			// Mostramos el error en una ventana de error
			VentanaDialogoComponent.crearVentanaError(next.getMessage(), this.getScene().getWindow());
			throw new ValidationException();
		}

		Calendar calendarHoy = Calendar.getInstance();
		calendarHoy.set(Calendar.HOUR_OF_DAY, 0);
		calendarHoy.set(Calendar.MINUTE, 0);
		calendarHoy.set(Calendar.SECOND, 0);
		calendarHoy.set(Calendar.MILLISECOND, 0);

		Date fechaApertura = cajaSesion.getCajaAbierta().getFechaApertura();
		Calendar calendarApertura = Calendar.getInstance();
		calendarApertura.setTime(fechaApertura);

		Date fechaCierre = FormatUtil.getInstance().desformateaFechaHora(tfFechaCierre.getTexto(), true);
		Calendar calendarCierre = Calendar.getInstance();
		calendarCierre.setTime(fechaCierre);

		// Si la fecha de cierre no es hoy, la ponemos sin hora
		Calendar calendarCierreSinHora = Calendar.getInstance();
		calendarCierreSinHora.setTime(fechaCierre);
		calendarCierreSinHora.set(Calendar.HOUR_OF_DAY, 0);
		calendarCierreSinHora.set(Calendar.MINUTE, 0);
		calendarCierreSinHora.set(Calendar.SECOND, 0);
		calendarCierreSinHora.set(Calendar.MILLISECOND, 0);
		if (!calendarCierreSinHora.equals(calendarHoy)) {
			calendarCierre.setTime(calendarCierreSinHora.getTime());
			fechaCierre = calendarCierreSinHora.getTime();
		}

		// La comprobación la hacemos sin hora
		calendarApertura.set(Calendar.HOUR_OF_DAY, 0);
		calendarApertura.set(Calendar.MINUTE, 0);
		calendarApertura.set(Calendar.SECOND, 0);
		calendarApertura.set(Calendar.MILLISECOND, 0);
		if (calendarCierreSinHora.before(calendarApertura)) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("La fecha de cierre no puede ser anterior a la fecha de apertura."), this.getScene().getWindow());
			throw new ValidationException();
		}

		formularioCierreCaja.setDateCierre(fechaCierre);
	}

}
