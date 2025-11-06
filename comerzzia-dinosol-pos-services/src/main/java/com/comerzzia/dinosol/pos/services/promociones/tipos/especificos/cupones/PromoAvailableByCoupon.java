package com.comerzzia.dinosol.pos.services.promociones.tipos.especificos.cupones;

import com.comerzzia.dinosol.pos.services.cupones.CustomerCouponDTO;

public interface PromoAvailableByCoupon {

	public CustomerCouponDTO getCustomerCoupon();
	
	public void setCustomerCoupon(CustomerCouponDTO customerCoupon);
	
	public boolean permiteCuponesAcumulables();

}
