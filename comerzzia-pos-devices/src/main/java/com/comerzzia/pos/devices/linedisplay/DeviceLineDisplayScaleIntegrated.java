package com.comerzzia.pos.devices.linedisplay;

import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.device.DeviceAbstractImpl;
import com.comerzzia.pos.core.devices.device.DeviceException;
import com.comerzzia.pos.core.devices.device.linedisplay.DeviceLineDisplay;
import com.comerzzia.pos.core.devices.device.scale.DeviceScale;

public class DeviceLineDisplayScaleIntegrated extends DeviceAbstractImpl implements DeviceLineDisplay {
	
	protected DeviceScale getScale() {
		return Devices.getInstance().getScale();
	}

	@Override
	public void write(String cad1, String cad2) {
		getScale().displayText((cad1 == null ? "" : cad2) + (cad2 == null ? "" : "\n" + cad2));		
	}

	@Override
	public void writeLineUp(String cadena) {
		write(cadena, null);		
	}

	@Override
	public void writeLineDown(String cadena) {
		write(null, cadena);		
	}

	@Override
	public void clear() {
		write(null, null);
		
	}

	@Override
	public Integer getNumColumns() {
		return null;
	}

	@Override
	public Integer getNumRows() {
		return null;
	}

	@Override
	public void writeRightUp(String cadena) {
	}

	@Override
	public void writeRightDown(String cadena) {
	}

	@Override
	public void tenderMode(BasketPromotable<?> basket) {
		
	}

	@Override
	public void saleMode(BasketPromotable<?> basket) {
		
	}

	@Override
	public void standbyMode() {		
	}

	@Override
	public void connect() throws DeviceException {		
	}

	@Override
	public void disconnect() throws DeviceException {		
	}

	@Override
	protected void loadConfiguration(DeviceConfiguration config) throws DeviceException {		
	}

}
