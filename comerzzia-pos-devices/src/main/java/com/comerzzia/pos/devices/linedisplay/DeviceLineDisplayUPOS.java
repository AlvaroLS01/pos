
package com.comerzzia.pos.devices.linedisplay;

import java.util.Date;

import org.apache.log4j.Logger;

import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.omnichannel.facade.model.notification.Notification;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.device.DeviceAbstractImpl;
import com.comerzzia.pos.core.devices.device.linedisplay.DeviceLineDisplay;
import com.comerzzia.pos.core.services.notifications.Notifications;
import com.comerzzia.pos.devices.drivers.javapos.JavaPosDisplay;
import com.comerzzia.pos.devices.drivers.javapos.facade.exceptions.NoDeviceException;
import com.comerzzia.pos.devices.drivers.javapos.facade.exceptions.NoLogicalNameException;
import com.comerzzia.pos.util.i18n.I18N;

import jpos.JposException;

public class DeviceLineDisplayUPOS extends DeviceAbstractImpl implements DeviceLineDisplay {

    protected static final Logger log = Logger.getLogger(DeviceLineDisplayUPOS.class.getName());
    
    protected JavaPosDisplay visor;
    
    protected String nombreConexion = "defaultLineDisplay";
    
    protected int filas = 2;
    
    protected int columnas = 20;
    
    protected boolean alertaError = false;
    
    protected int estado = APAGADO; 
	
	public static int APAGADO = 0;
	
	public static int ENCENDIDO = 1;
	
	public boolean bandera = false;
	
    @Override
	public void connect() {
    	if (visor == null || estado == APAGADO) {
	        try {
	        	log.debug("conecta() - Creamos la conexión con el visor");
	        	
	        	visor = JavaPosDisplay.getVisor();
            	visor.inicializar(nombreConexion);
            	
        		visor.prepareDevice();
            	        		
        		estado = ENCENDIDO;
        		
	            columnas = Devices.getInstance().getLineDisplay().getNumColumns();
	            filas = Devices.getInstance().getLineDisplay().getNumRows();
	            
	            alertaError = false;
	            
	            writeLineUp("---CAJA CERRADA---");
	        }
	        catch (NoLogicalNameException | NoDeviceException | JposException ex) {
	            log.error("conecta() Error: Conexión fallida "+ ex);
            	Devices.getInstance().getLineDisplay().setState(APAGADO);
	        }
    	}
	}
    
    @Override
    public void disconnect() {
        try {
        	clear();
        	visor.finishDevice();
        	estado = APAGADO;
			log.debug("desconecta() - Desconectando el visor...");
		} catch (JposException | NoLogicalNameException | NoDeviceException ex) {
		}
    }
    
    public void setState(int estado) {
		if (estado == APAGADO && alertaError == false) {		
			Notifications.get().addNotification(new Notification(I18N.getText("Compruebe que el visor está encendido y reinicie el TPV"), Notification.NotificationType.WARN, new Date()));
			alertaError = true;
		}
		
		this.estado = estado;
	}
    
    @Override
    public void write(String cadena1, String cadena2) {
        try {
        	if(checkConexion()==ENCENDIDO){
        		Devices.getInstance().getLineDisplay().clear();
        		if(cadena1.length()>columnas){
		    		cadena1 = cadena1.substring(0, columnas);
		    	}
		        visor.displayTextAt(cadena1, 0, 0);
		        
		        if(cadena2.length()>columnas){
		        	cadena2 = cadena2.substring(0, columnas);
		    	}
		        visor.displayTextAt(cadena2, 1, 0);
		        
		        alertaError = false;
        	}
        }
        catch (JposException ex) {
        	log.error("escribir() Error: Conexión fallida "+ ex);
            Devices.getInstance().getLineDisplay().setState(APAGADO);
        }
    }

    @Override
    public void writeLineUp(String cadena) {
        try {
        	if(checkConexion()==ENCENDIDO){
        		Devices.getInstance().getLineDisplay().clear();
	        	if(cadena.length()>columnas){
	        		cadena = cadena.substring(0, columnas);
	        	}
	            visor.displayTextAt(cadena, 0, 0);
	            
	            alertaError = false;
        	}
        }
        catch (JposException ex) {
        	log.error("escribirLineaArriba() Error: Conexión fallida "+ ex);
            Devices.getInstance().getLineDisplay().setState(APAGADO);
        }
    }

    @Override
    public void writeLineDown(String cadena) {
        try {
        	if(checkConexion()==ENCENDIDO){   
        		Devices.getInstance().getLineDisplay().clear();
	        	if(cadena.length()>columnas){
	        		cadena = cadena.substring(0, columnas);
	        	}
	            visor.displayTextAt(cadena, 1, 0);
	            
	            alertaError = false;
        	}
        }
        catch (JposException ex) {
        	log.error("escribirLineaAbajo() Error: Conexión fallida "+ ex);
            Devices.getInstance().getLineDisplay().setState(APAGADO);
        }
    }

    @Override
    public void clear() {
        try {
            visor.clearText();
            
            alertaError = false;
        }
        catch (JposException ex) {
        	log.error("limpiar() Error: Conexión fallida"+ ex);
            Devices.getInstance().getLineDisplay().setState(APAGADO);
        }
    }

    @Override
    public Integer getNumColumns() {
        Integer res = null;
        try {
            res = visor.getColumns();
        }
        catch (JposException ex) {
        	log.error("getNumColumnas() Error: Conexión fallida "+ ex);
            Devices.getInstance().getLineDisplay().setState(APAGADO);
        }
        return res;
    }
    
    public Integer getNumRows() {
        Integer res = null;
        try {
            res = visor.getRows();
        }
        catch (JposException ex) {
        	log.error("getNumFilas() Error: Conexión fallida "+ ex);
            Devices.getInstance().getLineDisplay().setState(APAGADO);
        }
        return res;
    }

    @Override
    public void writeRightUp(String cadena) {
        try {
        	if(checkConexion()==ENCENDIDO){
        		Devices.getInstance().getLineDisplay().clear();
	        	if(cadena.length()>columnas){
	        		cadena = cadena.substring(0, columnas);
	        	}
	            visor.displayRightAlignText(cadena);
	            
	            alertaError = false;
        	}
        }
        catch (JposException ex) {
        	log.error("escribirDerechaArriba() Error: Conexión fallida "+ ex);
            Devices.getInstance().getLineDisplay().setState(APAGADO);
        }
    }

    @Override
    public void writeRightDown(String cadena) {
        try {
        	if(checkConexion()==ENCENDIDO){
        		Devices.getInstance().getLineDisplay().clear();
	        	if(cadena.length()>columnas){
	        		cadena = cadena.substring(0, columnas);
	        	}
	            visor.displayRightAlignText(cadena);
	            
	            alertaError = false;
        	}
        }
        catch (JposException ex) {
        	log.error("escribirDerechaAbajo() Error: Conexión fallida "+ ex);
            Devices.getInstance().getLineDisplay().setState(APAGADO);
        }
    }
    
    
    
	@Override
	public void loadConfiguration(DeviceConfiguration config) {
		nombreConexion = config.getModelConfiguration().getConnectionName();
	}

	public int checkConexion(){
		if(estado == APAGADO){
			
			try {
				if(bandera == false){
					visor.close();
					bandera = true;
				}
			} catch (NoLogicalNameException | NoDeviceException | JposException e) {
				e.printStackTrace();
			}
			
    		connect();
    	}
		return this.estado;
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
}
