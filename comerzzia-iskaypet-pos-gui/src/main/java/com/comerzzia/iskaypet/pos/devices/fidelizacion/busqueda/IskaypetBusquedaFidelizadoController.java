package com.comerzzia.iskaypet.pos.devices.fidelizacion.busqueda;

import com.comerzzia.api.model.loyalty.FidelizadoBean;
import com.comerzzia.api.rest.client.exceptions.RestException;
import com.comerzzia.api.rest.client.exceptions.RestHttpException;
import com.comerzzia.api.rest.client.fidelizados.ConsultarFidelizadoRequestRest;
import com.comerzzia.iskaypet.pos.api.rest.client.fidelizados.IskaypetFidelizadoRest;
import com.comerzzia.iskaypet.pos.devices.fidelizacion.gestion.GestionTicketsFidelizadoView;
import com.comerzzia.iskaypet.pos.gui.ventas.devoluciones.IskaypetDevolucionesController;
import com.comerzzia.iskaypet.pos.services.core.fidelizacion.exception.FidelizacionService;
import com.comerzzia.iskaypet.pos.services.core.fidelizacion.exception.HttpRestOfflineException;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.dispositivo.fidelizacion.busqueda.BusquedaFidelizadoController;
import com.comerzzia.pos.dispositivo.fidelizacion.busqueda.FormularioBusquedaFidelizadoBean;
import com.comerzzia.pos.dispositivo.fidelizacion.seleccion.SeleccionFidelizadoView;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.i18n.I18N;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.comerzzia.iskaypet.pos.services.core.fidelizacion.exception.FidelizacionService.convertApiToPos;
import static com.comerzzia.iskaypet.pos.services.core.fidelizacion.exception.FidelizacionService.convertPosToApi;

@Primary
@Component
public class IskaypetBusquedaFidelizadoController extends BusquedaFidelizadoController {

	private static final Logger log = Logger.getLogger(IskaypetBusquedaFidelizadoController.class);

	public static final String PARAMETRO_MOSTRAR_ALTA = "MOSTRAR_ALTA";
	public static final String PARAMETRO_TITULO = "PARAMETRO_TITULO";
	public static final String PARAMETRO_BOTON_ACEPTAR = "PARAMETRO_BOTON_ACEPTAR";

	@Autowired
	private VariablesServices variablesServices;
	@Autowired
	private Sesion sesion;

	@Autowired
	private FidelizacionService fidelizacionService;

	@FXML
	protected Button btnAceptar;

	private Boolean esDevolucion;

	@FXML
	private Button btnBuscarOffline;

	@FXML
	private AnchorPane statusBar;

	@FXML
	private Label lbConexionStatus;  // "Online"/"Offline"

	private boolean currentOfflineMode = false;

	private ScheduledExecutorService customScheduler;

	public void startConnectionCheckScheduler() {
		if (customScheduler == null || customScheduler.isShutdown()) {
			log.debug("starConnectionCheckScheduler() - inicializando scheduler para comprobar conexión.");
			customScheduler = Executors.newSingleThreadScheduledExecutor();
			customScheduler.scheduleAtFixedRate(() -> {
				try {
					setOfflineMode(fidelizacionService.checkOnlineStatus());
				} catch (Exception e) {
					log.warn("startConnectionCheckScheduler() - error checking status: " + e.getMessage(), e);
				}
			}, 0, 1, TimeUnit.MINUTES);
		}
	}

	public void stopConnectionCheckScheduler() {
		if (customScheduler != null && !customScheduler.isShutdown()) {
			customScheduler.shutdownNow();
			log.debug("stopConnectionCheckScheduler() - se ha detenido scheduler que comprueba conexión.");
		} else {
			log.debug("stopConnectionCheckScheduler() - No hay instancias de scheduler activas.");
		}
	}

	@Override
	public void onClose() {
		super.onClose();
		stopConnectionCheckScheduler();
	}

	@Override
	public void initializeComponents() {
		super.initializeComponents();
		btnAltaFidelizado.setText(I18N.getTexto("Alta cliente"));
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		limpiarFormulario();

		// Comprueba si se debe mostrar el botón de alta
		btnAltaFidelizado.setVisible(true);
		if (getDatos().containsKey(PARAMETRO_MOSTRAR_ALTA)) {
			btnAltaFidelizado.setVisible((Boolean) getDatos().get(PARAMETRO_MOSTRAR_ALTA));
		}

		lbTitulo.setText(I18N.getTexto("Lea o escriba el código de barras de la tarjeta"));
		if (getDatos().containsKey(PARAMETRO_TITULO)) {
			lbTitulo.setText((String) getDatos().get(PARAMETRO_TITULO));
		}

		btnAceptar.setText(I18N.getTexto("Aceptar"));
		if (getDatos().containsKey(PARAMETRO_BOTON_ACEPTAR)) {
			btnAceptar.setText((String) getDatos().get(PARAMETRO_BOTON_ACEPTAR));
		}

		// Comprueba si es una devolucion
		esDevolucion = false;
		if (getDatos().containsKey(IskaypetDevolucionesController.ES_DEVOLUCION)) {
			esDevolucion = (Boolean) getDatos().get(IskaypetDevolucionesController.ES_DEVOLUCION);
		}

		boolean offline = fidelizacionService.checkOnlineStatus();
		setOfflineMode(offline);

	}

	@Override
	public void accionAltaFidelizado() {
		log.debug("accionAltaFidelizado() - Verificando conexión antes de continuar...");

		// comprobar estado de conexión
		boolean offline = fidelizacionService.checkOnlineStatus();
		setOfflineMode(offline);

		if (offline) {
			log.warn("accionAltaFidelizado() - No hay conexión. Cancelando proceso de alta.");

			Exception dummyException = new Exception("No se puede realizar Alta Fidelizados en modo Offline"); // se necesita para el constructor de la ventana de error, no sirve otro propósito.
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Se ha perdido la conexión, no se puede hacer Alta Fidelizados."), dummyException);

			return;
		}

		log.debug("accionAltaFidelizado() - Conexión verificada. Continuando con alta de fidelizado.");
		super.accionAltaFidelizado();
	}



	@FXML
	public void accionAceptar() {
		if (StringUtils.isBlank(tfNumero.getText()) && StringUtils.isBlank(tfApellidos.getText()) && StringUtils.isBlank(tfNombre.getText()) && StringUtils.isBlank(tfTelefono.getText())
		        && StringUtils.isBlank(tfEmail.getText()) && StringUtils.isBlank(tfDocumento.getText())) {

			VentanaDialogoComponent.crearVentanaConfirmacionUnBoton(I18N.getTexto("Es necesario rellenar al menos uno de los campos de búsqueda"), getStage());
			return;
		}

		// comprobamos si hay conexión
		boolean offline = fidelizacionService.checkOnlineStatus();
		setOfflineMode(offline);

		if (offline) {
			VentanaDialogoComponent.crearVentanaError(
					getStage(),
					I18N.getTexto("No se ha podido establecer la conexión con la central, pulse Escape para realizar búsqueda offline."),
					new HttpRestOfflineException());
			return;
		}

		frBuscaFidelizado.setCodTarjRegalo(tfNumero.getText());
		frBuscaFidelizado.setApellidos(tfApellidos.getText());
		frBuscaFidelizado.setNombre(tfNombre.getText());
		frBuscaFidelizado.setTelefono(tfTelefono.getText());
		frBuscaFidelizado.setEmail(tfEmail.getText());
		frBuscaFidelizado.setDocumento(tfDocumento.getText());

		// Validamos el formulario
		Set<ConstraintViolation<FormularioBusquedaFidelizadoBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frBuscaFidelizado);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<FormularioBusquedaFidelizadoBean> next = constraintViolations.iterator().next();
			frBuscaFidelizado.setErrorStyle(next.getPropertyPath(), true);
			frBuscaFidelizado.setFocus(next.getPropertyPath());
			VentanaDialogoComponent.crearVentanaError(next.getMessage(), this.getStage());
		} else {
			ConsultarFidelizadoRequestRest req = new ConsultarFidelizadoRequestRest(variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY),
					sesion.getAplicacion().getUidActividad());
			req.setApellidos(frBuscaFidelizado.getApellidos());
			req.setNombre(frBuscaFidelizado.getNombre());
			req.setTelefono(frBuscaFidelizado.getTelefono());
			req.setEmail(frBuscaFidelizado.getEmail());
			req.setDocumento(frBuscaFidelizado.getDocumento());
			req.setNumeroTarjeta(frBuscaFidelizado.getCodTarjRegalo());

			String tarjeta = "";

			if (frBuscaFidelizado.getCodTarjRegalo() != null && !frBuscaFidelizado.getCodTarjRegalo().isEmpty()) {
				tarjeta = frBuscaFidelizado.getCodTarjRegalo();
				try {

					if (fidelizacionService.isOfflineMode()) {
						throw new HttpRestOfflineException();
					}

					req.setNumeroTarjeta(tarjeta);

					List<FidelizadoBean> fidelizados = IskaypetFidelizadoRest.getFidelizadosDatos(req);
					if (fidelizados == null || fidelizados.isEmpty()) {
						VentanaDialogoComponent.crearVentanaConfirmacionUnBoton(I18N.getTexto("No se ha encontrado ningún fidelizado con los datos introducidos"), getStage());
					} else {
						getDatos().put(PARAMETRO_FIDELIZADO_SELECCIONADO, fidelizados.get(0));
					}

					// Si es venta normal, setea los datos continua estandar
					if (esDevolucion == null || !esDevolucion) {
						getDatos().put(PARAMETRO_NUM_TARJETA, tarjeta);
						getStage().close();
					} else {
						accionSeleccionarFidelizado(esDevolucion, tarjeta);
					}

				} catch (RestException | RestHttpException | HttpRestOfflineException e) {
					log.error("accionAceptar() - error buscando fidelizado" + e.getMessage(), e);
				}
			} else {
				FidelizadoBean fidelizado = null;
				try {
					if (fidelizacionService.isOfflineMode()) {
						throw new HttpRestOfflineException();
					}

					List<FidelizadoBean> fidelizados = IskaypetFidelizadoRest.getFidelizadosDatos(req);
					if (fidelizados.size() == 0) {
						VentanaDialogoComponent.crearVentanaConfirmacionUnBoton(I18N.getTexto("No se ha encontrado ningún fidelizado con los datos introducidos"), getStage());
						tarjeta = null;
					} else {

						if (fidelizados.size() == 1) {
							fidelizado = fidelizados.get(0);
							getDatos().put(PARAMETRO_FIDELIZADOS_SELECCION, fidelizados);
							// Si no es devolucion devuelve el fidelizado normal para la pantalla ventas
							if (esDevolucion == null || !esDevolucion) {
								getStage().close();
							} else {
								accionSeleccionarFidelizado(esDevolucion, fidelizado.getNumeroTarjeta());
							}
						} else if (fidelizados.size() > 1) {
							getDatos().put(PARAMETRO_FIDELIZADOS_SELECCION, fidelizados);
							accionSeleccionarFidelizado(esDevolucion, tarjeta);
							fidelizado = (FidelizadoBean) getDatos().get(PARAMETRO_FIDELIZADO_SELECCIONADO);
						}
						if (fidelizado != null) {
							tarjeta = fidelizado.getNumeroTarjeta();
							if (StringUtils.isBlank(tarjeta)) {
								VentanaDialogoComponent.crearVentanaConfirmacionUnBoton(I18N.getTexto("El fidelizado no dispone de una tarjeta"), getStage());
							} else if (!StringUtils.isBlank(tarjeta)) {

								getDatos().put(PARAMETRO_NUM_TARJETA, tarjeta);
								getStage().close();
							}
						} else {
							if (!StringUtils.isBlank(tarjeta)) {

								getDatos().put(PARAMETRO_NUM_TARJETA, tarjeta);
								getStage().close();
							}
						}
					}

				} catch (RestException | RestHttpException | HttpRestOfflineException e) {
					log.error("accionAceptar() - ha habido un problema con la petición REST: " + e.getMessage(), e);
					VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("No se ha podido establecer la conexión con la central, pulse Escape para realizar búsqueda offline."), e);
				} catch (Exception e) {
					log.error("accionAceptar() - Ha ocurrido un error: " + e.getMessage(), e);
					VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha ocurrido un error durante la petición a la central, consulte con el administrador."), e);
				}

			}

		}
	}

	@FXML
	public void accionSeleccionarFidelizado(Boolean esDevolucion, String tarjeta) {
		getDatos().put(IskaypetDevolucionesController.ES_DEVOLUCION, esDevolucion);
		// si tiene tarjeta implica que estamos seguro que es un usuario unico y no mostramos la lista de posibles coincidecias
		if (StringUtils.isNotBlank(tarjeta) && esDevolucion) {
			getApplication().getMainView().showModalCentered(GestionTicketsFidelizadoView.class, getDatos(), this.getStage());
		}
		else {
			getApplication().getMainView().showModalCentered(SeleccionFidelizadoView.class, getDatos(), this.getStage());
		}

		getStage().close();
	}

	@Override
	public void accionCancelar() {
		limpiarFormulario();
		super.accionCancelar();
	}

	private void limpiarFormulario() {
		tfNumero.setText("");
		tfNombre.setText("");
		tfApellidos.setText("");
		tfEmail.setText("");
		tfTelefono.setText("");
		tfDocumento.setText("");
	}

	/**
	 * Offline flow
	 */

	@Override
	@FXML
	public void accionAceptarIntro(KeyEvent e) {
		if (e.getCode() == KeyCode.ENTER) {
			if (btnAceptar.isDisabled()) {
				accionBuscarOffline();
			} else {
				accionAceptar();
			}
		}
	}

	@FXML
	public void accionBuscarOffline() {
		log.debug("accionBuscarOffline() - buscando offline");
		if (allFieldsBlank()) {
			VentanaDialogoComponent.crearVentanaConfirmacionUnBoton(
					I18N.getTexto("Es necesario rellenar al menos uno de los campos de búsqueda"), getStage());
		}
		else{
			fillBeanFromUI();

			Set<ConstraintViolation<FormularioBusquedaFidelizadoBean>> constraintViolations =
					ValidationUI.getInstance().getValidator().validate(frBuscaFidelizado);
			if (!constraintViolations.isEmpty()) {
				showFormConstraintError(constraintViolations);
			}

			//LLamar DB local
			List<FidelizadoBean> fidelizados = fidelizacionService.buscarLocalmenteFidelizado(frBuscaFidelizado);

			if (fidelizados == null || fidelizados.isEmpty()) {
				VentanaDialogoComponent.crearVentanaConfirmacionUnBoton(I18N.getTexto("No se ha encontrado ningún fidelizado con los datos introducidos (OFFLINE)"), getStage());
			}

			//un solo resultado
			if (fidelizados.size() == 1) {
				updateUIWithFidelizado(fidelizados.get(0));
				FidelizacionBean fidelizado = convertApiToPos(fidelizados.get(0));

				getDatos().put(PARAMETRO_FIDELIZADO_SELECCIONADO, fidelizado);

				if (esDevolucion == null || !esDevolucion) {
					getDatos().put(PARAMETRO_NUM_TARJETA, fidelizado.getNumTarjetaFidelizado());
					getStage().close();
				} else {
					accionSeleccionarFidelizado(esDevolucion, fidelizado.getNumTarjetaFidelizado());
				}
			}
			//multiples resultados
			else if (fidelizados.size() > 1) {
				getDatos().put(PARAMETRO_FIDELIZADOS_SELECCION, fidelizados);
				accionSeleccionarFidelizado(esDevolucion, "");

				FidelizadoBean selectedFidelizado = (FidelizadoBean) getDatos().get(PARAMETRO_FIDELIZADO_SELECCIONADO);

				if (selectedFidelizado != null) {

					FidelizacionBean posBean = convertApiToPos(selectedFidelizado);
					getDatos().put(PARAMETRO_FIDELIZADO_SELECCIONADO, posBean);

					updateUIWithFidelizado(convertPosToApi(posBean));

					String tarjeta = selectedFidelizado.getNumeroTarjeta();
					if (StringUtils.isBlank(tarjeta)) {
						VentanaDialogoComponent.crearVentanaConfirmacionUnBoton(
								I18N.getTexto("El fidelizado no dispone de una tarjeta"), getStage());
					} else {
						getDatos().put(PARAMETRO_NUM_TARJETA, tarjeta);
						getStage().close();
					}
				}
			}
		}
	}

	private boolean allFieldsBlank() {
		return StringUtils.isBlank(tfNumero.getText())
				&& StringUtils.isBlank(tfApellidos.getText())
				&& StringUtils.isBlank(tfNombre.getText())
				&& StringUtils.isBlank(tfTelefono.getText())
				&& StringUtils.isBlank(tfEmail.getText())
				&& StringUtils.isBlank(tfDocumento.getText());
	}

	private void fillBeanFromUI() {
		frBuscaFidelizado.setCodTarjRegalo(tfNumero.getText());
		frBuscaFidelizado.setApellidos(tfApellidos.getText());
		frBuscaFidelizado.setNombre(tfNombre.getText());
		frBuscaFidelizado.setTelefono(tfTelefono.getText());
		frBuscaFidelizado.setEmail(tfEmail.getText());
		frBuscaFidelizado.setDocumento(tfDocumento.getText());
	}

	private void updateUIWithFidelizado(FidelizadoBean fidelizado) {
		tfNumero.setText(fidelizado.getNumeroTarjeta());
		tfNombre.setText(fidelizado.getNombre());
		tfApellidos.setText(fidelizado.getApellidos());
		tfDocumento.setText(fidelizado.getDocumento());
	}

	private void showFormConstraintError(Set<ConstraintViolation<FormularioBusquedaFidelizadoBean>> violations) {
		ConstraintViolation<FormularioBusquedaFidelizadoBean> next = violations.iterator().next();
		frBuscaFidelizado.setErrorStyle(next.getPropertyPath(), true);
		frBuscaFidelizado.setFocus(next.getPropertyPath());
		VentanaDialogoComponent.crearVentanaError(next.getMessage(), this.getStage());
	}

	// offline mode para la ventana. Si estado cambia se actualiza el UI en busquedaFidelizadoController.
	public void setOfflineMode(boolean offline) {
		if (this.currentOfflineMode == offline) {
			return;
		}
		this.currentOfflineMode = offline;

		fidelizacionService.setOfflineMode(offline);
		if (offline) {
			log.warn("Se ha perdido la conexión! Activando modo OFFLINE...");
			startConnectionCheckScheduler();

		} else {
			log.info("Conexión recuperada! Activando modo ONLINE...");
			stopConnectionCheckScheduler();
		}
		Platform.runLater(() -> {
			setOfflineModeUI(offline);
		});
	}

	public void setOfflineModeUI(boolean offline) {
		log.debug("setOfflineModeUI() - Actualizando UI a " + (offline ? "OFFLINE" : "ONLINE"));
		if (offline) {
			lbConexionStatus.setText("Sin conexión con el servidor");
			lbConexionStatus.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
			statusBar.setStyle("-fx-background-color: red;");
			btnBuscarOffline.setDisable(false);
			btnAltaFidelizado.setDisable(true);
			btnAceptar.setDisable(true);
		} else {
			statusBar.setStyle("-fx-background-color: green;");
			lbConexionStatus.setText("Online");
			lbConexionStatus.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
			btnBuscarOffline.setDisable(true);
			btnAltaFidelizado.setDisable(false);
			btnAceptar.setDisable(false);
		}
	}
}