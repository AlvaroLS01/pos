package com.comerzzia.pos.core.services.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;

@SuppressWarnings("deprecation")
public class ComerzziaPasswordEncoder extends MessageDigestPasswordEncoder {

	public ComerzziaPasswordEncoder(String algorithm) {
		super(algorithm);
	}

	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		if (StringUtils.startsWith(rawPassword, "MD5:")) {
			return StringUtils.equals(StringUtils.substring((String) rawPassword, 4), encodedPassword);
		}
		else {
			return super.matches(rawPassword, encodedPassword);
		}
	}

}
