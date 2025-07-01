
package com.comerzzia.pos.devices.linedisplay;

import org.apache.log4j.Logger;

import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.omnichannel.facade.model.notification.Notification;
import com.comerzzia.omnichannel.facade.model.notification.Notification.NotificationType;
import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.device.DeviceAbstractImpl;
import com.comerzzia.pos.core.devices.device.linedisplay.DeviceLineDisplay;
import com.comerzzia.pos.core.services.notifications.Notifications;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosDeviceDisplayESCPOS;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosPrinterWritter;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosPrinterWritterRXTX;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosPrinterWritterUsb;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosUnicodeTranslator;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosUnicodeTranslatorInt;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.XMLDocumentNode;

public class DeviceLineDisplayEscPos extends DeviceAbstractImpl implements DeviceLineDisplay {

    protected static final Logger log = Logger.getLogger(DeviceLineDisplayEscPos.class.getName());

    protected EscPosDeviceDisplayESCPOS visor;

    @Override
    public void connect() {
    	
    }

    @Override
    public void write(String cadena1, String cadena2) {
        visor.writeVisor(cadena1, cadena2);
    }

    @Override
    public void writeLineUp(String cadena) {
        visor.writeVisor(cadena, "");
    }

    @Override
    public void writeLineDown(String cadena) {
        visor.writeVisor("", cadena);
    }

    @Override
    public void clear() {
        visor.clearVisor();
    }

    @Override
    public Integer getNumColumns() {
        //TODO: Buscar forma de contar el número de columnas en ESCPOS
        return null;
    }

    @Override
    public void writeRightUp(String cadena) {
        visor.writeVisor("       "+cadena, "");
        //TODO: Buscar forma de escribir a la derecha en ESCPOS
    }
    
    @Override
    public void writeRightDown(String cadena) {
        visor.writeVisor("", "       "+cadena);
        //TODO: Buscar forma de escribir a la derecha en ESCPOS
    }    

    

    @Override
    public void disconnect() {
    	clear();
    }

	@Override
	public Integer getNumRows() {
		return null;
	}

	@Override
	public void tenderMode(BasketPromotable<?> basket) {
	}

	@Override
	public void standbyMode() {
	}

	@Override
	public void saleMode(BasketPromotable<?> basket) {
	}

	@Override
	protected void loadConfiguration(DeviceConfiguration config) {
		try {
            if (visor == null) {
            	if(config == null || config.getModelConfiguration() == null || config.getModelConfiguration().getConnectionConfig() == null){
            		Notification notif = new Notification(I18N.getText("La configuración del visor \"{0}\" no es correcta", config != null? config.getConnectionName() : ""), NotificationType.ERROR);
        			Notifications.get().addNotification(notif);
            		return;
            	}
            	XMLDocumentNode configConexion = config.getModelConfiguration().getConnectionConfig();
            	
            	if(config.getModelConfiguration().getConnectionType().equals(CONN_TYPE_SERIAL_PORT)){
            		XMLDocumentNode nodoPuertoSerie = configConexion.getNode(TAG_CT_SERIAL_PORT);
            		
            		String puertoCOM = "COM" + nodoPuertoSerie.getNode(TAG_CT_SP_COMPORT).getValue();
            		int velocidad = nodoPuertoSerie.getNode(TAG_CT_SP_BAUDS).getValueAsInteger();
            		int bitsDatos = nodoPuertoSerie.getNode(TAG_CT_SP_DATABITS).getValueAsInteger();
            		int bitsParada = nodoPuertoSerie.getNode(TAG_CT_SP_STOPBITS).getValueAsInteger(); //StopBits 1.5 debe ser int 3. Definido en SerialPort.STOPBITS_1_5
            		int paridad = nodoPuertoSerie.getNode(TAG_CT_SP_PARITY).getValueAsInteger();
            		
            		visor = crearManejadorVisor(new EscPosPrinterWritterRXTX(puertoCOM, velocidad, bitsDatos, bitsParada, paridad), new EscPosUnicodeTranslatorInt());
            	}else if(config.getModelConfiguration().getConnectionType().equals(CONN_TYPE_USB)){
            		XMLDocumentNode nodoUSB = configConexion.getNode(TAG_CT_USB);
            		
            		String vendorId = nodoUSB.getNode(TAG_CT_USB_VENDORID).getValue();
            		String productId = nodoUSB.getNode(TAG_CT_USB_PRODUCTID).getValue();
            		String bus = nodoUSB.getNode(TAG_CT_USB_BUS).getValue();
            		String filename = nodoUSB.getNode(TAG_CT_USB_FILENAME).getValue();
            		String writingZone = nodoUSB.getNode(TAG_CT_USB_WRITINGZONE).getValue();
            		String numero = nodoUSB.getNode(TAG_CT_USB_NUMBER).getValue();
            		
            		visor = crearManejadorVisor(new EscPosPrinterWritterUsb(vendorId+"-"+productId+"-"+bus+"-"+filename+"-"+writingZone+","+numero), new EscPosUnicodeTranslatorInt());
            	}else{
            		Notification notif = new Notification(I18N.getText("La configuración del visor \"{0}\" no es correcta.", config != null? config.getConnectionName() : "")
            				+ " " +
            				I18N.getText("El tipo conexion \"{0}\" no es válido.", config.getModelConfiguration().getConnectionType()),
            				NotificationType.ERROR);
        			Notifications.get().addNotification(notif);
            	}
            	
            	
            	
            }
        } catch (Exception e) {
        	Notification notif = new Notification(I18N.getText("La configuración del visor \"{0}\" no es correcta.", config != null? config.getConnectionName() : ""), NotificationType.ERROR);
			Notifications.get().addNotification(notif);
        	log.error("cargaConfiguracion() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}
	}
	
	public EscPosDeviceDisplayESCPOS crearManejadorVisor(EscPosPrinterWritter printerWritter, EscPosUnicodeTranslator translator) {
		return new EscPosDeviceDisplayESCPOS(printerWritter, translator);
	}

}
