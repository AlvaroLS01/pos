package com.comerzzia.pos.core.gui.configuration.pos;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.omnichannel.facade.model.store.StorePos;
import com.comerzzia.omnichannel.facade.service.store.StorePosServiceFacade;
import com.comerzzia.pos.core.devices.AvailableDevicesConfiguration;
import com.comerzzia.pos.core.devices.DeviceType;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.configuration.DeviceModelConfiguration;
import com.comerzzia.pos.core.devices.device.Device;
import com.comerzzia.pos.core.devices.device.DeviceBuilder;
import com.comerzzia.pos.core.devices.device.DeviceConfigKey;
import com.comerzzia.pos.core.devices.device.DeviceException;
import com.comerzzia.pos.core.devices.device.hashcontrolled.HashControlledDevice;
import com.comerzzia.pos.core.devices.device.linedisplay.DeviceLineDisplay;
import com.comerzzia.pos.core.devices.device.scale.DeviceScale;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.controllers.ActionSceneController;
import com.comerzzia.pos.core.gui.controllers.CzzActionScene;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

@Component
@CzzActionScene
public class PosConfigurationController extends ActionSceneController implements Initializable {
    
    // log
    private static final Logger log = Logger.getLogger(PosConfigurationController.class.getName());
    protected String noUseVariable;
    
	protected static final String OPTION_GENERAL = "general";
	protected static final String TOGGLE_BTN_PREFIX_ID = "tgBtDevice";

    // Componentes de ventana
    @FXML
    protected ComboBox<String> cbConnMethods, cbDesConfig;
    
    @FXML
    protected TextArea taConfig;
    
    @FXML
    protected Label lbDesConfig, lbConnMethod;
    
    @FXML
    protected VBox vbButtons, vbAdditionalData;
    
    @FXML
    protected TextField tfStore, tfTill;
    
    @FXML
	protected ToggleButton tgBtGeneral;

	@FXML
	protected Button btAccept, btCancel, btEditConfiguration;
    
    protected String selectedOption;
	protected String selectedOptionType;

	protected HashMap<String, DeviceConfiguration> provisionalConfigurations;
    
    @Autowired
    private Session session;
    
    @Autowired
    private StorePosServiceFacade storePosService;
    
    protected ChangeListener<String> cbDesConfigListener = new ChangeListener<String>() {

		@Override
		public void changed(ObservableValue<? extends String> paramObservableValue, String oldValue, String newValue) {
			showConnMethodCBOptions();
			cbConnMethods.getSelectionModel().selectFirst();
		}
	};
    protected ChangeListener<String> cbConnMethodListener =new ChangeListener<String>() {

		@Override
		public void changed(ObservableValue<? extends String> paramObservableValue, String oldValue, String newValue) {
			showConnMethodConfigs();
		}
	};
	
    final DeviceLineDisplay visor = Devices.getInstance().getLineDisplay();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    @Override
    public void onSceneOpen() {
        visor.clear();
    	
        lbConnMethod.setText(I18N.getText("Caja configurada"));
        lbDesConfig.setText(I18N.getText("Tienda"));
        noUseVariable = I18N.getText("NO USA");
        
        //Muestra la configuración general por defecto
        tfStore.setText(session.getApplicationSession().getStorePosBusinessData().getWarehouse().getWhDes());
        tfTill.setText(session.getApplicationSession().getTillCode());
        showComboBox(false);
        taConfig.setVisible(false);
        // Deshabiliramos el foco para que no se quede cogido
        taConfig.setFocusTraversable(false);
        
		selectedOption = OPTION_GENERAL;
		tgBtGeneral.setSelected(true);
        
        btEditConfiguration.setVisible(false);
        
        vbAdditionalData.setVisible(false);
        vbAdditionalData.setManaged(false);
    }
    
    @Override
    public void initializeFocus() {
        tgBtGeneral.requestFocus();
    }
    
    @Override
    public void initializeComponents() {
        log.debug("inicializarComponentes() - Inicializando componentes");
        
        cbDesConfig.setItems(FXCollections.<String>observableArrayList());
        cbDesConfig.getSelectionModel().selectedItemProperty().addListener(cbDesConfigListener);   
        cbConnMethods.getSelectionModel().selectedItemProperty().addListener(cbConnMethodListener);

		provisionalConfigurations = new HashMap<>();
		loadDefaultProvisionalConfigurations();

		loadDeviceButtons();
		hideButtons();
    }
    
	protected void loadDefaultProvisionalConfigurations() {
		provisionalConfigurations.put(OPTION_GENERAL, new DeviceConfiguration());

		AvailableDevicesConfiguration availableDevicesConfig = Devices.getInstance().getAvailableDevicesConfiguration();
		for (DeviceType deviceType : availableDevicesConfig.getDevicesType().values()) {
			for (DeviceConfigKey configKey : deviceType.getConfigurationTags()) {
				provisionalConfigurations.put(configKey.getKey(), new DeviceConfiguration(configKey.getKey()));
			}
		}
	}

	protected void loadDeviceButtons() {
		// Prevent not button selected in toggle group
		tgBtGeneral.getToggleGroup().selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
			if (newVal == null)
				oldVal.setSelected(true);
		});
		
		AvailableDevicesConfiguration availableDevicesConfig = Devices.getInstance().getAvailableDevicesConfiguration();

		for (DeviceType deviceType : availableDevicesConfig.getDevicesType().values()) {

			for (DeviceConfigKey configKey : deviceType.getConfigurationTags()) {

				ToggleButton deviceTgBtn = new ToggleButton(configKey.getLabel());
				deviceTgBtn.setId(TOGGLE_BTN_PREFIX_ID + StringUtils.capitalize(configKey.getKey()));
				deviceTgBtn.setToggleGroup(tgBtGeneral.getToggleGroup());
				deviceTgBtn.setOnAction((ActionEvent event) -> actionBtDevice(deviceTgBtn, deviceType, configKey));
				deviceTgBtn.setPrefWidth(tgBtGeneral.getPrefWidth());
				deviceTgBtn.setPrefHeight(tgBtGeneral.getPrefHeight());
				deviceTgBtn.getStyleClass().addAll(tgBtGeneral.getStyleClass());

				vbButtons.getChildren().add(deviceTgBtn);
			}
		}
	}

	protected void hideButtons() {
		ListIterator<Node> iterator = vbButtons.getChildren().listIterator();
		while (iterator.hasNext()) {
			ToggleButton boton = (ToggleButton) iterator.next();
			if (AppConfig.getCurrentConfiguration().getHiddeButtons().contains(boton.getId())) {
				iterator.remove();
			}
		}
	}
    
    @FXML
    public void actionBtGeneral() {
        log.debug("actionBtGeneral()");
        
        btEditConfiguration.setVisible(false);
        
        lbConnMethod.setText(I18N.getText("Caja configurada"));
        lbDesConfig.setText(I18N.getText("Tienda"));
        taConfig.setVisible(false);
        showComboBox(false);
        
		selectedOption = OPTION_GENERAL;
		selectedOptionType = null;
        tfStore.setText(session.getApplicationSession().getStorePosBusinessData().getWarehouse().getWhDes());
        tfTill.setText(session.getApplicationSession().getTillCode());
        taConfig.setText("");
        vbAdditionalData.setVisible(false);
        vbAdditionalData.setManaged(false);
    }
    
	public void actionBtDevice(ToggleButton deviceBtn, DeviceType deviceType, DeviceConfigKey deviceConfigKey) {
		log.debug("actionBtDevice() - Device: " + deviceConfigKey.getLabel());

		btEditConfiguration.setVisible(false);

		lbConnMethod.setText(I18N.getText("Método de conexión"));
		lbDesConfig.setText(I18N.getText("Tipo"));
		taConfig.setVisible(true);
		showComboBox(true);

		selectedOption = deviceConfigKey.getKey();
		selectedOptionType = deviceType.getKey();
		cleanComboBox();

		vbAdditionalData.getChildren().clear();

		DeviceConfiguration deviceConfig;

		DeviceConfiguration provisionalConfig = provisionalConfigurations.get(selectedOption);
		if (provisionalConfig.getModel().isEmpty()) {
			deviceConfig = Devices.getInstance().getDevice(selectedOption).getConfiguration();
		}
		else {
			deviceConfig = provisionalConfig;
		}

		if (deviceConfig != null && !deviceConfig.getModel().equals(noUseVariable)) {
			setInitialDeviceConfiguration(deviceConfig);
			setAdditionalData(deviceConfig.getModelConfiguration());
		}
		else {
			cbDesConfig.getSelectionModel().select(noUseVariable);
			taConfig.setText("");
		}

		cbDesConfig.requestFocus();
	}

	protected void setAdditionalData(DeviceModelConfiguration modelConfiguration) {
		if (selectedOptionType.equals(DeviceType.TAG_SCALES)) {
			setScaleData(modelConfiguration);
		}

		vbAdditionalData.setVisible(!vbAdditionalData.getChildren().isEmpty());
		vbAdditionalData.setManaged(!vbAdditionalData.getChildren().isEmpty());
	}
    
    protected void setScaleData(DeviceModelConfiguration modelConfiguration) {
    	try {
    		String libraryHash = "";
    		    		
			Device device = Devices.getInstance().getDevice(selectedOption);
    		
			if (device instanceof DeviceScale) {
				DeviceScale scaleDevice = (DeviceScale) device;

				if (scaleDevice instanceof HashControlledDevice) {
					HashControlledDevice hashControlledScale = (HashControlledDevice) scaleDevice;
					libraryHash = hashControlledScale.getLibraryHash();
					if (!libraryHash.isEmpty()) {
						vbAdditionalData.getChildren().add(new Label(I18N.getText("Hash librería: ") + libraryHash));
					}
				}

				vbAdditionalData.getChildren().add(new Label(I18N.getText("Nº Cert: ") + scaleDevice.getCertNumber()));
				vbAdditionalData.getChildren().add(new Label(I18N.getText("Nombre fabricante: ") + "Comerzzia"));
				vbAdditionalData.getChildren().add(new Label(I18N.getText("Tipo de designación: ") + "Comerzzia Enterprise Suite Module czz-POS"));
    		}
		}
		catch (Exception e) {
			log.trace("setScalechecksum() - No hash configured.");
		}
	}
    
	protected void setInitialDeviceConfiguration(DeviceConfiguration device) {
		HashMap<String, HashMap<String, DeviceModelConfiguration>> configuracionDispositivo = Devices.getInstance().getAvailableDevicesConfiguration().getDevices(selectedOptionType);
        
		LinkedList<String> listaDispositivos = new LinkedList<>(configuracionDispositivo.keySet());
        
        // Fix para hacer que NO USA o la cadena equivalente sea la primera que aparezca
        if (listaDispositivos.removeFirstOccurrence(I18N.getText("NO USA"))){
            listaDispositivos.addFirst(I18N.getText("NO USA"));
        }
        
        ObservableList<String> listDesConfig = FXCollections.observableList(listaDispositivos);
        cbDesConfig.getSelectionModel().selectedItemProperty().removeListener(cbDesConfigListener);
		cbDesConfig.setItems(listDesConfig);
		
        String modelo = device.getModel();
        if(StringUtils.isBlank(modelo)) {
        	modelo = "NO USA";        	
        }
        
		cbDesConfig.getSelectionModel().select(modelo);
		cbDesConfig.getSelectionModel().selectedItemProperty().addListener(cbDesConfigListener);
        HashMap<String, DeviceModelConfiguration> configuracion = configuracionDispositivo.get(modelo);
        if(configuracion != null) {
			Set<String> metodosConexion = configuracion.keySet();
			cbConnMethods.setItems(FXCollections.observableList(new ArrayList<>(metodosConexion)));
	        cbConnMethods.getSelectionModel().select(device.getConnectionName());
	        if(device.getModelConfiguration().getConnectionConfig()!=null){
	            taConfig.setText(device.getModelConfiguration().getConnectionConfig().toString());
	        }
	        //Debemos mostrar las operaciones en caso de tenerlas
	        if(device.getModelConfiguration().getOperationsConfiguration()!=null){
	            taConfig.setText(taConfig.getText() +"\n"+ device.getModelConfiguration().getOperationsConfiguration().toString());
	        }
        }
        
        vbAdditionalData.setVisible(false);
        vbAdditionalData.setManaged(false);
        vbAdditionalData.getChildren().clear();
    }
    
    /**
     * Limpia las opciones disponibles en los combobox.
     */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void cleanComboBox() {
		HashMap<String, HashMap<String, DeviceModelConfiguration>> listaOpcionesTienda = Devices.getInstance().getAvailableDevicesConfiguration().getDevices(selectedOptionType);
        cbDesConfig.getSelectionModel().selectedItemProperty().removeListener(cbDesConfigListener);
        if(listaOpcionesTienda!=null){
            LinkedList listaDispositivos = new LinkedList(listaOpcionesTienda.keySet());        
             // Fix para hacer que NO USA o la cadena equivalente sea la primera que aparezca
            if (listaDispositivos.removeFirstOccurrence(I18N.getText("NO USA"))){
                listaDispositivos.addFirst(I18N.getText("NO USA"));
            }
            cbDesConfig.setItems(FXCollections.observableList(listaDispositivos));
        }
        else{
            cbDesConfig.setItems(FXCollections.<String>observableArrayList());
        }
        cbDesConfig.getSelectionModel().selectedItemProperty().addListener(cbDesConfigListener);
        cbConnMethods.setItems(FXCollections.<String>observableArrayList());
        
    }
    
	public void showConnMethodCBOptions() {
        
		if (!selectedOption.equals(OPTION_GENERAL)) {
			HashMap<String, HashMap<String, DeviceModelConfiguration>> opcionesBtSeleccion = Devices.getInstance().getAvailableDevicesConfiguration().getDevices(selectedOptionType);
            
			String opcionSeleccionadaDesConfig = cbDesConfig.getSelectionModel().getSelectedItem();
            
            if(opcionSeleccionadaDesConfig!=null){
                
                HashMap<String,DeviceModelConfiguration> metodosConex = opcionesBtSeleccion.get(opcionSeleccionadaDesConfig);
                if(metodosConex!=null){
                    cbConnMethods.setItems(FXCollections.observableList(new ArrayList<String>(metodosConex.keySet())));
                }
                else{
                    cbConnMethods.setItems(FXCollections.<String>emptyObservableList());
                    setProvitionalConfig(noUseVariable, null);
                }
                taConfig.setText("");
            }
        }
    }
    
	public void showConnMethodConfigs() {
        
		if (!selectedOption.equals(OPTION_GENERAL)) {
			HashMap<String, HashMap<String, DeviceModelConfiguration>> opcionesBtSeleccion = Devices.getInstance().getAvailableDevicesConfiguration().getDevices(selectedOptionType);
            
			String opcionSeleccionadaDesConfig = cbDesConfig.getSelectionModel().getSelectedItem();
            
            if(opcionSeleccionadaDesConfig!=null){
                DeviceModelConfiguration metodoConex = null;
                HashMap<String,DeviceModelConfiguration> metodosConex = opcionesBtSeleccion.get(opcionSeleccionadaDesConfig);
                if(metodosConex!=null){
					metodoConex = metodosConex.get(cbConnMethods.getSelectionModel().getSelectedItem());
                }
                
                if(metodoConex!=null){
                    taConfig.setText(metodoConex.getConnectionConfig().toString());            
                    //Debemos mostrar las operaciones en caso de tenerlas
                    if (metodoConex.getOperationsConfiguration()!=null){
                        taConfig.setText(taConfig.getText() +"\n"+ metodoConex.getOperationsConfiguration().toString());
                    }
                    setProvitionalConfig(opcionSeleccionadaDesConfig,metodoConex);
                    
                    Device dispositivo = getCurrentDevice();
                    if(dispositivo != null && dispositivo.isConfigurable()) {
						btEditConfiguration.setVisible(dispositivo != null && dispositivo.isConfigurable());
                    }
                    else {
                    	btEditConfiguration.setVisible(false);
                    }
                }
                else{
                    taConfig.setText("");
                }
            }
        }
    }
            
    @FXML
    public void actionCancel(){
		loadDefaultProvisionalConfigurations();
        closeCancel();
    }
    
    @FXML
    public void actionAccept(){
        try {
			for (Entry<String, DeviceConfiguration> entry : provisionalConfigurations.entrySet()) {
				if (!entry.getValue().getModel().equals("")) {
					Devices.getInstance().getDevice(entry.getKey()).setConfiguration(entry.getValue());
				}
			}
		
			StorePos confCaja = session.getApplicationSession().getStorePosBusinessData().getStorePos();
        
	        byte[] confDispositivos = Devices.getInstance().createDevicesConfiguration(confCaja.getSetting());
	        
	        if(confDispositivos!=null){
	            confCaja.setSetting(confDispositivos);
                storePosService.updatePosConfiguration(confCaja.getPosUid(), confCaja.getSetting());
                DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Deberá reiniciar la aplicación para establecer los cambios."));
                closeSuccess();
	
	            actionBtGeneral();
	            btEditConfiguration.setVisible(false);
	        }
        } catch (DeviceException e) {
			log.error("accionAceptar() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Error guardando información de dispositivos"));
		}
    }
    
    @FXML
    public void actionAcceptEnter(KeyEvent event){
        
        if(event.getCode() == KeyCode.ENTER){
            actionAccept();
        }
    }
    
    //Metodo para crear la configuración provisional de dispositivos para más adelante, al aceptar,
    //guardar todos los cambios realizados
    protected void setProvitionalConfig(String modelo, DeviceModelConfiguration metodoConex){
		if (!selectedOption.equals(OPTION_GENERAL)) {
			DeviceConfiguration deviceConfig = provisionalConfigurations.get(selectedOption);
			deviceConfig.setModel(modelo);
			deviceConfig.setConnectionName(metodoConex != null ? metodoConex.getConnectionName() : "");
			deviceConfig.setModelConfiguration(metodoConex);

			Device device = Devices.getInstance().getDevice(selectedOption);
			if (device != null && device.getConfiguration() != null) {
				deviceConfig.setConfigurationParameters(device.getConfiguration().getConfigurationParameters());
			}
		}
    }
    /**
     * Muestra los combobox de selección de metodo de conexion y tipo de dispositivo.
     * @param mostrarComboBox 
     */
    protected void showComboBox(boolean mostrarComboBox){
        cbDesConfig.setVisible(mostrarComboBox);
        cbConnMethods.setVisible(mostrarComboBox);
        tfTill.setVisible(!mostrarComboBox);
        tfStore.setVisible(!mostrarComboBox);
    }
    
    @FXML
    public void openDeviceConfiguration() {
    	Device dispositivo = getCurrentDevice();
    	if(dispositivo != null) {
    		dispositivo.configure(getStage());
    	}
    	else {
    		DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("No se ha podido abrir la ventana de configuración"));
    	}
    }

	protected Device getCurrentDevice() {
		DeviceConfiguration configuracionActual = provisionalConfigurations.get(selectedOption);

		String modelo = cbDesConfig.getSelectionModel().getSelectedItem();
		String nombreConexion = cbConnMethods.getSelectionModel().getSelectedItem();
		DeviceModelConfiguration confModelo = Devices.getInstance().getAvailableDevicesConfiguration().getDevices(selectedOptionType).get(modelo).get(nombreConexion);
		Device dispositivo = null;
        try {
	        dispositivo = new DeviceBuilder().create(confModelo);
	        dispositivo.setConfiguration(configuracionActual);
        }
        catch (Exception e) {
        	log.error("getDispositivoActual() - No se ha podido inicializar el dispositivo: " + e.getMessage());
        }
	    return dispositivo;
    }

}
