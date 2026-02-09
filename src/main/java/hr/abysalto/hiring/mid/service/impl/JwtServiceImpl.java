package hr.abysalto.hiring.mid.service.impl;

import hr.abysalto.hiring.mid.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    @Override
    public String generateToken(UserDetails userDetails) {
        log.info("Generating JWT token for user: {}", userDetails.getUsername());
        String token = Jwts.builder()
                   .subject(userDetails.getUsername())
                   .issuedAt(new Date())
                   .expiration(new Date(System.currentTimeMillis() + expirationMs))
                   .signWith(getSigningKey())
                   .compact();
        log.info("JWT token generated successfully for user: {}", userDetails.getUsername());
        return token;
    }

    @Override
    public String extractUsername(String token) {
        log.info("Extracting username from JWT token");
        String username = extractClaims(token).getSubject();
        log.info("Extracted username: {}", username);
        return username;
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        log.info("Validating JWT token for user: {}", userDetails.getUsername());
        final String username = extractUsername(token);
        boolean valid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        if (valid) {
            log.info("JWT token is valid for user: {}", userDetails.getUsername());
        } else {
            log.warn("JWT token validation failed for user: {}", userDetails.getUsername());
        }
        return valid;
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    private Claims extractClaims(final String token) {
        try {
            return Jwts.parser()
                       .verifyWith(getSigningKey())
                       .build()
                       .parseSignedClaims(token)
                       .getPayload();
        } catch (final JwtException e) {
            log.error("Failed to parse JWT token: {}", e.getMessage());
            throw new RuntimeException("Invalid token", e);
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}