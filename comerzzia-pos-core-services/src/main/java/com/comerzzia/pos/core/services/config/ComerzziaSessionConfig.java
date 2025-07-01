package com.comerzzia.pos.core.services.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.comerzzia.core.commons.sessions.ComerzziaSession;
import com.comerzzia.pos.core.services.security.DefaultPasswordEncoderFactories;

@Configuration
public class ComerzziaSessionConfig {

	@Bean
	public ComerzziaSession comerzziaSession() {
		return new ComerzziaSession();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return DefaultPasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

}
