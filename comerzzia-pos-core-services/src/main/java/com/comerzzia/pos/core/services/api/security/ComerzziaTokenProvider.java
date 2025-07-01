package com.comerzzia.pos.core.services.api.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.comerzzia.core.commons.sessions.ComerzziaTenantResolver;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class ComerzziaTokenProvider {
    private final Logger log = Logger.getLogger(ComerzziaTokenProvider.class);
    
    private HashMap<String, String> userTokens = new HashMap<>();

    private static final String AUTHORITIES_KEY = "auth";

    @Value("${security.jwt.token.secret-key:my-secret-token-to-change-in-production-my-secret-token-to-change-in-production}")
    private String secretKey;// = "my-secret-token-to-change-in-production-my-secret-token-to-change-in-production"; // = "my-secret-token-to-change-in-production-my-secret-token-to-change-in-production";
    
    @Value("${security.jwt.token.secret-key-base64:#{null}}")
    private String secretKeyBase64;
    
    private Key key;
    
    // @Value("${security.jwt.token.expire-length}") // 8h
    private long tokenValidityInMilliseconds = 86400000L; 
    
    // @Value("${security.jwt.token.expire-length-remenber}")
    private long tokenValidityInMillisecondsForRememberMe = 86400000L;
    
    @Autowired
    protected ComerzziaTenantResolver tenantResolver;

//    public TokenProvider() { 
//    }

//    @PostConstruct
//    public void init() {
//        byte[] keyBytes;
//
//        if (!StringUtils.isEmpty(secretKey)) {
//            log.warn("Warning: the JWT key used is not Base64-encoded. " +
//                "We recommend using the `security.jwt.token.secret-key-base64` key for optimum security.");
//            keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
//        } else {
//            log.debug("Using a Base64-encoded JWT secret key");
//            keyBytes = Decoders.BASE64.decode(secretKeyBase64); 
//        }
//        
//        this.key = Keys.hmacShaKeyFor(keyBytes);
//    }
    
    private Key getKey() {
        if (this.key == null) {
        	byte[] keyBytes;

            if (!StringUtils.isEmpty(secretKey)) {
                log.warn("Warning: the JWT key used is not Base64-encoded. " +
                    "We recommend using the `security.jwt.token.secret-key-base64` key for optimum security.");
                keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            } else {
                log.debug("Using a Base64-encoded JWT secret key");
                keyBytes = Decoders.BASE64.decode(secretKeyBase64); 
            }
            
            this.key = Keys.hmacShaKeyFor(keyBytes);           
        }
        
        return this.key;
     }    

    private String createTokenForUser(String activityUid, String userCode, boolean rememberMe) {
        long now = (new Date()).getTime();
        Date validity;
        if (rememberMe) {
            validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
        } else {
            validity = new Date(now + this.tokenValidityInMilliseconds);
        }
        
        return Jwts.builder()
            .setSubject(userCode)
            .claim(AUTHORITIES_KEY, "")
            .claim("activityUid", activityUid)
            .signWith(getKey(), SignatureAlgorithm.HS512)
            .setExpiration(validity)
            .compact();
    }


    private boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(getKey()).parseClaimsJws(authToken);            
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature.");
            log.trace("Invalid JWT signature trace: {}", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            log.trace("Expired JWT token trace: {}", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: {}", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace: {}", e);
        }
        return false;
    }
    
    public String getValidToken() {
    	return getValidToken(tenantResolver.getActivityUid(), tenantResolver.getUser().getUserCode());
    }
    
    public String getValidToken(String activityUid, String userCode) {
    	String sessionKey = activityUid + "-" + userCode;
        String token = userTokens.get(sessionKey);
        
        // check token is valid
    	if (token != null && !validateToken(token)) {
    		// reset token
    		token = null;
    	}
    	
    	// create token
    	if (token == null) {
    	   token = createTokenForUser(activityUid, userCode, false);
    	   userTokens.put(sessionKey, token);
    	}
    	
    	return token;
    }
        
}
