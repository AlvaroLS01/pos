package com.comerzzia.pos.core.devices;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.comerzzia.omnichannel.facade.model.notification.Notification;
import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.configuration.DeviceModelConfiguration;
import com.comerzzia.pos.core.devices.device.Device;
import com.comerzzia.pos.core.devices.device.DeviceBuilder;
import com.comerzzia.pos.core.devices.device.DeviceException;
import com.comerzzia.pos.core.devices.device.cashdrawer.DeviceCashDrawer;
import com.comerzzia.pos.core.devices.device.fiscal.DeviceFiscal;
import com.comerzzia.pos.core.devices.device.linedisplay.DeviceLineDisplay;
import com.comerzzia.pos.core.devices.device.loyaltycard.DeviceLoyaltyCard;
import com.comerzzia.pos.core.devices.device.mobilephonerecharge.DeviceMobilePhoneRecharge;
import com.comerzzia.pos.core.devices.device.printer.DevicePrinter;
import com.comerzzia.pos.core.devices.device.scale.DeviceScale;
import com.comerzzia.pos.core.devices.device.scanner.DeviceScanner;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.XMLDocument;
import com.comerzzia.pos.util.xml.XMLDocumentNode;


public class Devices {

	private static Devices instance = null;

	/**
	 * TAGS de archivo de configuración *
	 */
	public static final String TAG_ROOT = "Parametros";
	public static final String TAG_DEVICES = "dispositivos";
	public static final String TAG_CONN_NAME = "nombreconexion";
	public static final String TAG_COMMANDS_NAME = "nombrejuegocomando";
	public static final String TAG_MODEL = "modelo";
	public static final String TAG_CONFIGURATION = "configuracion";
	public static final String TAG_CONFIG_PARAMS = "parametrosConfiguracion";
	public static final String NO_USA = "NO USA";

	// Log
	private static final Logger log = Logger.getLogger(Devices.class.getName());
	// Dispositivos de la aplicación
	protected LinkedHashMap<String, Device> devices = new LinkedHashMap<>();

	protected AvailableDevicesConfiguration availableDevices;
	
	public Devices() {
	}

	public static Devices getInstance() {
		if (instance == null) {
			instance = new Devices();
		}
		return instance;
	}
	
	public static void setCustomInstance(Devices instance){
		Devices.instance = instance;
	}

	public List<Notification> connectDevices() {
		List<Notification> errors = new ArrayList<>();
		
		for (Device device : devices.values()) {
			try{
				device.connect();
			}catch(Throwable e){
				log.error("connectDevices() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
				String error = I18N.getText("Ha ocurrido un error al conectar el dispositivo {0}", device.toString());
				errors.add(new Notification(error, Notification.NotificationType.ERROR, new Date()));
			}
		}
		
		return errors;
	}
	
	public HashMap<String, Device> getDevices(){
		return devices;
	}
	
	/**
	 * Libera todos los dispositivos configurados
	 * @throws DeviceException 
	 */
	public void disconnectDevices()  {
		for (Device device : devices.values()) {
			if (device != null) {
				try{
					device.disconnect();
				}catch(Throwable e){
					log.error("disconnectDevices() - Error al liberar dispositivo " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
				}
			}
		}
	}

	/**
	 * Carga la configuración de dispositivos de la aplicación
	 *
	 * @param devicesConfiguration
	 */
	public void loadDevicesConfiguration(byte[] devicesConfiguration) throws AvailableDevicesLoadException {
		List<String> errorsList = new ArrayList<String>();
		// Leemos la configuración de dispositivos
		try {
			devices.clear();

			// Primero ponemos todos los dispositivos a NoConfig			
			availableDevices.getDevicesType().values().forEach(deviceType ->
				deviceType.getConfigurationTags().forEach(configurationTag -> {
					try {
						setDevice(configurationTag.getKey(), deviceType.getDummyImplementation().newInstance());
					} catch (Exception e) {
						log.warn("Se ha producido un error inicializando el dispositivo dummy para la configuración " + configurationTag.getKey(), e);
					}
				}));				
			
			
			XMLDocument xmlDocument = new XMLDocument(devicesConfiguration);
			XMLDocumentNode root = xmlDocument.getRoot();
			XMLDocumentNode deviceNode = root.getNode(TAG_DEVICES, true);
			
			if (deviceNode == null ) {
			   throw new Exception(I18N.getText("El POS no contiene configuración de dispositivos. Posiblemente aun no esta configurado."));
			}
			
			List<XMLDocumentNode> childs = deviceNode.getChildren();
			for (XMLDocumentNode deviceConfigNode : childs) {
				String deviceConfigKey = deviceConfigNode.getName();
				
				// registrar tipo de dispositivo
				DeviceType deviceType = availableDevices.getTypeForConfigTag(deviceConfigKey);
				
				if (deviceType == null) {
					log.warn("No se ha encontrado tipo de dispositivo para configuracion " + deviceConfigKey);
					continue;
				}
				
				try {
				   loadDeviceConfiguration(deviceType, deviceConfigKey, deviceConfigNode);
				} catch (Exception e) {
					log.error("loadDevicesConfiguration() - Error loading device configuration: ", e);
					errorsList.add(e.getMessage());
				}
			}
		} catch (Exception ex) {
			String error = I18N.getText("Error parseando el xml de configuracion: ") + ex.getMessage();
			
			log.error(error, ex);
			errorsList.add(error);
		}

		if (errorsList.size() > 0) {
			throw new AvailableDevicesLoadException(errorsList);
		}
	}

	protected void loadDeviceConfiguration(DeviceType deviceType, String deviceConfigKey, XMLDocumentNode device) throws Exception {
		DeviceConfiguration deviceConfig = null;
		DeviceModelConfiguration modelConfig = null;
		Device iDevice = null;
		
		log.debug("loadDeviceConfiguration() - Cargando configuración de dispositivo a partir del nodo: " + device.getName());
						
				
		// obtener configuracion del dispositivo y del modelo
		try {					
			// Carga la configuracion del tipo de dispositivo de la configuracion del POS
			deviceConfig = new DeviceConfiguration(deviceConfigKey, device);
			String model = deviceConfig.getModel();
			String connName = deviceConfig.getConnectionName();

			if (model != null && !model.isEmpty() && !NO_USA.equals(model) && !connName.isEmpty()) {
				// Cargar la configuracion del tipo de dispositivo/modelo de devices.xml
				try {				   
				   modelConfig = availableDevices.getDeviceTypeConfigurations(deviceType.getKey()).get(model).get(connName);
				} catch (Exception ignore) {
				}
				
				if (modelConfig == null) {
					throw new Exception(I18N.getText("Modelo/conexión no disponible") + " " + model + "/" + connName);
				}
								
				deviceConfig.setModelConfiguration(modelConfig);
				availableDevices.setDeviceConnectionMethod(deviceType.getKey(), deviceConfig);
				iDevice = new DeviceBuilder().create(modelConfig);				
				iDevice.setConfiguration(deviceConfig);
				setDevice(deviceConfigKey, iDevice);
				
			}
		} catch (Exception e) {
			String error = I18N.getText("Error cargando dispositivo") + " '" + deviceConfigKey + "': " + e.getMessage();
			log.error(error, e);
			
			// Poner el dispositivo "dummy" con la configuracion guardada
			deviceConfig.setModelConfiguration(modelConfig);
			availableDevices.setDeviceConnectionMethod(deviceType.getKey(), deviceConfig);
			iDevice = deviceType.getDummyImplementation().newInstance();						
			iDevice.setConfiguration(deviceConfig);			
			setDevice(deviceConfigKey, iDevice);
			
			throw new Exception(error, e);			
		}		
	}

	/**
	 * Lee los dispositivos disponibles
	 * 
	 * @throws AvailableDevicesLoadException
	 */
	public void loadAvailableDevices() throws AvailableDevicesLoadException {
		availableDevices = new AvailableDevicesConfiguration();
		availableDevices.loadDevicesConfiguration();
	}

	public byte[] createDevicesConfiguration(byte[] posConfiguration) {
		try {
			Node paymentMethodsNode = null;
			try {
				XMLDocument xmlConfiguracion = new XMLDocument(posConfiguration); 
				XMLDocumentNode xmlNodeMediosPago = xmlConfiguracion.getNode("medios_pago", true);
				if(xmlNodeMediosPago != null) {
					paymentMethodsNode = xmlNodeMediosPago.getNode();
				}
			}
			catch(Exception e) {
				log.error("createDevicesConfiguration() - Ha habido un error al recuperar la configuración de medios de pago: " + e.getMessage(), e);
			}
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			Element root = doc.createElement(TAG_ROOT);
			doc.appendChild(root);
			Element devicesNode = doc.createElement(TAG_DEVICES);
			root.appendChild(devicesNode);

			for (Entry<String, Device> entry : devices.entrySet()) {
				String deviceConfigKey = entry.getKey();
				Device iDispositivo = entry.getValue();
				if (iDispositivo != null && iDispositivo.getConfiguration() != null && !iDispositivo.getConfiguration().getModel().equals(AvailableDevicesConfiguration.DEVICE_NOT_CONFIGURED)) {
					devicesNode.appendChild(createDeviceConfiguration(deviceConfigKey, iDispositivo.getConfiguration().getModel(), iDispositivo.getConfiguration().getConnectionName(), iDispositivo.getConfiguration().getConfigurationParameters(), doc));
				} else {
					devicesNode.appendChild(createDeviceConfiguration(deviceConfigKey, AvailableDevicesConfiguration.DEVICE_NOT_CONFIGURED, "", null, doc));
				}
			}
			
			if(paymentMethodsNode != null) {
				Node nodoMediosPagoCopia = doc.importNode(paymentMethodsNode, true);
				root.appendChild(nodoMediosPagoCopia);
			}
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(bos);
			transformer.transform(source, result);

			return bos.toByteArray();

		} catch (ParserConfigurationException | TransformerException ex) {
			log.error("crearConfiguracionDispositivos() - Error al crear el xml de configuracion de dispositivos : " + ex.getMessage(), ex);
		}

		return null;
	}

	protected Element createDeviceConfiguration(String nDevice, String model, String connectionName, Map<String,String> configurationParams, Document doc) {
		Element device = doc.createElement(nDevice);
		Element modelNode = doc.createElement(TAG_MODEL);

		modelNode.appendChild(doc.createTextNode(model));

		Element connNameNode = doc.createElement(TAG_CONN_NAME);
		connNameNode.appendChild(doc.createTextNode(connectionName));

		device.appendChild(modelNode);
		device.appendChild(connNameNode);
		
		if(configurationParams != null) {
			Element configurationparamNode = doc.createElement(TAG_CONFIG_PARAMS);
			for(String param : configurationParams.keySet()) {
				Element paramNode = doc.createElement(param);
				paramNode.appendChild(doc.createTextNode(configurationParams.get(param)));
				configurationparamNode.appendChild(paramNode);
			}
			device.appendChild(configurationparamNode);
		}

		return device;
	}

	
    public AvailableDevicesConfiguration getAvailableDevicesConfiguration() {
        return availableDevices;
    }
    
     public static void openCashDrawer(){     
    	log.debug("openCashDrawer() - Abriendo cajon");
        getInstance().getCashDrawer().open();
    }

	@SuppressWarnings("unchecked")
	public <T extends Device> T getDevice(String deviceConfigTag) {
		return (T) devices.get(deviceConfigTag);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Device> Optional<T> getDeviceIfNotDummy(String deviceConfigTag) {
		DeviceType deviceType = availableDevices.getTypeForConfigTag(deviceConfigTag);
		Device device = devices.get(deviceConfigTag);
		if (device == null || (deviceType != null && deviceType.getDummyImplementation().isInstance(device))) {
			return Optional.empty();
		}
		return Optional.of((T) device);
	}

	public void setDevice(String deviceConfigTag, Device device) {
		devices.put(deviceConfigTag, device);
	}
     
	public DeviceCashDrawer getCashDrawer() {
		return (DeviceCashDrawer) devices.get(DeviceType.CASH_DRAWER);
	}

	public void setCashDrawer(DeviceCashDrawer cashDrawer) {
		devices.put(DeviceType.CASH_DRAWER, cashDrawer);
	}

	public DevicePrinter getPrinter1() {
		return (DevicePrinter) devices.get(DeviceType.PRINTER1);
	}

	public void setPrinter1(DevicePrinter printer1) {
		devices.put(DeviceType.PRINTER1, printer1);
	}

	public DevicePrinter getPrinter2() {
		return (DevicePrinter) devices.get(DeviceType.PRINTER2);
	}

	public void setPrinter2(DevicePrinter printer2) {
		devices.put(DeviceType.PRINTER2, printer2);
	}

	public DeviceLineDisplay getLineDisplay() {
		return (DeviceLineDisplay) devices.get(DeviceType.LINE_DISPLAY);
	}

	public void setLineDisplay(DeviceLineDisplay lineDisplay) {
		devices.put(DeviceType.LINE_DISPLAY, lineDisplay);
	}

	public DeviceScale getScale() {
		return (DeviceScale) devices.get(DeviceType.SCALE);
	}

	public void setScale(DeviceScale scale) {
		devices.put(DeviceType.SCALE, scale);
	}

	public DeviceLoyaltyCard getLoyaltyCard() {
		return (DeviceLoyaltyCard) devices.get(DeviceType.LOYALTY_CARD);
	}

	public void setLoyaltyCard(DeviceLoyaltyCard loyaltyCard) {
		devices.put(DeviceType.LOYALTY_CARD, loyaltyCard);
	}
	public DeviceMobilePhoneRecharge getMobilePhoneRecharge() {
		return (DeviceMobilePhoneRecharge) devices.get(DeviceType.MOBILE_PHONE_RECHARGE);
	}

	public void setMobilePhoneRecharge(DeviceMobilePhoneRecharge phoneRecharge) {
		devices.put(DeviceType.MOBILE_PHONE_RECHARGE, phoneRecharge);
	}
	public DeviceScanner getScanner() {
		return (DeviceScanner) devices.get(DeviceType.SCANNER);
	}

	public void setScanner(DeviceScanner scanner) {
		devices.put(DeviceType.SCANNER, scanner);
	}

	public DeviceFiscal getFiscal() {
		return (DeviceFiscal) devices.get(DeviceType.FISCAL);
	}

	public void setFiscal(DeviceFiscal fiscal) {
		devices.put(DeviceType.FISCAL, fiscal);
	}

	
}
