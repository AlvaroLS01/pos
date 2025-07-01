
package com.comerzzia.pos.devices.drivers.javapos.facade.jpos;

import com.comerzzia.pos.devices.drivers.javapos.facade.exceptions.NoDeviceException;
import com.comerzzia.pos.devices.drivers.javapos.facade.exceptions.NoLogicalNameException;

import jpos.BaseControl;
import jpos.JposException;

public abstract class CommonDeviceCatFacade {
    
        private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(CommonDeviceCatFacade.class.getName());
    
	/**
	 * Nombre lógico del dispositivo. Se configuró de tal manera en el archivo jpos.xml
	 */
	private String logicalName;
	/**
	 * Dispositivo utilizado. Serán clases que hereden de BaseControl como LineDisplay, POSPrinter o CashDrawer
	 */
	private BaseControl device;


	
	/**
	 * Devuelve la instancia del dispositivo utilizado
	 * @return Instancia del dispositivo utilizado
	 */
	public BaseControl getDevice() {
		return device;
	}

	
	/**
	 * Inserta la instancia del dispositivo utilizado
	 * @param device Instancia del dispositivo utilizado
	 */
	public void setDevice(BaseControl device) {
		this.device = device;
	}

	/**
	 * Devuelve el nombre lógico del dispositivo
	 * @return Nombre lógico del dispositivo. Se configuró de tal manera en el archivo jpos.xml
	 */
	public String getLogicalName() {
		return logicalName;
	}

	/**
	 * Inserta el nombre lógico del dispositivo
	 * @param logicalName Nombre lógico del dispositivo. Se configuró de tal manera en el archivo jpos.xml
	 */
	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}
	
	/**
	 * @return Devuelve true si existe la instancia del dispositivo y un nombre lógico. En caso contrario, devuelve false.
	 * @throws NoLogicalNameException No hay un nombre lógico de dispositivo configurado en la clase
	 * @throws NoDeviceException No hay una instancia de dispositivo configurada en la clase
	 */
	private Boolean checkLogicalNameAndDevice() throws NoLogicalNameException, NoDeviceException{
		Boolean res=false;
		if(logicalName!=null && device !=null){
			res=true;
		}
		else{
			if(logicalName==null){
                log.error("checkLogicalNameAndDevice() - No hay un nombre lógico de dispositivo configurado en la clase");
			    throw new NoLogicalNameException(logicalName);
			}
			if(device==null){
                log.error("checkLogicalNameAndDevice() - No hay una instancia de dispositivo configurada en la clase");
			    throw new NoDeviceException();
			}
		}
		return res;
	}
	
	/**
	 * Ejecuta el método open para el nombre lógico configurado. Este método enlazará el DeviceService configurado en jpos.xml con la instancia BaseControl
	 * @param logicalName Nombre lógico del dispositivo. Se configuró de tal manera en el archivo jpos.xml
	 * @throws JposException Excepción generada por la librería JavaPOS. Contiene un código que identifica la razón de la misma. Estos códigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 * @throws NoLogicalNameException No hay un nombre lógico de dispositivo configurado en la clase
	 * @throws NoDeviceException No hay una instancia de dispositivo configurada en la clase
	 */
	public void open(String logicalName) throws JposException, NoLogicalNameException, NoDeviceException{
		if(checkLogicalNameAndDevice()){
			device.open(logicalName);
		}
	}
	
	/**
	 * Ejecuta el método claim sobre la instancia de dispositivo. Como resultado, el usuario obtendrá el uso exclusivo del dispositivo durante un tiempo pasado por parámetro
	 * @param timeout Tiempo de expiración en segundos. Por defecto es de 60 segundos
	 * @throws JposException Excepción generada por la librería JavaPOS. Contiene un código que identifica la razón de la misma. Estos códigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 * @throws NoLogicalNameException No hay un nombre logico de dispositivo configurado en la clase
	 * @throws NoDeviceException No hay una instancia de dispositivo configurada en la clase
	 */
	public void claim(Integer timeout) throws JposException, NoLogicalNameException, NoDeviceException{
		if(checkLogicalNameAndDevice()){
			device.claim(timeout);
		}
	}
	
	/**
	 * Activa la instancia de dispositivo. Como resultado, el usuario podrá ejecutar las operaciones que necesite del dispositivo
	 * @throws JposException Excepción generada por la librería JavaPOS. Contiene un codigo que identifica la razón de la misma. Estos códigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 * @throws NoLogicalNameException No hay un nombre lógico de dispositivo configurado en la clase
	 * @throws NoDeviceException No hay una instancia de dispositivo configurada en la clase
	 */
	public void enable() throws NoLogicalNameException, NoDeviceException, JposException{
		if(checkLogicalNameAndDevice()){
			device.setDeviceEnabled(true);
		}
	}
	
	/**
	 * Desactiva la instancia de dispositivo. Como resultado, el usuario no podrá ejecutar las operaciones del dispositivo
	 * @throws JposException Excepción generada por la librería JavaPOS. Contiene un código que identifica la razón de la misma. Estos códigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 * @throws NoLogicalNameException No hay un nombre lógico de dispositivo configurado en la clase
	 * @throws NoDeviceException No hay una instancia de dispositivo configurada en la clase
	 */
	public void disable() throws JposException, NoLogicalNameException, NoDeviceException{
		if(checkLogicalNameAndDevice() && device.getDeviceEnabled()){
			device.setDeviceEnabled(false);
		}
	}
	
	/**
	 * Libera la instancia de dispositivo. Como resultado, el usuario no tendrá acceso exclusivo al dispositivo. Deshace lo que el método claim hizo
	 * @throws NoLogicalNameException No hay un nombre lógico de dispositivo configurado en la clase
	 * @throws NoDeviceException No hay una instancia de dispositivo configurada en la clase
	 * @throws JposException Excepción generada por la librería JavaPOS. Contiene un código que identifica la razón de la misma. Estos códigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void release() throws NoLogicalNameException, NoDeviceException, JposException{
		if(checkLogicalNameAndDevice() && device.getClaimed()){
			device.release();
		}
	}
	
	/**
	 * Cierra la instancia de dispositivo desenlazando el DeviceSerivce de la instancia de dispositivo. Deshace lo que el método open hizo
	 * @throws NoLogicalNameException No hay un nombre lógico de dispositivo configurado en la clase
	 * @throws NoDeviceException No hay una instancia de dispositivo configurada en la clase
	 * @throws JposException Excepción generada por la librería JavaPOS. Contiene un código que identifica la razón de la misma. Estos códigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void close() throws NoLogicalNameException, NoDeviceException, JposException{
		if(checkLogicalNameAndDevice()){
			device.close();
		}
	}

	/**
	 * Este método prepara el dispositivo para que pueda ser utilizado. Ejecuta los métodos open, claim (no expira) y enable para la instancia y nombre lógico configurados
	 * @throws NoLogicalNameException No hay un nombre lógico de dispositivo configurado en la clase
	 * @throws NoDeviceException No hay una instancia de dispositivo configurada en la clase
	 * @throws JposException Excepción generada por la librería JavaPOS. Contiene un código que identifica la razón de la misma. Estos códigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void prepareDevice() throws NoLogicalNameException,	NoDeviceException, JposException {
		log.debug("prepareDevice() - open");
		this.open(logicalName);
		log.debug("prepareDevice() - claim");
		this.claim(0);
		log.debug("prepareDevice() - enable");
		this.enable();
	}

	/**
	 * Este método prepara el dispositivo para que pueda ser utilizado. Ejecuta los métodos open, claim (no expira) y enable para la instancia y nombre lógico pasados por parámetro
	 * @param logicalName Nombre lógico del dispositivo. Se configuró de tal manera en el archivo jpos.xml
	 * @param device Instancia del dispositivo utilizado
	 * @throws JposException Excepción generada por la libreria JavaPOS. Contiene un código que identifica la razón de la misma. Estos códigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 * @throws NoLogicalNameException No hay un nombre lógico de dispositivo configurado en la clase
	 * @throws NoDeviceException No hay una instancia de dispositivo configurada en la clase
	 */
	public void prepareDevice(String logicalName, BaseControl device) throws JposException, NoLogicalNameException, NoDeviceException{		
		this.setLogicalName(logicalName);
		this.setDevice(device);
		this.open(logicalName);
		this.claim(0);
		this.enable();
	}
	
	/**
	 * Este método finaliza la operación del dispositivo. Deshace lo que el método prepareDevice hizo para la instancia de dispositivo configurada
	 * @throws JposException Excepción generada por la librería JavaPOS. Contiene un código que identifica la razón de la misma. Estos códigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 * @throws NoLogicalNameException No hay un nombre lógico de dispositivo configurado en la clase
	 * @throws NoDeviceException No hay una instancia de dispositivo configurada en la clase
	 */
	public void finishDevice() throws JposException, NoLogicalNameException, NoDeviceException {
		try {
			log.debug("finishDevice() - disable");
			this.disable();
			log.debug("finishDevice() - release");
			this.release();
			log.debug("finishDevice() - close");
			this.close();
			log.debug("finishDevice() - Impresora desconectada correctamente");
		}
		catch(Exception e) {
			log.error("finishDevice() - Cerrando el dispositivo: " + e.getMessage(), e);
		}
	}

}
