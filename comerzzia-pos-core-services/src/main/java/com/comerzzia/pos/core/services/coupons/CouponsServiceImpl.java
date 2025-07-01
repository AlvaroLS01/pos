package com.comerzzia.pos.core.services.coupons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.api.loyalty.client.CouponsApiClient;
import com.comerzzia.api.loyalty.client.model.Coupon;
import com.comerzzia.core.commons.exception.NotFoundException;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketLoyalCustomerCoupon;
import com.comerzzia.omnichannel.facade.service.basket.exception.CouponValidationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class CouponsServiceImpl implements CouponsService {
	protected Map<String, CouponTypeDTO> couponsType;
	protected boolean isCouponLoaded;
	
    @Autowired
    protected CouponsApiClient couponsApiClient;
    
    @Autowired
    protected ModelMapper modelMapper;
    
    @PostConstruct 
    protected void init() {
    	readCouponsTypeFile();
    }
        
	public CouponTypeDTO getCouponType(String couponTypeCode) {		
		return couponsType.get(couponTypeCode);
	}
	
	public CouponTypeDTO getCouponTypeByPrefix(String couponPrefix) {
		if(couponsType != null) {
			for(CouponTypeDTO couponType : couponsType.values()) {
				if(StringUtils.startsWith(couponPrefix, couponType.getPrefix())) {
					return couponType;
				}
			}
		}
		
		return null;
	}
	
	
	/**
	 * Indicates if the code is a coupon according to its prefix.
	 * @param code Code to be evaluated
	 * @return true if is a coupon, false if it is not
	 */
	@Override
	public boolean isCouponCode(String code) {
		if (StringUtils.isBlank(code)) return false;
		
		return getCouponTypeByPrefix(code) != null;
	}
	
	protected void readCouponsTypeFile() {
    	try {
    		couponsType = new HashMap<String, CouponTypeDTO>();
    		
    		String filePath = "entities/ly_coupons_types_tbl.json";
    		log.debug("readCouponsTypeFile() - Reading file " + filePath);
    		
	    	Method method = (java.lang.Thread.class).getMethod("getContextClassLoader", (Class<?>[]) null);
			ClassLoader classLoader = (ClassLoader) method.invoke(Thread.currentThread(), (Object[]) null);
			URL url = classLoader.getResource(filePath);
						
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            
            String json = "";
            String line = "";
            while ((line = in.readLine()) != null) {
                json = json + System.lineSeparator() + line;
            }
            in.close();
			
			List<CouponTypeDTO> coupons = readJson(json.getBytes());
			
			for(CouponTypeDTO couponType : coupons) {
				couponsType.put(couponType.getCouponTypeCode(), couponType);
			}
			
			isCouponLoaded = true;
    	}
    	catch (Exception e) {
    		log.warn("readCouponsTypeFile() - File not found ly_coupons_types_tbl.json: " + e.getMessage());
    	}
		
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T readJson(byte[] json) {
		if (json == null) {
			return null;
		}
		ObjectMapper objectMapper = new ObjectMapper();
		// jaxb annotations support
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			return (T)objectMapper.readValue(new String(json), new TypeReference<List<CouponTypeDTO>>(){});
		}
		catch (JsonParseException | JsonMappingException e) {
			throw new RuntimeException(e);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public BasketLoyalCustomerCoupon validateCoupon(String couponCode, Long loyalCustomerId) {
	    try {
	            
            BasketLoyalCustomerCoupon coupon = new BasketLoyalCustomerCoupon();
			Coupon validation = couponsApiClient.validateCoupon(couponCode, loyalCustomerId, null).getBody();
			
			modelMapper.map(validation, coupon);
			coupon.setCouponCode(validation.getCouponCode());
			coupon.setPromotionId(validation.getPromotionId());
			coupon.setBalance(validation.getBalance());
	            
	        return coupon;

	    } catch (NotFoundException e) {
	        log.error("validateCoupon() - HTTP Error while validating coupon: " + e.getMessage(), e);
	        throw new CouponValidationException("Error validating coupon: " + e.getMessage(), e);
	    } catch (Exception e) {
	        log.error("validateCoupon() - Unexpected error while validating coupon: " + e.getMessage(), e);
	        throw new CouponValidationException("Error validating coupon: " + e.getMessage(), e);
	    }
	}

	
	public boolean isCouponLoaded() {
		return isCouponLoaded;
	}
}
