package com.comerzzia.pos.core.devices.device.loyaltycard;

import com.comerzzia.omnichannel.facade.model.basket.header.BasketLoyalCustomer;
import com.comerzzia.pos.core.devices.device.DeviceCallback;
import com.comerzzia.pos.core.devices.device.Device;


public interface DeviceLoyaltyCard extends Device{
	
	public static final String TAG_CONFIGURATION = "configuration";
	public static final String TAG_CONFIG_PREFIXES = "prefixes";
	public static final String TAG_CONFIG_PREFIX = "prefix";
    
    public boolean isLoyaltyCardPrefix(String itemCode);

    public void findLoyalCustomerInBackground(String cardCode, DeviceCallback<BasketLoyalCustomer> callback) throws DeviceLoyaltyCardException;
    public void findLoyalCustomerAndCouponsInBackground(String numTarjeta, DeviceCallback<BasketLoyalCustomer> callback) throws DeviceLoyaltyCardException;
    
    /**Marca la petición actual de tarjeta fidelizado como ignorada para que no llame al callback*/
    public void ignoreBackgroundTaskResult();
    public void setApikey(String apiKey);
    
	public BasketLoyalCustomer findLoyalCustomer(String cardCode) throws DeviceLoyaltyCardException;
	public BasketLoyalCustomer findLoyalCustomerAndCoupons(String cardCode) throws DeviceLoyaltyCardException;

	void setLoyalCustomerCoupons(BasketLoyalCustomer basketLyCustomer) throws Exception;
    
}
