package com.comerzzia.pos.core.devices.device.loyaltycard;

import com.comerzzia.omnichannel.facade.model.basket.header.BasketLoyalCustomer;
import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.device.DeviceAbstractImpl;
import com.comerzzia.pos.core.devices.device.DeviceCallback;


public class DeviceLoyaltyCardDummy extends DeviceAbstractImpl implements DeviceLoyaltyCard {

    @Override
    public void connect() {
      
    }

    @Override
    public void disconnect() {
        
    }
    
    @Override
    public boolean isLoyaltyCardPrefix(String codArticulo) {
        return false;
    }

    @Override
    public void setApikey(String apiKey) {
       
    }

	@Override
	protected void loadConfiguration(DeviceConfiguration config) {
	}

	@Override
	public void ignoreBackgroundTaskResult() {
	}

	@Override
	public void findLoyalCustomerInBackground(String cardCode, DeviceCallback<BasketLoyalCustomer> callback) throws DeviceLoyaltyCardException {
	}

	@Override
	public void findLoyalCustomerAndCouponsInBackground(String numTarjeta, DeviceCallback<BasketLoyalCustomer> callback) throws DeviceLoyaltyCardException {
		
	}

	@Override
	public BasketLoyalCustomer findLoyalCustomer(String numTarjRegalo) throws DeviceLoyaltyCardException {
		return null;
	}

	@Override
	public BasketLoyalCustomer findLoyalCustomerAndCoupons(String numTarjRegalo) throws DeviceLoyaltyCardException {
		return null;
	}

	@Override
	public void setLoyalCustomerCoupons(BasketLoyalCustomer basketLyCustomer) throws Exception {

	}
	
	@Override
	public boolean isReady() {
		return false;
	}
    
}
