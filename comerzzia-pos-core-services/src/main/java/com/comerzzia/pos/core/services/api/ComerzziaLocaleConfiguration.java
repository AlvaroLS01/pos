package com.comerzzia.pos.core.services.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;

@Configuration
public class ComerzziaLocaleConfiguration {
	@Autowired
	private MessageSource messageSource;	
    
    @Bean(name = "messageSourceAccessor")
    public MessageSourceAccessor messageSourceAccessor() {
    	MessageSourceAccessor r = new MessageSourceAccessor(messageSource);
       return r;
    }    
}
