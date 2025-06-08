package com.ecober.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;
import java.security.Key;

@Service
public class JwtService {

    private static final String SECRET_KEY = "ecoberSecretKeyecoberSecretKey1234"; // min 256 bits

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /**
     * Generates JWT using userId as subject
     */
    public String generateToken(UUID userId) {
        return Jwts.builder()
            .setSubject(userId.toString()) // ✅ Set UUID as subject
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * Extracts userId from JWT token
     */
    public String extractUserId(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject(); // ✅ subject = userId (UUID)
    }

    /**
     * Validates token against stored userId
     */
    public boolean isTokenValid(String token, UUID userId) {
        String extractedUserId = extractUserId(token);
        return extractedUserId.equals(userId.toString());
    }
}
