


package com.comerzzia.pos.devices.cashdrawer;

import org.apache.log4j.Logger;

import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.device.DeviceAbstractImpl;
import com.comerzzia.pos.core.devices.device.DeviceException;
import com.comerzzia.pos.core.devices.device.cashdrawer.DeviceCashDrawer;

import jpos.CashDrawer;
import jpos.JposConst;
import jpos.JposException;


public class DeviceCashDrawerUPOS extends DeviceAbstractImpl implements DeviceCashDrawer {

	protected static Logger log = Logger.getLogger(DeviceCashDrawerUPOS.class);
	protected CashDrawer cd = new CashDrawer();
	protected String logicalName;

	@Override
    public void connect() throws DeviceException {
		try {
			cd.open(logicalName);
			cd.claim(0);
			cd.setDeviceEnabled(true);
		} catch (JposException e) {
			log.debug("conecta() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			throw new DeviceException(e);
		}
    }

    @Override
    public void disconnect() throws DeviceException {
    	try {    		
    		cd.setDeviceEnabled(false);
    		cd.release();
			cd.close();
		} catch (JposException e) {
			log.error("desconecta() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			throw new DeviceException(e);
		}
    }

    @Override
    public void open() {
    	try {
            cd.openDrawer();
		} catch (JposException e) {
			log.debug("abrir() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}
    }

    @Override
   	public void loadConfiguration(DeviceConfiguration config) throws DeviceException {
   		try {
			logicalName = config.getModelConfiguration().getConnectionName();
		} catch (Exception e) {
			log.error("cargaConfiguracion() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			throw new DeviceException(e);
		}
   	}
    
	@Override
	public boolean isReady() {
		return cd.getState() == JposConst.JPOS_S_IDLE;
	}
    
    @Override
	public boolean isOpened() {
		try {
			return cd.getDrawerOpened();
		}
		catch (Exception e) {
			log.error("isOpen() - Error while check if cash drawer is open:  " + e.getMessage(), e);
			return true;
		}
	}

}
