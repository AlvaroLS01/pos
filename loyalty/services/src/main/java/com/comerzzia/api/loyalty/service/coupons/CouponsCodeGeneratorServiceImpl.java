package com.comerzzia.api.loyalty.service.coupons;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.checkdigit.EAN13CheckDigit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.apache.commons.validator.routines.checkdigit.CheckDigitException;


import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.core.service.exception.BadRequestException;
import com.comerzzia.api.loyalty.persistence.couponsTypes.CouponType;
import com.comerzzia.api.loyalty.service.coupons.dto.NewCoupon;
import com.comerzzia.api.loyalty.service.couponsTypes.CouponsTypesService;
import com.comerzzia.core.servicios.contadores.ContadorException;
import com.comerzzia.core.servicios.contadores.ContadoresService;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

@Service
public class CouponsCodeGeneratorServiceImpl implements CouponsCodeGeneratorService {
   @Autowired
   CouponsService couponsService;
   
   @Autowired
   ContadoresService contadoresService;
   
   @Autowired
   CouponsTypesService couponsTypesService;
   
   /*
    * Default format for sequentially and aleatory coupons
    * CouponTypePrefix +
    * PromotionId (10 digits pad zero left) +
    * Balance (5 digits pad zero left) +
    * Start date (AAMMDD)
    * End date (AAMMDD)
    * coupon identificator (8 digits)
    */
   
   @Override
   public String generateCouponCode(IDatosSesion datosSesion, NewCoupon newCoupon) throws ApiException {
      CouponType couponType = couponsTypesService.selectByPrimaryKeyFromCache(datosSesion, newCoupon.getCouponTypeCode());
      
      if (couponType.getGenerationMode().compareTo(CouponsTypesService.GENERATION_MODE_SEQUENTIALLY) == 0) {
         return getCouponCodeSequentially(datosSesion, newCoupon);         
      } else if (couponType.getGenerationMode().compareTo(CouponsTypesService.GENERATION_MODE_MANUAL) == 0) {
         if (StringUtils.isEmpty(newCoupon.getCouponCode())) {
            throw new BadRequestException(LY_COUPON_CODE_NULL_IN_MANUAL_MODE, new String[] {});
         }
         return newCoupon.getCouponCode();
      } else {
         // default mode aleatory
         return getCouponCodeAleatory(datosSesion, newCoupon);
      }              
   }   
   
   protected String getCouponSuffix(IDatosSesion datosSesion, NewCoupon newCoupon) {
//      SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
//      
//      String promotionId = StringUtils.leftPad(newCoupon.getPromotionId().toString(), 10, "0");
//      String balance = StringUtils.leftPad(newCoupon.getBalance() == null ? "0" : newCoupon.getBalance().multiply(new BigDecimal(100)).abs().toBigInteger().toString(), 5, "0");
//      String startDate = (newCoupon.getStartDate()!=null)?df.format(newCoupon.getStartDate()):"000000"; 
//      String endDate = (newCoupon.getEndDate()!=null)?df.format(newCoupon.getEndDate()):"000000";
//            
//      return promotionId + balance + startDate + endDate;
      return "";
   }
         
   protected String getCouponCodeAleatory(IDatosSesion datosSesion, NewCoupon newCoupon) throws ApiException {
      if (newCoupon.getCouponCode() != null) {
         return newCoupon.getCouponCode();
      }
      
      String couponCode = null;       
      int attempts=0;
      
      CouponType couponType = couponsTypesService.selectByPrimaryKeyFromCache(datosSesion, newCoupon.getCouponTypeCode());
      String couponSuffix = getCouponSuffix(datosSesion, newCoupon);
      
      // get available account code
      do {
         attempts++;
         if (attempts > 1000) {
            throw new ApiException(ApiException.STATUS_RESPONSE_ERROR_CONFLICT_STATE, 
                                   LY_COUPON_CODE_MAX_ATTEMPTS, new String[] {});       
         }
         
         Double maxValue = (Math.pow(10, couponType.getMaxLength()))-1D;
         Long value = new Long(1 + (long) (Math.random() * (maxValue - 1)));
         
         couponCode = formatCouponCode(value, couponType.getPrefix(), couponSuffix, couponType.getMaxLength());
         
         // search account code
      } while (couponsService.exitsCouponCode(datosSesion, couponCode));

      return couponCode;
   }
   
   protected String getCouponCodeSequentially(IDatosSesion datosSesion, NewCoupon newCoupon) throws ApiException {
      if (newCoupon.getCouponCode() != null) {
         return newCoupon.getCouponCode();
      }
      
      CouponType couponType = couponsTypesService.selectByPrimaryKeyFromCache(datosSesion, newCoupon.getCouponTypeCode());
      String couponSuffix = getCouponSuffix(datosSesion, newCoupon);
      
      Map<String, String> parametrosContador = new HashMap<String, String>();
      parametrosContador.put("COUPON_TYPE_CODE", (newCoupon.getCouponTypeCode() != null)?newCoupon.getCouponTypeCode():"DEFAULT");
      parametrosContador.put("PROMOTION_ID", newCoupon.getPromotionId().toString());
      
      Long value;
      try {
         value = contadoresService.obtenerValorContador(datosSesion, "LY_COUPON_CODE", parametrosContador);
      }catch(ContadorException e) {
         throw new ApiException(ApiException.STATUS_RESPONSE_ERROR_INTERNAL, COUNTER_NOT_FOUND, new String[]{"LY_COUPON_CODE"});
      }
      
      return formatCouponCode(value, couponType.getPrefix(), couponSuffix, couponType.getMaxLength());
   }

   protected String formatCouponCode(Long value, String prefix, String suffix, Short maxLength) {
	   String code = StringUtils.join(new String[]{prefix, suffix, StringUtils.leftPad(value.toString(), maxLength, "0")});
	
	   // tomar como ean13
	   if (code.length() == 12) {
			try {
				code += new EAN13CheckDigit().calculate(code);
			} catch (CheckDigitException e) {
				throw new RuntimeException(e);
			}	   
       }
	   
	   return code;
   }
   

}
