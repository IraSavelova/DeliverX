package com.deliverx.auth.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    private final Log logger = LogFactory.getLog(getClass());

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        // Keys.hmacShaKeyFor() кидает WeakKeyException в рантайме
        // (при запуске) если secret < 32 байт (символов)
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJwtToken(Authentication authentication) {
        String email = authentication.getName();   // Почта как имя
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(authToken);
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("JWT expired: {}", e);
        } catch (UnsupportedJwtException e) {
            logger.warn("JWT unsupported: {}", e);
        } catch (MalformedJwtException e) {
            logger.warn("JWT malformed: {}", e);
        } catch (IllegalArgumentException e) {
            logger.warn("JWT claims string is empty: {}", e);
        }
        return false;
    }

    public long getJwtExpirationMs() { return jwtExpirationMs; }
}
