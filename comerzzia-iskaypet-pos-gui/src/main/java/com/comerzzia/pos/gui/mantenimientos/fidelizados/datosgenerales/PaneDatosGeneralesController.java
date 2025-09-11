package com.comerzzia.pos.gui.mantenimientos.fidelizados.datosgenerales;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.api.model.core.TiendaBean;
import com.comerzzia.api.model.loyalty.EstadoCivilBean;
import com.comerzzia.api.model.loyalty.FidelizadoBean;
import com.comerzzia.api.model.loyalty.TiposContactoFidelizadoBean;
import com.comerzzia.api.rest.client.fidelizados.ConsultarFidelizadoRequestRest;
import com.comerzzia.api.rest.client.request.RequestRest;
import com.comerzzia.core.util.base.Estado;
import com.comerzzia.core.util.tipoidentificacion.IValidadorDocumentoIdentificacion;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import com.comerzzia.pos.core.gui.componentes.datepicker.DatePicker;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.TabController;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.ConsultarEstadosCivilesTask;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.ConsultarTiendaFavoritaFidelizadoTask;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.EstadoCivilGui;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.FidelizadoController;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.SexoGui;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.TipoIdentGui;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.colectivos.ColectivoAyudaGui;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.colectivos.ColectivoController;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.colectivos.ColectivoView;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.tiendas.TiendaController;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.tiendas.TiendaGui;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.tiendas.TiendaView;
import com.comerzzia.pos.gui.ventas.tickets.factura.paises.PaisesController;
import com.comerzzia.pos.gui.ventas.tickets.factura.paises.PaisesView;
import com.comerzzia.pos.persistence.paises.PaisBean;
import com.comerzzia.pos.persistence.tiposIdent.TiposIdentBean;
import com.comerzzia.pos.services.core.paises.PaisNotFoundException;
import com.comerzzia.pos.services.core.paises.PaisService;
import com.comerzzia.pos.services.core.paises.PaisServiceException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentNotFoundException;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentService;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentServiceException;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

@Component
public class PaneDatosGeneralesController extends TabController<FidelizadoController>{

	protected static final Logger log = Logger.getLogger(PaneDatosGeneralesController.class);
	
	@Autowired
	private Sesion sesion;
	
	@Autowired
    protected VariablesServices variablesService;
	
	@Autowired
	private PaisService paisService = SpringContext.getBean(PaisService.class);
	
	@Autowired
	private TiposIdentService tiposIdentService = SpringContext.getBean(TiposIdentService.class);
	
	@FXML
	protected TextField tfCodigo, tfNumeroTarjeta, tfNombre, tfApellidos, tfDocumento, tfEmail, tfMovil, tfCodPostal;
	
	@FXML
	protected TextField tfProvincia, tfPoblacion, tfLocalidad, tfCodPais, tfDesPais, tfDomicilio, tfCodTienda, tfDesTienda, tfCodColectivo, tfDesColectivo;
	
	@FXML
	protected CheckBox chNotifEmail, chNotifMovil, chPaperLess;
	
	@FXML
	protected Button btBuscarPais, btBuscarTienda, btBuscarColectivo, btLimpiarTienda, btLimpiarColectivo, btnValidarDocumento;
  	
	@FXML
	protected ComboBox<TipoIdentGui> cbTipoDocumento;
	
	protected ObservableList<TipoIdentGui> tiposDocumento;
	
	@FXML
	protected ComboBox<SexoGui> cbSexo;
	
	@FXML
	protected ComboBox<EstadoCivilGui> cbEstadoCivil;
	
	protected ObservableList<EstadoCivilGui> estadosCiviles;
	
	@FXML
	protected DatePicker dpFechaNacimiento, dpFechaAlta;
	
	@FXML
	protected Label lbNumeroTarjeta, lbColectivo, lbFechaAlta;
	
	protected FidelizadoBean fidelizado;
	
	protected TiendaBean tiendaFavorita;
	
	protected String codPais;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ObservableList<SexoGui> sexos = FXCollections.observableArrayList();
		SexoGui hombre = new SexoGui("H", "HOMBRE");
		SexoGui mujer = new SexoGui("M", "MUJER");
		sexos.add(mujer);
		sexos.add(hombre);
		cbSexo.setItems(sexos);
		estadosCiviles = FXCollections.observableArrayList();
		cbEstadoCivil.setItems(estadosCiviles);
		tiposDocumento = FXCollections.observableArrayList();
		cbTipoDocumento.setItems(tiposDocumento);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		dpFechaAlta.setDateFormat(df);
		
		String apiKey = variablesService.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
		String uidActividad = sesion.getAplicacion().getUidActividad();
		RequestRest consulta = new RequestRest(apiKey, uidActividad);
		consulta.setLanguageCode(sesion.getAplicacion().getStoreLanguageCode());
		ConsultarEstadosCivilesTask consultarEstadosCivilesTask = SpringContext.getBean(ConsultarEstadosCivilesTask.class, 
				consulta, 
				new RestBackgroundTask.FailedCallback<List<EstadoCivilBean>>() {
					
					@Override
					public void failed(Throwable throwable) {
						getTabParentController().getApplication().getMainView().close();
					}
					
					@Override
					public void succeeded(List<EstadoCivilBean> result) {
						List<EstadoCivilGui> estados = new ArrayList<EstadoCivilGui>();
						for(EstadoCivilBean estado : result){
							EstadoCivilGui estGui = new EstadoCivilGui(estado);
							estados.add(estGui);
						}
						estadosCiviles = FXCollections.observableArrayList(estados);
						cbEstadoCivil.setItems(estadosCiviles);
						
					}
				}, getScene());
	consultarEstadosCivilesTask.start();
	
	tfCodPais.focusedProperty().addListener(new ChangeListener<Boolean>() {

		@Override
		public void changed(ObservableValue<? extends Boolean> ov,
				Boolean oldValue, Boolean newValue) {
			tfCodPais.setText(tfCodPais.getText().toUpperCase());
			if(!newValue && (codPais == null || !codPais.equals(tfCodPais.getText()))){
				codPais = tfCodPais.getText();
				cargaTiposDocumento();
			}
		}
	});
	
	tfCodTienda.focusedProperty().addListener(new ChangeListener<Boolean>(){
		@Override
		public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
			tfCodTienda.setText(tfCodTienda.getText().toUpperCase());
			if (!newValue) {
				accionCargarTienda();
			}
		}
	 });
	
	tfCodColectivo.focusedProperty().addListener(new ChangeListener<Boolean>(){
		@Override
		public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
			tfCodColectivo.setText(tfCodColectivo.getText().toUpperCase());
			if (!newValue) {
				accionCargarColectivo();
			}
		}
	 });
	}
	
	

	public void selected(){
		FidelizadoBean newFidelizado = getTabParentController().getFidelizado();
		if(newFidelizado != null && (fidelizado == null || !fidelizado.getIdFidelizado().equals(newFidelizado.getIdFidelizado()))){
			limpiarFormulario();
			cargarTiendaFavorita(newFidelizado);
		}
		
		fidelizado = newFidelizado;
		String codPaisTxt = tfCodPais.getText();
		if(codPaisTxt == null || codPaisTxt.isEmpty()){
			String newCodPais = fidelizado != null ? fidelizado.getCodPais() : sesion.getAplicacion().getTienda().getCliente().getCodpais();
			if(codPais == null || !codPais.equals(newCodPais)){
				codPais = newCodPais;
				PaisBean pais = obtenerPais(codPais);
				tfCodPais.setText(pais.getCodPais());
				tfDesPais.setText(pais.getDesPais());
				List<TiposIdentBean> tiposIden = cargarTiposIdent(codPais);
				List<TipoIdentGui> tiposDoc = new ArrayList<TipoIdentGui>();
				for(TiposIdentBean tipoIden : tiposIden){
					TipoIdentGui tipoGui = new TipoIdentGui(tipoIden);
					tiposDoc.add(tipoGui);
				}
				tiposDocumento = FXCollections.observableArrayList(tiposDoc);
				cbTipoDocumento.setItems(tiposDocumento);
			}
		}
		
		
		if(!"INSERCION".equals(getTabParentController().getModo())){
			cargarDatosGenerales();
		}
		if("CONSULTA".equals(getTabParentController().getModo())){
			camposEditables(false);
		}else{
			camposEditables(true);
		}

		
	}
	
	protected void cargarTiendaFavorita(final FidelizadoBean fidelizado){
		
			
		String apiKey = variablesService.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
		String uidActividad = sesion.getAplicacion().getUidActividad();
		
			ConsultarFidelizadoRequestRest consultaTienda = new ConsultarFidelizadoRequestRest(apiKey, uidActividad);
			consultaTienda.setIdFidelizado(String.valueOf(fidelizado.getIdFidelizado()));
			
			ConsultarTiendaFavoritaFidelizadoTask consultarTiendaFavoritaTask = SpringContext.getBean(ConsultarTiendaFavoritaFidelizadoTask.class, 
					consultaTienda, 
					new RestBackgroundTask.FailedCallback<TiendaBean>() {
						@Override
						public void succeeded(TiendaBean result) {						
							tiendaFavorita = result;	
							if(tiendaFavorita.getCodAlm() != null){
								fidelizado.setCodAlm(tiendaFavorita.getCodAlm());
							}
							cargarDatosGenerales();
						}
						@Override
						public void failed(Throwable throwable) {
							getTabParentController().getApplication().getMainView().close();
						}
					}, getTabParentController().getStage());
			consultarTiendaFavoritaTask.start();
		
	}
	
	public void limpiarFormulario(){
		tfCodigo.clear();
		tfNumeroTarjeta.clear();
		tfNombre.clear();
		tfApellidos.clear();
		tfDocumento.clear();
		tfEmail.clear();
		tfMovil.clear();
		tfCodPostal.clear();
		tfProvincia.clear();
		tfPoblacion.clear();
		tfLocalidad.clear();
		tfCodPais.clear();
		tfDesPais.clear();
		String newCodPais = sesion.getAplicacion().getTienda().getCliente().getCodpais();
		if(newCodPais != null){
			codPais = newCodPais;
			PaisBean pais = obtenerPais(codPais);
			tfCodPais.setText(pais.getCodPais());
			tfDesPais.setText(pais.getDesPais());
			List<TiposIdentBean> tiposIden = cargarTiposIdent(codPais);
			List<TipoIdentGui> tiposDoc = new ArrayList<TipoIdentGui>();
			for(TiposIdentBean tipoIden : tiposIden){
				TipoIdentGui tipoGui = new TipoIdentGui(tipoIden);
				tiposDoc.add(tipoGui);
			}
			tiposDocumento = FXCollections.observableArrayList(tiposDoc);
			cbTipoDocumento.setItems(tiposDocumento);
		}
		tfDomicilio.clear();
		tfCodTienda.clear();
		tfDesTienda.clear();
		tfCodColectivo.clear();
		tfDesColectivo.clear();
		dpFechaNacimiento.clear();
		dpFechaAlta.clear();
		cbEstadoCivil.setValue(null);
		cbSexo.setValue(null);
		cbTipoDocumento.setValue(null);
		chNotifEmail.setSelected(false);
		chNotifMovil.setSelected(false);
		chPaperLess.setSelected(false);
	}
	
	
	
	protected void cargarDatosGenerales(){
		log.debug("cargarDatosGenerales()");		
			tfCodigo.setText(fidelizado.getCodFidelizado());
			tfNumeroTarjeta.setText(fidelizado.getNumeroTarjeta());
			tfNombre.setText(fidelizado.getNombre());
			tfApellidos.setText(fidelizado.getApellidos());
			seleccionaEstadoCivil(fidelizado.getCodEstCivil());
			seleccionaSexo(fidelizado.getSexo());
			seleccionaTipoIdent(fidelizado.getCodTipoIden());
			dpFechaNacimiento.setValue(fidelizado.getFechaNacimiento());
			dpFechaAlta.setValue(fidelizado.getFechaAlta());
			TiposContactoFidelizadoBean email = fidelizado.getTipoContacto("EMAIL");
			TiposContactoFidelizadoBean movil = fidelizado.getTipoContacto("MOVIL");
			if("CONSULTA".equals(getTabParentController().getModo()) && !isPuedeVerDatosSensibles()){
				if(email != null && email.getEstadoBean() != Estado.BORRADO){
					chNotifEmail.setSelected(email.getRecibeNotificaciones());
					ocultaDatoSensible(tfEmail,email.getValor());
				}
				ocultaDatoSensible(tfDocumento,fidelizado.getDocumento());
				ocultaDatoSensible(tfDomicilio,fidelizado.getDomicilio());
				if(movil != null && movil.getEstadoBean() != Estado.BORRADO){
					chNotifMovil.setSelected(movil.getRecibeNotificaciones());
					ocultaDatoSensible(tfMovil,movil.getValor());
				}
			}else{
				tfDocumento.setText(fidelizado.getDocumento());
				if(email != null && email.getEstadoBean() != Estado.BORRADO){
					chNotifEmail.setSelected(email.getRecibeNotificaciones());
					tfEmail.setText(email.getValor());
				}
				if(movil != null && movil.getEstadoBean() != Estado.BORRADO){
					chNotifMovil.setSelected(movil.getRecibeNotificaciones());
					tfMovil.setText(movil.getValor());
				}
				tfDomicilio.setText(fidelizado.getDomicilio());
			}
			tfCodPostal.setText(fidelizado.getCp());
			tfProvincia.setText(fidelizado.getProvincia());
			tfPoblacion.setText(fidelizado.getPoblacion());
			tfLocalidad.setText(fidelizado.getLocalidad());
			tfCodPais.setText(fidelizado.getCodPais());
			tfDesPais.setText(fidelizado.getDesPais());
			if(tiendaFavorita != null){
				tfCodTienda.setText(tiendaFavorita.getCodAlm());
				tfDesTienda.setText(tiendaFavorita.getDesAlm());
			}
			if(fidelizado.getPaperLess() != null) {
				chPaperLess.setSelected(fidelizado.getPaperLess());
			}else {
				chPaperLess.setSelected(false);
			}
			
	}
	
	protected void ocultaDatoSensible(TextField textField, String string) {
		String sustituir = string.substring(1, string.length()-1);
		String car = sustituir.replaceAll(".", "*");
		textField.setText(string.replace(sustituir, car));
	}
	@FXML
	public void accionBuscarPais(){
		getTabParentController().getApplication().getMainView().showModalCentered(PaisesView.class, getTabParentController().getDatos(), getTabParentController().getStage());

		if(getTabParentController().getDatos()!=null && getTabParentController().getDatos().containsKey(PaisesController.PARAMETRO_SALIDA_PAIS)){
			PaisBean pais = (PaisBean)getTabParentController().getDatos().get(PaisesController.PARAMETRO_SALIDA_PAIS);
			tfDesPais.setText(pais.getDesPais());
			tfCodPais.setText(pais.getCodPais());
			codPais = pais.getCodPais().toUpperCase();
			cargaTiposDocumento();
		}
	}
	
	@FXML
	public void accionBuscarColectivo(){
		getTabParentController().getDatos().put("colectivos", getTabParentController().getTodosColectivos());
		getTabParentController().getApplication().getMainView().showModalCentered(ColectivoView.class, getTabParentController().getDatos(), getTabParentController().getStage());

		if(getTabParentController().getDatos()!=null && getTabParentController().getDatos().containsKey(ColectivoController.PARAMETRO_SALIDA_COLECTIVO)){
			ColectivoAyudaGui colectivo = (ColectivoAyudaGui) getTabParentController().getDatos().get(ColectivoController.PARAMETRO_SALIDA_COLECTIVO);
			tfDesColectivo.setText(colectivo.getDesColectivo());
			tfCodColectivo.setText(colectivo.getCodColectivo());
		}
	}
	
	@FXML
	public void accionBuscarTienda(){
		getTabParentController().getDatos().put("tiendas", getTabParentController().getTodasTiendas());
		getTabParentController().getApplication().getMainView().showModalCentered(TiendaView.class, getTabParentController().getDatos(), getTabParentController().getStage());

		if(getTabParentController().getDatos()!=null && getTabParentController().getDatos().containsKey(TiendaController.PARAMETRO_SALIDA_TIENDA)){
			TiendaGui tienda = (TiendaGui) getTabParentController().getDatos().get(TiendaController.PARAMETRO_SALIDA_TIENDA);
			tfDesTienda.setText(tienda.getDesTienda());
			tfCodTienda.setText(tienda.getCodTienda());
		}
	}
	
	public void camposEditables(boolean editable){
		tfNumeroTarjeta.setEditable(editable);
		tfNombre.setEditable(editable);
		tfApellidos.setEditable(editable);
		tfDocumento.setEditable(editable);
		cbEstadoCivil.setDisable(!editable);
		cbSexo.setDisable(!editable);
		cbTipoDocumento.setDisable(!editable);
		dpFechaNacimiento.setDisable(!editable);
		dpFechaAlta.setDisable(true);
		chNotifEmail.setDisable(!editable);
		tfEmail.setEditable(editable);
		chNotifMovil.setDisable(!editable);
		tfMovil.setEditable(editable);
		tfCodPostal.setEditable(editable);
		tfProvincia.setEditable(editable);
		tfPoblacion.setEditable(editable);
		tfLocalidad.setEditable(editable);
		tfCodPais.setEditable(editable);
		tfDesPais.setEditable(editable);
		tfDomicilio.setEditable(editable);
		tfCodTienda.setEditable(editable);
		tfDesTienda.setEditable(editable);
		tfCodColectivo.setEditable(editable);
		tfDesColectivo.setEditable(editable);		
		btBuscarColectivo.setDisable(!editable);
		btBuscarPais.setDisable(!editable);
		btBuscarTienda.setDisable(!editable);
		btLimpiarColectivo.setDisable(!editable);
		btLimpiarTienda.setDisable(!editable);
		chPaperLess.setDisable(!editable);
		if("INSERCION".equals(getTabParentController().getModo())){
			tfCodigo.setEditable(true);
			tfNumeroTarjeta.setVisible(true);
			tfCodColectivo.setVisible(true);
			tfDesColectivo.setVisible(true);
			btBuscarColectivo.setVisible(true);
			btLimpiarColectivo.setVisible(true);
			lbNumeroTarjeta.setVisible(true);
			lbColectivo.setVisible(true);
			lbFechaAlta.setVisible(false);
			dpFechaAlta.setVisible(false);
		}else{
			tfCodigo.setEditable(false);
			tfNumeroTarjeta.setVisible(false);
			tfCodColectivo.setVisible(false);
			tfDesColectivo.setVisible(false);
			btBuscarColectivo.setVisible(false);
			btLimpiarColectivo.setVisible(false);
			lbNumeroTarjeta.setVisible(false);
			lbColectivo.setVisible(false);
			lbFechaAlta.setVisible(true);
			dpFechaAlta.setVisible(true);
		}
	}
	
	protected void seleccionaSexo(String codSexo) {
		List<SexoGui> sexos = cbSexo.getItems();
		for (SexoGui sexo : sexos) {
			if (sexo.getCodigo().equals(codSexo)) {
				cbSexo.getSelectionModel().select(sexo);
				break;
			}
		}

	}
	
	protected void seleccionaEstadoCivil(String codEstadoCivil) {
		for (EstadoCivilGui estadoCivil : estadosCiviles) {
			if (estadoCivil.getCodEstadoCivil().equals(codEstadoCivil)) {
				cbEstadoCivil.getSelectionModel().select(estadoCivil);
				break;
			}
		}
	}
	
	protected void seleccionaTipoIdent(String codTipoIdent) {
		for (TipoIdentGui tipoIdent : tiposDocumento) {
			if (tipoIdent.getCodigo().equals(codTipoIdent)) {
				cbTipoDocumento.getSelectionModel().select(tipoIdent);
				break;
			}
		}
	}
	
	protected PaisBean obtenerPais(String codPais){
		PaisBean pais = null;

		try {
			pais = paisService.consultarCodPais(codPais);
		}
		catch (PaisNotFoundException ex) {
			log.error("No se encontró el país.");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error en la búsqueda, país no encontrado"), getTabParentController().getStage());
		}
		catch (PaisServiceException ex) {
			log.error("Error buscando el país.", ex);
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error en la búsqueda del país del cliente."), getTabParentController().getStage());
		}

		return pais;
	}
	
	protected List<TiposIdentBean> cargarTiposIdent(String codPais){
		List<TiposIdentBean> tiposIdent = new ArrayList<TiposIdentBean>();
		if(codPais != null){			
			try {
				tiposIdent = tiposIdentService.consultarTiposIdent(null, true, codPais.toUpperCase());
			} catch (TiposIdentNotFoundException e) {
				log.error("No se encontró los tipos de documento para el país.");
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error en la búsqueda, tipos de documento no encontrado"), getTabParentController().getStage());
			} catch (TiposIdentServiceException e) {
				log.error("Error buscando los tipos de documento para el país.");
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error en la búsqueda, tipos de documento no encontrado"), getTabParentController().getStage());
			}
		}
		return tiposIdent;
	}
	
	@FXML
	public void cargaTiposDocumento(){
		List<TiposIdentBean> tiposIdent = new ArrayList<TiposIdentBean>();
		String codPais = tfCodPais.getText();
		if(codPais != null){			
			try {
				PaisBean pais = paisService.consultarCodPais(codPais);
				tfDesPais.setText(pais.getDesPais());
				tiposIdent = tiposIdentService.consultarTiposIdent(null, true, codPais);
				List<TipoIdentGui> tiposGui = new ArrayList<TipoIdentGui>();
				for(TiposIdentBean tipoIdent : tiposIdent){
					TipoIdentGui tipoIdentGui = new TipoIdentGui(tipoIdent);
					tiposGui.add(tipoIdentGui);
				}
				tiposDocumento = FXCollections.observableArrayList(tiposGui);
				cbTipoDocumento.setItems(tiposDocumento);
			} catch (TiposIdentNotFoundException e) {
				log.error("No se encontró los tipos de documento para el país.");
				tiposDocumento = FXCollections.observableArrayList(new ArrayList<TipoIdentGui>());
				cbTipoDocumento.setItems(tiposDocumento);
			} catch (TiposIdentServiceException e) {
				log.error("Error buscando los tipos de documento para el país.");
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error en la búsqueda de tipos de documento"), getTabParentController().getStage());
			} catch (PaisNotFoundException e) {
				log.error("Error buscando los paises.");
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error en la búsqueda, país no encontrado"), getTabParentController().getStage());
			} catch (PaisServiceException e) {
				log.error("Error buscando los paises.");
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error en la búsqueda, país no encontrado"), getTabParentController().getStage());
			}
		}
		
	}
	
	@FXML
	public void accionLimpiarTienda(){
		tfCodTienda.clear();
		tfDesTienda.clear();
	}
	
	@FXML
	public void accionLimpiarColectivo(){
		tfCodColectivo.clear();
		tfDesColectivo.clear();
	}
	
	@FXML
	public void accionCargarTienda(){
		String codTienda = tfCodTienda.getText();
		if(StringUtils.isNotBlank(codTienda)){
			boolean valido = false;
			for(TiendaGui tienda : getTabParentController().getTodasTiendas()){
				if(tienda.getCodTienda().equals(codTienda)){
					tfCodTienda.setText(tienda.getCodTienda());
					tfDesTienda.setText(tienda.getDesTienda());
					valido = true;
					break;
				}
			}
			if(!valido){
				tfCodTienda.clear();
				tfDesTienda.clear();
			}
		}
	}
	
	@FXML
	public void accionCargarColectivo(){
		String codColectivo = tfCodColectivo.getText();
		if(StringUtils.isNotBlank(codColectivo)){
			boolean valido = false;
			for(ColectivoAyudaGui colectivo : getTabParentController().getTodosColectivos()){
				if(colectivo.getCodColectivo().equals(codColectivo)){
					tfCodColectivo.setText(colectivo.getCodColectivo());
					tfDesColectivo.setText(colectivo.getDesColectivo());
					valido = true;
					break;
				}
			}
			if(!valido){
				tfCodColectivo.clear();
				tfDesColectivo.clear();
			}
		}
	}
	
	  
	@FXML
 	public void accionValidarDocumento() {
		if (cbTipoDocumento.getSelectionModel().getSelectedItem() != null) {
			
			String claseValidacion = cbTipoDocumento.getSelectionModel().getSelectedItem().getClaseValidacion();
			try{
				IValidadorDocumentoIdentificacion validadorDocumentoIdentificacion = (IValidadorDocumentoIdentificacion) Class.forName(claseValidacion).newInstance();
				
				if(validadorDocumentoIdentificacion.validarDocumentoIdentificacion(tfDocumento.getText())){
					VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Documento valido"), getTabParentController().getStage());
				}else{
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Documento no valido"), getTabParentController().getStage());
				}
			}
			catch(ClassCastException e) {
				String mensaje = I18N.getTexto("La clase {0} no implementa la interfaz {1}", claseValidacion, IValidadorDocumentoIdentificacion.class);
				VentanaDialogoComponent.crearVentanaError(mensaje, getTabParentController().getStage());
			}
			catch(ClassNotFoundException e) {
				String mensaje = I18N.getTexto("No se ha encontrado la clase {0}", claseValidacion);
				VentanaDialogoComponent.crearVentanaError(mensaje, getTabParentController().getStage());
			}
			catch(Exception e) {
				String mensaje = I18N.getTexto("Error validando el documento de identificación: ") + e.getMessage();
				VentanaDialogoComponent.crearVentanaError(mensaje, getTabParentController().getStage());
			}	
		}
	
 	}

	public TextField getTfCodigo() {
		return tfCodigo;
	}

	public TextField getTfNumeroTarjeta() {
		return tfNumeroTarjeta;
	}

	public TextField getTfNombre() {
		return tfNombre;
	}

	public TextField getTfApellidos() {
		return tfApellidos;
	}

	public TextField getTfDocumento() {
		return tfDocumento;
	}

	public TextField getTfEmail() {
		return tfEmail;
	}

	public TextField getTfMovil() {
		return tfMovil;
	}

	public TextField getTfCodPostal() {
		return tfCodPostal;
	}

	public TextField getTfProvincia() {
		return tfProvincia;
	}

	public TextField getTfPoblacion() {
		return tfPoblacion;
	}
	
	public TextField getTfLocalidad(){
		return tfLocalidad;
	}

	public TextField getTfCodPais() {
		return tfCodPais;
	}

	public TextField getTfDesPais() {
		return tfDesPais;
	}

	public TextField getTfDomicilio() {
		return tfDomicilio;
	}

	public TextField getTfCodTienda() {
		return tfCodTienda;
	}

	public TextField getTfDesTienda() {
		return tfDesTienda;
	}

	public TextField getTfCodColectivo() {
		return tfCodColectivo;
	}

	public TextField getTfDesColectivo() {
		return tfDesColectivo;
	}

	public CheckBox getChNotifEmail() {
		return chNotifEmail;
	}

	public CheckBox getChNotifMovil() {
		return chNotifMovil;
	}

	public ComboBox<TipoIdentGui> getCbTipoDocumento() {
		return cbTipoDocumento;
	}

	public ComboBox<SexoGui> getCbSexo() {
		return cbSexo;
	}

	public ComboBox<EstadoCivilGui> getCbEstadoCivil() {
		return cbEstadoCivil;
	}

	public DatePicker getDpFechaNacimiento() {
		return dpFechaNacimiento;
	}



	public FidelizadoBean getFidelizado() {
		return fidelizado;
	}



	public void setFidelizado(FidelizadoBean fidelizado) {
		this.fidelizado = fidelizado;
	}
	
	public boolean isPuedeVerDatosSensibles() {
		return getTabParentController().isPuedeVerDatosSensibles();
	}
	
	public CheckBox getChPaperLess() {
		return chPaperLess;
	}
}
