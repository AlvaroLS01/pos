package com.comerzzia.pos.core.services.countryidtypevalidator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Component;

import com.comerzzia.core.commons.CoreContextHolder;
import com.comerzzia.core.facade.model.CountryIdType;

import lombok.extern.log4j.Log4j;

@Component
@Log4j
public class CountryDocTypeValidatorBuilder {
	
	protected CountryDocTypeValidator getValidator(CountryIdType countryIdType) {
    	CountryDocTypeValidator validatorVatNumber = null;
    	
    	// compatibility
        try {
			validatorVatNumber = (CountryDocTypeValidator) CoreContextHolder.getInstance(countryIdType.getValidationClass());
			return validatorVatNumber;
		} catch (RuntimeException ignore) {
		}
        
    	// by country code+identification type code
        try {
			validatorVatNumber = (CountryDocTypeValidator) CoreContextHolder.get().getBean("countryDocTypeValidator" + countryIdType.getCountryCode() + "_" + countryIdType.getIdentificationTypeCode());
			
			return validatorVatNumber;
		} catch (NoSuchBeanDefinitionException ignore) {
		}    	

    	// by country code
        try {
			validatorVatNumber = (CountryDocTypeValidator) CoreContextHolder.get().getBean("countryDocTypeValidator" + countryIdType.getCountryCode());
			
			return validatorVatNumber;
		} catch (NoSuchBeanDefinitionException ignore) {
		}    	
        
        return null;
	}
	
    public boolean validate(CountryIdType countryIdType, String document) {
    	if (StringUtils.isBlank(countryIdType.getValidationClass())) return true;
    	
    	CountryDocTypeValidator validatorVatNumber = getValidator(countryIdType);
    	
    	if (validatorVatNumber == null) {
    		log.error("Error loading document type validation for " + countryIdType);
    		return true;
    	}
    				
		return validatorVatNumber.validateVatNumber(document);
	}
}
