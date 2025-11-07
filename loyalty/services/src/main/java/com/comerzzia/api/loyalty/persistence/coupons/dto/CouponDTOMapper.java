package com.comerzzia.api.loyalty.persistence.coupons.dto;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.comerzzia.api.loyalty.persistence.coupons.CouponUk;

public interface CouponDTOMapper {
    List<CouponDTO> selectByExample(CouponDTOExample example);

    CouponDTO selectByUk(@Param("key") CouponUk key, @Param("languageCode") String languageCode);
    
    List<CouponDTO> selectByCustomer(CouponDTOExample example);
    CouponCustomerUseDTO selectByUkAndCustomer(@Param("key")CouponUk key, 
    										   @Param("loyalCustomerId") String loyalCustomerId, 
    										   @Param("languageCode") String languageCode);
    
    CouponsKpiDTO selectKPIS(@Param("uidActividad") String uidActividad,
                      @Param("loyalCustomerId") String loyalCustomerId,
                      @Param("currentDate") Date currentDate,
                      @Param("startDate") Date startDate,
                      @Param("endDate") Date endDate); 
}