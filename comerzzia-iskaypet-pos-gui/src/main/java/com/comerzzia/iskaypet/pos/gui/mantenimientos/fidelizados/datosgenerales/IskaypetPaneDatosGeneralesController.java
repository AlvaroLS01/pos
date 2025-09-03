package com.comerzzia.iskaypet.pos.gui.mantenimientos.fidelizados.datosgenerales;

import com.comerzzia.api.model.loyalty.ColectivoBean;
import com.comerzzia.api.model.loyalty.ColectivosFidelizadoBean;
import com.comerzzia.api.model.loyalty.SolicitudBean;
import com.comerzzia.api.model.loyalty.TiposContactoFidelizadoBean;
import com.comerzzia.api.rest.client.colectivos.ColectivosRequestRest;
import com.comerzzia.core.util.base.Estado;
import com.comerzzia.core.util.tipoidentificacion.IValidadorDocumentoIdentificacion;
import com.comerzzia.iskaypet.pos.gui.mantenimientos.fidelizados.datosgenerales.lenguaje.LenguajeNotFoundException;
import com.comerzzia.iskaypet.pos.gui.mantenimientos.fidelizados.datosgenerales.lenguaje.LenguajesController;
import com.comerzzia.iskaypet.pos.gui.mantenimientos.fidelizados.datosgenerales.lenguaje.LenguajesView;
import com.comerzzia.iskaypet.pos.persistence.lenguajes.LenguajeBean;
import com.comerzzia.iskaypet.pos.services.lenguajes.LenguajesService;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.ConsultarTodosColectivosTask;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.TipoIdentGui;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.colectivos.ColectivoAyudaGui;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.datosgenerales.PaneDatosGeneralesController;
import com.comerzzia.pos.persistence.paises.PaisBean;
import com.comerzzia.pos.persistence.tiposIdent.TiposIdentBean;
import com.comerzzia.pos.services.core.paises.PaisNotFoundException;
import com.comerzzia.pos.services.core.paises.PaisService;
import com.comerzzia.pos.services.core.paises.PaisServiceException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentNotFoundException;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentService;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentServiceException;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Component
@Primary
@SuppressWarnings({ "unchecked", "rawtypes" })
public class IskaypetPaneDatosGeneralesController extends PaneDatosGeneralesController {

	protected static final String MODO_INSERCION = "INSERCION";
	protected static final String MODO_EDICION = "EDICION";
	protected static final String MODO_CONSULTA = "CONSULTA";
	protected static final String COLECTIVO_DEFECTO = "X_FIDELIZACION.COD_COLECTIVO_REGISTRO";
	protected static final String COLECTIVOS_FIDELIZADOS = "X_POS.COD_COLECTIVOS_FIDELIZADOS";
	protected static final String PERMITE_ALTA_FIDELIZACION = "X_POS.PERMITE_ALTA_FIDELIZACION";
	protected static final String MOSTRAR_PERMITE_NOTIFICACIONES = "X_POS.MOSTRAR_PERMITE_NOTIFICACIONES";
	protected static final String MOSTRAR_RGPD = "X_POS.MOSTRAR_RGPD";

	@Autowired
	private Sesion sesion;
	@Autowired
	private PaisService paisService;
	@Autowired
	private TiposIdentService tiposIdentService;

	@FXML
	private CheckBox cBoxPersona;
	@FXML
	private CheckBox cBoxEmpresa;
	@FXML
	private CheckBox cBoxFidelizado;
	@FXML
	private Label lbFidelizado;
	@FXML
	private Label lbApellidos;
	@FXML
	private Label lbSexo;
	@FXML
	private Label lbEstadoCivil;
	@FXML
	private AnchorPane apTiendaFavorita;
	@FXML
	private AnchorPane apColectivo;
	@FXML
	private TextField tfEmailConfirm;
	@FXML
	private TextField tfConsentimientoRGPD;
	@FXML
	private TextField tfCodLenguaje;
	@FXML
	private TextField tfDesLenguaje;
	@FXML
	private TextField tfTelefonoFijo;
	@FXML
	private Button btBuscarLenguaje;
	@FXML
	protected Label lbConsentimientoRGPD;
	@FXML
	protected CheckBox chNotifEmail;

	protected List<TiposIdentBean> tiposIdent;
	protected Boolean esEmpresa;

	protected Boolean permiteAltaFidelizacion;
	
    @Autowired
    private LenguajesService lenguajesService;
    
    protected String codLenguaje;
	protected Boolean permiteMostrarNotificaciones;

	protected Boolean permiteMostrarRGPD;




	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		super.initialize(url, bundle);

		// Campos que no queremos que sean visibles
		apTiendaFavorita.setVisible(false);
		apColectivo.setVisible(false);
		lbSexo.setVisible(false);
		cbSexo.setVisible(false);
		lbEstadoCivil.setVisible(false);
		cbEstadoCivil.setVisible(false);

		tfEmail.setContextMenu(new ContextMenu());
		tfEmail.setOnKeyPressed(event -> {
			if (event.isShortcutDown() && (event.getCode() == KeyCode.C || event.getCode() == KeyCode.COPY)) {
				event.consume();
			}
		});

		tfEmailConfirm.setContextMenu(new ContextMenu());
		tfEmailConfirm.setOnKeyPressed(event -> {
			if (event.isShortcutDown() && (event.getCode() == KeyCode.C || event.getCode() == KeyCode.COPY)) {
				event.consume();
			}
		});

		permiteAltaFidelizacion = variablesService.getVariableAsBoolean(PERMITE_ALTA_FIDELIZACION, Boolean.TRUE);
		if (!permiteAltaFidelizacion) {
			cBoxFidelizado.setDisable(true);
		}
		
		tfCodLenguaje.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov,
					Boolean oldValue, Boolean newValue) {
				tfCodLenguaje.setText(tfCodLenguaje.getText().toUpperCase());
				if(!newValue && (codLenguaje == null || !codLenguaje.equals(tfCodLenguaje.getText()))){
					codLenguaje = tfCodLenguaje.getText();
					setDesLenguajeByCodLengua(tfCodLenguaje.getText(), false);
				}
			}
		});

		permiteMostrarNotificaciones = variablesService.getVariableAsBoolean(MOSTRAR_PERMITE_NOTIFICACIONES);
		permiteMostrarRGPD = variablesService.getVariableAsBoolean(MOSTRAR_RGPD);

		chNotifEmail.setVisible(permiteMostrarNotificaciones);
		lbConsentimientoRGPD.setVisible(permiteMostrarRGPD);
		tfConsentimientoRGPD.setVisible(permiteMostrarRGPD);
	}

	@Override
	public void camposEditables(boolean editable) {
		super.camposEditables(editable);

		tfEmailConfirm.setEditable(editable);
		tfTelefonoFijo.setEditable(editable);
		tfCodLenguaje.setEditable(editable);
		tfDesLenguaje.setEditable(editable);
		btBuscarLenguaje.setDisable(!editable);
		
		if (permiteAltaFidelizacion) cBoxFidelizado.setDisable(!editable);

		cBoxEmpresa.setDisable(!editable);
		cBoxPersona.setDisable(!editable);
		tfDocumento.setEditable(editable);
		cbTipoDocumento.setDisable(!editable);

		String modo = getTabParentController().getModo();

		if (MODO_INSERCION.equals(modo)) {
			clickPersona();
			setearValoresColectivo(editable);
			tfCodigo.setDisable(true);
			chNotifEmail.setSelected(true);
			if (permiteAltaFidelizacion)
				cBoxFidelizado.setSelected(true);
		} else {
			cargarTipoIdentificacionFidelizado();
			boolean isFidelizado = isFidelizado();
			cBoxFidelizado.setSelected(isFidelizado);
			if (permiteAltaFidelizacion)
				cBoxFidelizado.setDisable(isFidelizado);

			if (MODO_EDICION.equals(modo)) {
				boolean permiteEdicion = permiteEdicionDocumento();
				tfDocumento.setEditable(permiteEdicion);
				cbTipoDocumento.setDisable(!permiteEdicion);
			}
			else if (MODO_CONSULTA.equals(modo)) {
				cBoxFidelizado.setDisable(true);
			}

		}

	}

	private boolean permiteEdicionDocumento() {
		log.debug("permiteEdicionDocumento() - Comprobando si se permite la edición del documento");
		String documento = fidelizado.getDocumento();
		if (StringUtils.isBlank(documento)) {
			log.debug("permiteEdicionDocumento() - El documento no se encuentra en el fidelizado");
			return true;
		}

		boolean esValido = esDocumentoValido(documento);
		log.debug("permiteEdicionDocumento() - El documento es válido: " + esValido);
		return !esValido;
	}

	private boolean esDocumentoValido(String documento) {

		try {
			TipoIdentGui tipoIdentGui = cbTipoDocumento.getSelectionModel().getSelectedItem();
			String claseValidacion = tipoIdentGui.getClaseValidacion();
			if (StringUtils.isBlank(claseValidacion)) {
				log.debug("esDocumentoValido() - La clase de validación es nula, setamos el documento como válido");
				return true;
			}

			IValidadorDocumentoIdentificacion validadorDocumentoIdentificacion = (IValidadorDocumentoIdentificacion) Class.forName(claseValidacion).newInstance();
			if (validadorDocumentoIdentificacion.validarDocumentoIdentificacion(documento)) {
				log.debug("esDocumentoValido() - El documento es válido");
				return true;
			} else {
				log.debug("esDocumentoValido() - El documento no es válido");
				return false;
			}
		} catch (Exception e) {
			log.error("esDocumentoValido() - Error al validar el documento: " + e.getMessage(), e);
			return false;
		}

	}

	private void setearValoresColectivo(boolean editable) {
		try {
			String key = variableColectivo();
			if (StringUtils.isBlank(key)) {
				throw new Exception(COLECTIVO_DEFECTO);
			}
			setearLabelColectivo(key, editable);
		}
		catch (Exception e) {
			setearLabelColectivoPorDefecto(editable);
			log.error("setearValoresColectivo() - Variable de colectivo no encontrada: " + e.getMessage(), e);
		}
	}

	private void setearLabelColectivo(String key, boolean editable) {
		String apiKey = variablesService.getVariableAsString("WEBSERVICES.APIKEY");
		String uidActividad = sesion.getAplicacion().getUidActividad();
		ColectivosRequestRest consultaColectivos = new ColectivosRequestRest(apiKey, uidActividad);
		consultaColectivos.setPrivado("N");
		SpringContext.getBean(ConsultarTodosColectivosTask.class, new Object[] { consultaColectivos, new RestBackgroundTask.FailedCallback<List<ColectivoBean>>(){

			public void succeeded(List<ColectivoBean> result) {
				List<ColectivoAyudaGui> colectivos = result.stream().map(ColectivoAyudaGui::new).collect(Collectors.toList());
				String[] keySplit = key.split(";");
				setearLabelColectivo(colectivos, keySplit[0], keySplit[1], editable);
			}

			public void failed(Throwable throwable) {
				log.error("setearLabelColectivo() - failed() - No se han podido cargar los colectivos desde el backoffice");
			}
		}, getScene() }).start();
	}

	private void setearLabelColectivo(List<ColectivoAyudaGui> colectivos, String codColectivo, String desColectivo, boolean editable) {

		ColectivoAyudaGui colectivo = colectivos.stream().filter(el -> el.getCodColectivo().equals(codColectivo) && el.getDesColectivo().equals(desColectivo)).findAny().orElse(null);

		if (colectivo == null) {
			setearLabelColectivoPorDefecto(editable);
			log.error("setearLabelColectivo() - No se ha encontrado el colectivo para la variable con codigo: " + codColectivo + " y descripción: " + desColectivo);
			return;
		}

		log.debug("setearLabelColectivo() - Se ha encontrado el colectivo de fidelización para el registro para la " + "variable con codigo: " + codColectivo + " y descripción: " + desColectivo);

		tfCodColectivo.setText(colectivo.getCodColectivo());
		tfDesColectivo.setText(colectivo.getDesColectivo());
		tfDesColectivo.setDisable(true);
		tfCodColectivo.setDisable(true);
		btBuscarColectivo.setVisible(false);
		btLimpiarColectivo.setVisible(false);
	}

	private void setearLabelColectivoPorDefecto(boolean editable) {
		tfCodColectivo.clear();
		tfDesColectivo.clear();
		tfDesColectivo.setEditable(editable);
		tfCodColectivo.setEditable(editable);
		tfDesColectivo.setEditable(editable);
		btBuscarColectivo.setDisable(!editable);
		btLimpiarColectivo.setDisable(!editable);
	}

	private String variableColectivo() {
		return variablesService.getVariableAsString(COLECTIVO_DEFECTO);
	}

	@FXML
	protected void clickEmpresa() {
		cBoxEmpresa.setSelected(true);
		cBoxPersona.setSelected(false);
		cBoxFidelizado.setVisible(false);
		cBoxFidelizado.setSelected(false);
		lbFidelizado.setVisible(false);

		lbApellidos.setText(I18N.getTexto("Razón social"));

		esEmpresa = true;
		cargaTiposDocumento();

		if (MODO_EDICION.equals(getTabParentController().getModo())) {
			cbTipoDocumento.setValue(null);
		}
	}

	@Override
	public void cargaTiposDocumento() {
		String codPais = StringUtils.isNotBlank(tfCodPais.getText()) ? tfCodPais.getText() : sesion.getAplicacion().getTienda().getCliente().getPais();
		if (codPais != null) {
			try {
				PaisBean pais = paisService.consultarCodPais(codPais);
				tfDesPais.setText(pais.getDesPais());
				tiposIdent = tiposIdentService.consultarTiposIdent(esEmpresa, true, codPais);

				List<TipoIdentGui> tiposGui = new ArrayList<>();
				for (TiposIdentBean tipoIdent : tiposIdent) {
					TipoIdentGui tipoIdentGui = new TipoIdentGui(tipoIdent);
					tiposGui.add(tipoIdentGui);
				}

				tiposDocumento = FXCollections.observableArrayList(tiposGui);
				cbTipoDocumento.setItems(tiposDocumento);
			}
			catch (TiposIdentNotFoundException tiposIdentNotFoundException) {
				log.error("No se encontró los tipos de documento para el país.");
				tiposDocumento = FXCollections.observableArrayList(new ArrayList());
				cbTipoDocumento.setItems(tiposDocumento);
			}
			catch (TiposIdentServiceException tiposIdentServiceException) {
				log.error("Error buscando los tipos de documento para el país.");
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error en la búsqueda de tipos de documento"), getTabParentController().getStage());
			}
			catch (PaisNotFoundException | PaisServiceException paisNotFoundException) {
				log.error("Error buscando los paises.");
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error en la búsqueda, país no encontrado"), getTabParentController().getStage());
			}
		}
	}

	@Override
	protected void cargarDatosGenerales() {
		super.cargarDatosGenerales();

		// Se arregla carga de fechas en el text field del date picket
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		if (this.fidelizado.getFechaNacimiento() != null) {
			this.dpFechaNacimiento.getTextField().setText(df.format(this.fidelizado.getFechaNacimiento()));
		}

		if (this.fidelizado.getFechaAlta() != null) {
			this.dpFechaAlta.getTextField().setText(df.format(this.fidelizado.getFechaAlta()));
		}

		if (StringUtils.isBlank(fidelizado.getCodPais())) {
			codPais = sesion.getAplicacion().getTienda().getCliente().getCodpais();
			PaisBean pais = obtenerPais(codPais);
			tfCodPais.setText(pais.getCodPais());
			tfDesPais.setText(pais.getDesPais());
		}

		TiposContactoFidelizadoBean email = fidelizado.getTipoContacto("EMAIL");
		if (StringUtils.isNotBlank(email.getValor())) {
			tfEmailConfirm.setText(email.getValor());
		}

		// ISK-311 GAP 141 Actualización servicio NAV (nuevos campos)
		for (SolicitudBean solicitud : fidelizado.getLstSolicitudes()) {
			if ("RGPD".equalsIgnoreCase(solicitud.getTipoSolicitud())) {
				tfConsentimientoRGPD.setText(solicitud.getDesSolicitud());
				break;
			}
		}
		
		// ISK-313 GAP 141-b Idioma en alta y modificación fidelizados POS
		if (StringUtils.isNotBlank(fidelizado.getCodlengua())) {
			tfCodLenguaje.setText(fidelizado.getCodlengua());
			setDesLenguajeByCodLengua(fidelizado.getCodlengua(), true);
		}
		
		TiposContactoFidelizadoBean contactoTelefonoFijo = fidelizado.getTipoContacto("TELEFONO1");
		if (contactoTelefonoFijo != null && contactoTelefonoFijo.getEstadoBean() != Estado.BORRADO) {
			tfTelefonoFijo.setText(contactoTelefonoFijo.getValor());
		}
	}
	
	@FXML
	private void buscarDescLengua() {
		codLenguaje = tfCodLenguaje.getText();
		setDesLenguajeByCodLengua(null, false);
	}

	private void setDesLenguajeByCodLengua(String codLengua, boolean precargando) {
		try {
			if (StringUtils.isBlank(codLengua)) {
				codLengua = tfCodLenguaje.getText();
			}
			if (StringUtils.isNotBlank(codLengua)) {
				LenguajeBean lenguajeByCodLengua = lenguajesService.getLenguajeByCodLengua(codLengua);
				tfCodLenguaje.setText(codLengua);
				tfDesLenguaje.setText(lenguajeByCodLengua.getDeslengua());
			}
		} catch (LenguajeNotFoundException ex) {
			log.info("setDesLenguajeByCodLengua() - " + ex.getMessage());
			if (!precargando) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error en la búsqueda, idioma no encontrado"), getTabParentController().getStage());
			}
		} catch (Exception e) {
			if (!precargando) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error en la búsqueda del idioma del cliente"), getTabParentController().getStage());
			}
		}
	}

	@Override
	public void limpiarFormulario() {
		super.limpiarFormulario();
		this.cBoxEmpresa.setSelected(false);
		this.cBoxPersona.setSelected(false);
		this.cBoxFidelizado.setSelected(false);
		this.tfEmailConfirm.clear();
		this.tfConsentimientoRGPD.clear();
		this.tfCodLenguaje.clear();
		this.tfDesLenguaje.clear();
		this.tfTelefonoFijo.clear();
	}

	@FXML
	protected void clickPersona() {
		cBoxPersona.setSelected(true);
		cBoxEmpresa.setSelected(false);
		cBoxFidelizado.setVisible(true);
		lbFidelizado.setVisible(true);
		lbApellidos.setText(I18N.getTexto("Apellidos"));

		esEmpresa = false;
		cargaTiposDocumento();

		if (MODO_EDICION.equals(getTabParentController().getModo())) {
			cbTipoDocumento.setValue(null);
		}
	}

	@FXML
	protected void clickFidelizado() {

		if (cBoxFidelizado.isSelected()) {
			setearValoresColectivo(false);
		}
		else {
			setearLabelColectivoPorDefecto(false);
		}

	}

	protected boolean isFidelizado() {

		if (!fidelizado.getColectivos().isEmpty()) {

			for (ColectivosFidelizadoBean colectivo : fidelizado.getColectivos()) {
				String codcolectivos = this.variablesService.getVariableAsString(COLECTIVOS_FIDELIZADOS);
				if (StringUtils.isNotBlank(codcolectivos)) {
					List<String> colectivosList = Arrays.asList(codcolectivos.split(";"));
					if (colectivosList.contains(colectivo.getCodColectivo())) {
						tfCodColectivo.setText(colectivo.getCodColectivo());
						tfDesColectivo.setText(colectivo.getDesColectivo());
						return true;
					}
				}
			}
		}
		return false;
	}

	protected void cargarTipoIdentificacionFidelizado() {
		if (tiposIdent == null) {
			esEmpresa = null;
			cargaTiposDocumento();
		}

		TiposIdentBean tipoIdent = tiposIdent.stream().filter(el -> el.getCodTipoIden().equals(fidelizado.getCodTipoIden())).findFirst().orElse(null);
		if (tipoIdent != null) {
			if (tipoIdent.getEmpresa()) {
				clickEmpresa();
			}
			else {
				clickPersona();
			}
			cbTipoDocumento.setValue(new TipoIdentGui(tipoIdent));
		}
		else {
			clickPersona();
		}
	}
	
	// ISK-313 GAP 141-b Idioma en alta y modificación fidelizados POS
	@FXML
	public void accionBuscarLenguaje(){
		getTabParentController().getApplication().getMainView().showModalCentered(LenguajesView.class, getTabParentController().getDatos(), getTabParentController().getStage());

		if(getTabParentController().getDatos()!=null && getTabParentController().getDatos().containsKey(LenguajesController.PARAMETRO_SALIDA_LENGUAJE)){
			LenguajeBean lenguaje = (LenguajeBean)getTabParentController().getDatos().get(LenguajesController.PARAMETRO_SALIDA_LENGUAJE);
			tfDesLenguaje.setText(lenguaje.getDeslengua());
			tfCodLenguaje.setText(lenguaje.getCodlengua());
		}
	}
	
	@Override
	public void selected() {
		super.selected();
		
		// ISK-313 GAP 141-b Idioma en alta y modificación fidelizados POS		
		if(MODO_INSERCION.equals(getTabParentController().getModo())) {
			String codLenguaje = StringUtils.isNotBlank(sesion.getAplicacion().getStoreLanguageCode()) 
									? sesion.getAplicacion().getStoreLanguageCode()
									: sesion.getAplicacion().getTienda().getCliente().getCodlengua();
			setDesLenguajeByCodLengua(codLenguaje, true);
		}
	}

	@FXML
	@Override
	public void accionValidarDocumento() {

		if (cbTipoDocumento.getSelectionModel().getSelectedItem() == null) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe seleccionar un tipo de documento"), getTabParentController().getStage());
			return;
		}

		TipoIdentGui tipoIdentGui = cbTipoDocumento.getSelectionModel().getSelectedItem();
		String claseValidacion = tipoIdentGui.getClaseValidacion();

		// Compromprobamos que la clase de validación no sea nula, si lo es mostramos un mensaje de error
		if (StringUtils.isBlank(claseValidacion)) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se ha definido la clase de validación para el tipo de documento {0}", tipoIdentGui.getCodigo()), getTabParentController().getStage());
			return;
		}

		String mensaje;
		try {
			IValidadorDocumentoIdentificacion validadorDocumentoIdentificacion = (IValidadorDocumentoIdentificacion) Class.forName(claseValidacion).newInstance();
			if (validadorDocumentoIdentificacion.validarDocumentoIdentificacion(tfDocumento.getText())) {
				VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Documento valido"), getTabParentController().getStage());
			} else {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Documento no valido"), getTabParentController().getStage());
			}
		} catch (ClassCastException var4) {
			mensaje = I18N.getTexto("La clase {0} no implementa la interfaz {1}", new Object[]{claseValidacion, IValidadorDocumentoIdentificacion.class});
			VentanaDialogoComponent.crearVentanaError(mensaje, getTabParentController().getStage());
		} catch (ClassNotFoundException var5) {
			mensaje = I18N.getTexto("No se ha encontrado la clase {0}", claseValidacion);
			VentanaDialogoComponent.crearVentanaError(mensaje, getTabParentController().getStage());
		} catch (Exception var6) {
			Exception e = var6;
			mensaje = I18N.getTexto("Error validando el documento de identificación: ") + e.getMessage();
			VentanaDialogoComponent.crearVentanaError(mensaje, getTabParentController().getStage());
		}

	}

	public CheckBox getcBoxPersona() {
		return cBoxPersona;
	}

	public CheckBox getcBoxEmpresa() {
		return cBoxEmpresa;
	}

	public CheckBox getcBoxFidelizado() {
		return cBoxFidelizado;
	}

	public TextField getTfEmailConfirm() {
		return tfEmailConfirm;
	}

	public TextField getTfConsentimientoRGPD() {
		return tfConsentimientoRGPD;
	}

	public void setTfConsentimientoRGPD(TextField tfConsentimientoRGPD) {
		this.tfConsentimientoRGPD = tfConsentimientoRGPD;
	}

	public TextField getTfCodLenguaje() {
		return tfCodLenguaje;
	}

	public void setTfCodLenguaje(TextField tfCodLenguaje) {
		this.tfCodLenguaje = tfCodLenguaje;
	}

	public TextField getTfDesLenguaje() {
		return tfDesLenguaje;
	}

	public void setTfDesLenguaje(TextField tfDesLenguaje) {
		this.tfDesLenguaje = tfDesLenguaje;
	}

	public Button getBtBuscarLenguaje() {
		return btBuscarLenguaje;
	}

	public void setBtBuscarLenguaje(Button btBuscarLenguaje) {
		this.btBuscarLenguaje = btBuscarLenguaje;
	}

	public TextField getTfTelefonoFijo() {
		return tfTelefonoFijo;
	}

	public void setTfTelefonoFijo(TextField tfTelefonoFijo) {
		this.tfTelefonoFijo = tfTelefonoFijo;
	}
	
}
