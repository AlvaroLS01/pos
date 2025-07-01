
package com.comerzzia.pos.core.devices.device.linedisplay;

import org.apache.log4j.Logger;

import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.device.DeviceAbstractImpl;

public class DeviceLineDisplayDummy extends DeviceAbstractImpl implements DeviceLineDisplay {
	
	private static final Logger log = Logger.getLogger(DeviceLineDisplayDummy.class);

    @Override
    public void connect() {
        
    }

    @Override
    public void disconnect() {
        
    }

	@Override
    public void write(String cad1, String cad2) {
		log.trace("VisorNoConfig write() - \n" + cad1 + "\n" + cad2);
    }

    @Override
    public void writeLineUp(String cadena) {
    	write(cadena, "-");
    }

    @Override
    public void writeLineDown(String cadena) {
    	write("-", cadena);
    }

    @Override
    public void clear() {
    	log.trace(String.format("VisorNoConfig limpiar()"));
    }

    @Override
    public Integer getNumColumns() {
        return 0;
    }

    @Override
    public void writeRightUp(String cadena) {
    	write("       " + cadena, "-");
    }

    @Override
    public void writeRightDown(String cadena) {
    	write("-", "       " + cadena);
    }

	@Override
	public Integer getNumRows() {
		return null;
	}

	@Override
	public void tenderMode(BasketPromotable<?> basket) {
		log.trace("VisorNoConfig modoPago()");
	}

	@Override
	public void standbyMode() {
		log.trace("VisorNoConfig modoEspera()");
	}

	@Override
	public void saleMode(BasketPromotable<?> basket) {
		log.trace("VisorNoConfig modoVenta()");
	}

	@Override
	protected void loadConfiguration(DeviceConfiguration config) {
	}
    
}
