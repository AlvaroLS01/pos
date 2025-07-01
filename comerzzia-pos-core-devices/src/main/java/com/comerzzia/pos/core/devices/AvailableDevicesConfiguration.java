package com.comerzzia.pos.core.devices;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.configuration.DeviceModelConfiguration;
import com.comerzzia.pos.core.devices.device.Device;
import com.comerzzia.pos.core.devices.device.DeviceAbstractImpl;
import com.comerzzia.pos.core.devices.device.DeviceConfigKey;
import com.comerzzia.pos.util.xml.XMLDocument;
import com.comerzzia.pos.util.xml.XMLDocumentException;
import com.comerzzia.pos.util.xml.XMLDocumentNode;

public class AvailableDevicesConfiguration {

    public static final String DEVICE_NOT_CONFIGURED = "NO USA";

    private static final String TAG_CONN_METHODS = "connectionmethods";
    private static final String ATT_MODEL = "model";
    private static final String ATT_CONN_NAME = "connectionname";
    private static final String ATT_CONN_TYPE = "connectiontype";
    private static final String ATT_MANAGER = "manager";
   
	protected final Map<String, DeviceType> devicesType = new LinkedHashMap<>();

    protected final HashMap<String, HashMap<String, HashMap<String, DeviceModelConfiguration>>> devices = new HashMap<>();

    private static final Logger log = Logger.getLogger(AvailableDevicesConfiguration.class.getName());

    public void loadDevicesConfiguration() throws AvailableDevicesLoadException {
        String connType, connMethod, manager;
        
        try {
            log.trace("loadDevicesConfiguration() - Cargando tipos de dispositivos a partir de XML...");
            XMLDocument xmlDocument = loadDevicesXMLConfiguration();

            XMLDocumentNode nodeDevices = xmlDocument.getRoot();
            
            for (XMLDocumentNode deviceTypeNode : nodeDevices.getChildren()) {
            	DeviceType deviceType = registerDeviceType(deviceTypeNode);
            	
            	HashMap<String, HashMap<String, DeviceModelConfiguration>> deviceTypeConfigurations = new LinkedHashMap<>();
            	devices.put(deviceType.getKey(), deviceTypeConfigurations);
            	
            	// add NO CONFIGURED MODEL
            	deviceTypeConfigurations.put(DEVICE_NOT_CONFIGURED, null);
            	            	
            	XMLDocumentNode nodeDevice = nodeDevices.getNode(deviceType.getKey());
            	List<XMLDocumentNode> nodeDeviceModels = nodeDevice.getChildren();
                
                boolean firstConfigKey = true;

                for (XMLDocumentNode nodeDeviceModel : nodeDeviceModels) {
                	if (nodeDeviceModel.getName().equals("configKey")) {
                		if (firstConfigKey) {
                			deviceType.getConfigurationTags().clear();
                			firstConfigKey = false;
                		}
                		deviceType.getConfigurationTags().add(new DeviceConfigKey(nodeDeviceModel.getAttributeValue("key", false), nodeDeviceModel.getAttributeValue("label", true)));
                		continue;
                	} 
                	List<XMLDocumentNode> connMethodTypes = nodeDeviceModel.getNode(TAG_CONN_METHODS).getChildren();
                	HashMap<String, DeviceModelConfiguration> deviceConnMethods = new LinkedHashMap<>();
                    
                    for (XMLDocumentNode nodeConnMethod : connMethodTypes) {
                        connMethod = nodeConnMethod.getAttributeValue(ATT_CONN_NAME, true);
                        connType = nodeConnMethod.getAttributeValue(ATT_CONN_TYPE, true);
                        manager = nodeConnMethod.getAttributeValue(ATT_MANAGER, true);
                        deviceConnMethods.put(connMethod, new DeviceModelConfiguration(nodeDeviceModel, nodeConnMethod, connMethod, connType, manager));
                    }
                    
                    deviceTypeConfigurations.put(nodeDeviceModel.getAttributeValue(ATT_MODEL, true), deviceConnMethods);
                }	
            }
        }
        catch (XMLDocumentException ex) {
            log.error("cargarConfiguracionDispositivos() - Error cargando configuración de dispositivos . "+ex.getMessage());
            throw new AvailableDevicesLoadException(ex);
        }
        catch(Exception e){
            log.error("cargarConfiguracionDispositivos() - Error cargando configuración de dispositivos . "+e.getMessage(), e);
            throw new AvailableDevicesLoadException(e);
        }
    }

	public HashMap<String, HashMap<String, DeviceModelConfiguration>> getLineDisplays() {
        return devices.get(DeviceType.TAG_LINE_DISPLAYS);
    }

    public HashMap<String, HashMap<String, DeviceModelConfiguration>> getCashDrawers() {
    	return devices.get(DeviceType.TAG_CASH_DRAWERS);
    }

    public HashMap<String, HashMap<String, DeviceModelConfiguration>> getPrinters() {
    	return devices.get(DeviceType.TAG_PRINTERS);
    }

    public HashMap<String, HashMap<String, DeviceModelConfiguration>> getScales() {
    	return devices.get(DeviceType.TAG_SCALES);
    }

    public HashMap<String, HashMap<String, DeviceModelConfiguration>> getLoyaltyCards() {
    	return devices.get(DeviceType.TAG_LOYALTYCARDS);
    }

    
    public HashMap<String, HashMap<String, DeviceModelConfiguration>> getMobileDeviceRecharges() {
    	return devices.get(DeviceType.TAG_MOBILE_PHONE_RECHARGES);
    }

    public HashMap<String, HashMap<String, DeviceModelConfiguration>> getDevices(String tipoDispositivo) {
    	return devices.get(tipoDispositivo);
    }
    
    public HashMap<String, HashMap<String, DeviceModelConfiguration>> getScanners() {
    	return devices.get(DeviceType.TAG_SCANNERS);
    }

	public void setDeviceConnectionMethod(String deviceTypeKey, DeviceConfiguration deviceConfig) {
    	HashMap<String, HashMap<String, DeviceModelConfiguration>> map = devices.get(deviceTypeKey);
        if (deviceConfig.isNotDummyDevice()) {
            String model = deviceConfig.getModel();
            String connName = deviceConfig.getConnectionName();
            DeviceModelConfiguration conn = map.get(model).get(connName);
            deviceConfig.setModelConfiguration(conn);

        }
    }

    protected XMLDocument loadDevicesXMLConfiguration() throws AvailableDevicesLoadException {
        XMLDocument xmlDocument;
        try {
            //Cargar fichero de configuración 
            URL url = Thread.currentThread().getContextClassLoader().getResource("devices.xml");
            if (url == null) {
                log.error("cargarXMLConfDispositivos() - No se ha encontrado el fichero de configuración de dispositivos: devices.xml");
                throw new RuntimeException("No se ha encontrado el fichero de configuración de dispositivos: devices.xml");
            }
            xmlDocument = new XMLDocument(url);
            log.info("Usando URL [" + url.toString() + "] para configuración de dispositivos.");
            return xmlDocument;
        }
        catch (Exception e) {
            log.fatal("Error al cargar configuración de dispositivos : " + e.getMessage());
            throw new AvailableDevicesLoadException("Error cargando fichero devices.xml con configuración de dispositivos. ", e);
        }
    }
    
    
    @SuppressWarnings("unchecked")
	protected DeviceType registerDeviceType(XMLDocumentNode device) throws Exception {
		String dummyDeviceString = device.getAttributeValue("dummyManager", true);
		String deviceLabelString = device.getAttributeValue("label", true);
		
		Class<? extends DeviceAbstractImpl> dummyDevice = null;
		DeviceType deviceType = new DeviceType(device.getName(), deviceLabelString);
				
		if (StringUtils.isNotBlank(dummyDeviceString)) {
			dummyDevice = (Class<? extends DeviceAbstractImpl>) getClass().getClassLoader().loadClass(dummyDeviceString);
			deviceType.setDummyImplementation(dummyDevice);
		}
		
		Assert.notNull(deviceType.getDummyImplementation(), "Dummy implementation for device type " + deviceType.getKey() + " not found");
				
		devicesType.put(deviceType.getKey(), deviceType);
		
		return deviceType;		
	}
	
	
	public Map<String, DeviceType> getDevicesType() {
		return devicesType;	
	}
	
	public HashMap<String, HashMap<String, DeviceModelConfiguration>> getDeviceTypeConfigurations(String key) {
		return devices.get(key);		
	}

	public DeviceType getTypeForConfigTag(String name) {
		for (DeviceType deviceType : devicesType.values()) {
			for (DeviceConfigKey configKey : deviceType.getConfigurationTags()) {
				if (StringUtils.equals(name, configKey.getKey())) {
					return deviceType;					
				}
			}
		}
		return null;
	}

}
