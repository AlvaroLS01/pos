package com.comerzzia.pos.core.gui.initialize;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.omnichannel.facade.model.notification.Notification;
import com.comerzzia.omnichannel.facade.model.payments.StorePosPaymentMethods;
import com.comerzzia.omnichannel.facade.service.payments.PaymentMethodManager;
import com.comerzzia.omnichannel.facade.service.payments.PaymentsManagerServiceFacade;
import com.comerzzia.omnichannel.facade.service.store.StorePosPaymentMethodServiceFacade;
import com.comerzzia.pos.core.devices.AvailableDevicesLoadException;
import com.comerzzia.pos.core.devices.DeviceType;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.device.Device;
import com.comerzzia.pos.core.devices.device.DeviceConfigKey;
import com.comerzzia.pos.core.devices.device.printer.DevicePrinter;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.POSURLHandler;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.initialize.InitializerOperation.OperationResult;
import com.comerzzia.pos.core.gui.skin.CzzImageManager;
import com.comerzzia.pos.core.services.notifications.Notifications;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;

@Component
@Scope("prototype")
@Slf4j
public class InitializerController extends SceneController implements Initializable{
	
    @Autowired
    protected Session sesion;
    
    @Autowired
    protected CzzImageManager czzImageManager;
    
    @Autowired
	protected StorePosPaymentMethodServiceFacade storePosPaymentMethodService;
    @Autowired
	protected PaymentsManagerServiceFacade paymentsManager;
    
    @FXML
    protected WebView operationWebView;
    
    @FXML
    protected Button btLogin;
    
    @FXML
    protected ImageView ivCustomerLogo;
    
    protected List<InitializerOperation> initializerOperations;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}
	
	@Override
	public void initializeComponents() throws InitializeGuiException {
		showKeyboard = false;
		ivCustomerLogo.setImage(czzImageManager.getLogo(CzzImageManager.IMAGES_CUSTOMER_LOGO_NAME));
	}
	
	@Override
	public void onSceneOpen() throws InitializeGuiException {
		log.debug("onSceneOpen() - Loading application configuration.");
		initializerOperations = new ArrayList<InitializerOperation>();
		FormatUtils.getInstance().init(Locale.getDefault());
		refreshScreenData();
	}
	
	@Override
	protected void executeLongOperations() {
		initializeApplication();
		disconectLineDisplay();
		initializeDevicesConfig();
		initializeDevices();
		initializeURLHandlers();
		initializePaymentMethods();
		initializeOtherControllers();
	}
	
	@Override
	protected void succededLongOperations() {
		refreshScreenData();
		Devices.getInstance().getLineDisplay().standbyMode();
		if(canContinue()) {
			btLogin.fire();

		}
	}
	
	@Override
	protected void failedLongOperations(Throwable e) {
		refreshScreenData();
	}
	
	protected void initializePaymentMethods() {
		InitializerOperation operation = new InitializerOperation(I18N.getText("Inicializando métodos de pago ..."));
		operation.setOperationResult(OperationResult.OK);
		initializerOperations.add(operation);
		StorePosPaymentMethods paymentMethods = storePosPaymentMethodService.getPaymentMethods(sesion.getApplicationSession().getCodAlmacen(), sesion.getApplicationSession().getTillCode());
		paymentsManager.loadPaymentMethodManagers(new ArrayList<>(paymentMethods.getPaymentMethodConfigurations().values()));
		for(String paymentMethod:paymentMethods.getPaymentMethodConfigurations().keySet()) {
			try {
				PaymentMethodManager  paymentManager = paymentsManager.getPaymentMethodManager(paymentMethod);
				if(paymentManager!=null) {
					paymentManager.initialize();
				}
			}catch(Throwable e) {
				log.warn("initializePaymentMethods() - Error initializing payment method.", e);
				operation.setOperationResult(OperationResult.WARN);
				operation.getOperationMsgs().add(I18N.getText("Error inicializando método de pago {0} - {1}", paymentMethod, paymentMethods.getPaymentsMethods().get(paymentMethod).getPaymentMethodDes()));
				operation.getOperationMsgs().add(e.getMessage());
			}
			
		}
	}
	
	protected void initializeOtherControllers() {}
	
	protected void initializeURLHandlers() {
		log.debug("initializeURLHandlers() - Initializing URL handlers...");
		InitializerOperation operation = new InitializerOperation(I18N.getText("Inicializando manejadores de URL's..."));
		initializerOperations.add(operation);
		try {
			POSURLHandler.clearMethods();

			operation.setOperationResult(OperationResult.OK);
		}catch (Throwable e) {
			log.error("initializeURLHandlers() - Error inititalizing URL handlers");
			operation.setOperationResult(OperationResult.ERROR);
			operation.getOperationMsgs().add(e.getMessage());
		}

	}

	protected void initializeDevicesConfig() {
		log.debug("initializeDevicesConfig - Initializing devices config...");
		InitializerOperation operation = new InitializerOperation(I18N.getText("Inicializando configuración de dispositivos..."));
		initializerOperations.add(operation);
		try {
			operation.setOperationResult(OperationResult.OK);
			// Cargamos dispositivos
			Devices dispositivos = Devices.getInstance();

			try {
				// Leemos los dispositivos disponibles
				dispositivos.loadAvailableDevices();
			}
			catch (AvailableDevicesLoadException e) {
				log.error(e.getMessage(), e);
				String error = I18N.getText("Ha habido errores al cargar la configuración de dispositivos disponibles. Por favor, revise el archivo devices.xml");
				Notifications.get().addNotification(new Notification(error, Notification.NotificationType.WARN, new Date()));
				operation.setOperationResult(OperationResult.WARN);
				operation.getOperationMsgs().add(e.getMessage());
			}
			try {
				// Cargamos la configuración de los dispositivos
				dispositivos.loadDevicesConfiguration(sesion.getApplicationSession().getStorePosBusinessData().getStorePos().getSetting());
			}
			catch (AvailableDevicesLoadException e) {
				log.warn("initializeDevicesConfig() - Error: ", e);
				operation.setOperationResult(OperationResult.WARN);
				if (sesion.getApplicationSession().getStorePosBusinessData().getStorePos().getSetting() == null) {
					e.getErrors().add(0, I18N.getText("Aún no se han configurado los dispositivos"));					
				}
				
				// Crear una notificación por cada dispositivo
				for (String error : e.getErrors()) {
					log.warn("initializeDevicesConfig() - "+error);
					Notifications.get().addNotification(new Notification(error, Notification.NotificationType.WARN, new Date()));
					operation.getOperationMsgs().add(error);
				}				
			}
		}catch (Throwable e) {
			log.error("initializeDevicesConfig() - ",e);
			operation.setOperationResult(OperationResult.ERROR);
			operation.getOperationMsgs().add(e.getMessage());
		}
	}
	
	protected void initializeDevices() {
		Devices dispositivos = Devices.getInstance();
		HashMap<String, Device> devicesConfigured = dispositivos.getDevices();
		Map<String, DeviceType> devicesType = dispositivos.getAvailableDevicesConfiguration().getDevicesType();
		
		// for every device type
		for (Entry<String, DeviceType> deviceType : devicesType.entrySet()) {
			// for every device type configuration
			for (DeviceConfigKey configTag : deviceType.getValue().getConfigurationTags()) {
				String configTagKey = configTag.getKey();
				Device dispositivo = devicesConfigured.get(configTagKey);
				
				if (dispositivo == null || 
					configTagKey.equals(DeviceType.LINE_DISPLAY)) { //No se inicializa el visor fuera del hilo de javafx
					continue; 
				}
				
				String deviceModel = configTag.getLabel();
				if (dispositivo.getConfiguration() != null) {
					deviceModel = deviceModel + "-" + dispositivo.getConfiguration().getModel();
				}
				
				log.debug("initializeDevices() - Initializing device: " + configTagKey + "-" + configTag.getLabel());
				InitializerOperation operation = new InitializerOperation(I18N.getText("Inicializando dispositivo: {0}", deviceModel));
				initializerOperations.add(operation);
				try{
					dispositivo.connect();
					if (configTagKey.equals(DeviceType.PRINTER1) || configTagKey.equals(DeviceType.PRINTER2) ) {
						printTestPage(configTagKey);
					}
					operation.setOperationResult(OperationResult.OK);
				}catch(Throwable e){
					log.error("initializeDevices() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
					String error = I18N.getText("Ha ocurrido un error al conectar el dispositivo {0}", dispositivo.toString());
					Notifications.get().addNotification(new Notification(error, Notification.NotificationType.WARN, new Date()));
					operation.setOperationResult(OperationResult.WARN);
					operation.getOperationMsgs().add(e.getMessage());
				}
			}
		}		
	}
	
	protected void initializeApplication() {
		log.debug("initializeApplication() - Initializing Application...");
		InitializerOperation operation = new InitializerOperation(I18N.getText("Inicializando aplicación..."));
		initializerOperations.add(operation);
		try {
			sesion.initAplicacion();
			operation.setOperationResult(OperationResult.OK);
		} catch (Throwable e) {
			log.error("initializeApplication() - Error initializing application", e);
			operation.setOperationResult(OperationResult.ERROR);
			operation.getOperationMsgs().add(e.getMessage());
		}
	}
	
	@Override
	protected void initializeFocus() {
		btLogin.requestFocus();
	}
	
	protected void loadOperationWebView() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("operations", initializerOperations);
		loadWebView("core/initializer", params, operationWebView);
	}

	@Override
	@FXML
	public void gotoLogin() {
		log.debug("gotoLogin() - Initialization succeded. Loading login screen.");
		
		closeSuccess();
		
		
	}
	

	protected void refreshScreenData() {
		btLogin.setDisable(!canIgnore());
		loadOperationWebView();
	}

	protected boolean canContinue() {
		return initializerOperations.stream().noneMatch(op -> op.getOperationResult().equals(OperationResult.ERROR) 
				|| op.getOperationResult().equals(OperationResult.PENDING)
				|| op.getOperationResult().equals(OperationResult.WARN));
	}
	
	protected boolean canIgnore() {
		return !initializerOperations.isEmpty() && initializerOperations.stream().noneMatch(op -> op.getOperationResult().equals(OperationResult.ERROR) 
				|| op.getOperationResult().equals(OperationResult.PENDING));
	}
	
	protected void disconectLineDisplay() {
		try {
			if(Devices.getInstance().getLineDisplay()!=null) {
				log.debug("disconectLineDisplay() - LineDisplay already intialized. Disconecting.");
				Devices.getInstance().getLineDisplay().disconnect();
			}
		}catch (Throwable ignore) {}
	}
	
	@Override
	public WebView getWebView() {
		return operationWebView;
	}
	
	protected void printTestPage(String configTagKeyDevice) throws Exception {
		Optional<Device> device = Devices.getInstance().getDeviceIfNotDummy(configTagKeyDevice);
		if (device.isPresent()) {
			((DevicePrinter) device.get()).printTest();
		}
	}
	
	@Override
	public void onInactivity() {/* Do noting */}

}
