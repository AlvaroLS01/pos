package com.comerzzia.dinosol.pos.services.cupones;

import java.math.BigDecimal;
import java.util.Comparator;

public class ComparatorCuponImporte implements Comparator<CustomerCouponDTO> {

	@Override
	public int compare(CustomerCouponDTO o1, CustomerCouponDTO o2) {
		Long promotionIdO1 = o1.getPromotionId();
		if(promotionIdO1 == null) {
			promotionIdO1 = 0L;
		}
		
		Long promotionIdO2 = o2.getPromotionId();
		if(promotionIdO2 == null) {
			promotionIdO2 = 0L;
		}
		
		
		int comparacionPromocion = promotionIdO1.compareTo(promotionIdO2);
		
		if(comparacionPromocion != 0) {
			return comparacionPromocion;
		}
		
		BigDecimal balanceO1 = o1.getBalance();
		if(balanceO1 == null) {
			balanceO1 = BigDecimal.ZERO;
		}
		
		BigDecimal balanceO2 = o2.getBalance();
		if(balanceO2 == null) {
			balanceO2 = BigDecimal.ZERO;
		}
		
		int comparacionImporte = balanceO2.compareTo(balanceO1);
		
		return comparacionImporte;
	}

}
