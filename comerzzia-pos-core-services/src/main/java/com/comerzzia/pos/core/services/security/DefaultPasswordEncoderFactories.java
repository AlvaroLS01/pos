package com.comerzzia.pos.core.services.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SuppressWarnings("deprecation")
public class DefaultPasswordEncoderFactories {

	/*
	 * https://dzone.com/articles/password-encoder-migration-with-spring-security-5 
	 */
    public static PasswordEncoder createDelegatingPasswordEncoder() {        
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        MessageDigestPasswordEncoder defaultPasswordEncoder = new ComerzziaPasswordEncoder("MD5");
        
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("MD5", defaultPasswordEncoder);
        encoders.put("ldap", new org.springframework.security.crypto.password.LdapShaPasswordEncoder());
        encoders.put("SHA-256", new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-256"));

        DelegatingPasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder("MD5", encoders);
        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(defaultPasswordEncoder);
        return delegatingPasswordEncoder;
    }
}
