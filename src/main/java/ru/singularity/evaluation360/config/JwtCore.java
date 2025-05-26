package ru.singularity.evaluation360.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ru.singularity.evaluation360.entity.UserEntity;

import javax.crypto.SecretKey;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtCore {
    private final Long accessTokenTimeLive;
    private final Long refreshTokenTimeLive;
    private final SecretKey key;

    public JwtCore(
            @Value("${jwt.secret.token}") String jwtSecret,
            @Value("${jwt.time.live}") Long accessTokenTimeLive,
            @Value("${jwt.refresh.time.live:86400000}") Long refreshTokenTimeLive) {
        this.accessTokenTimeLive = accessTokenTimeLive;
        this.refreshTokenTimeLive = refreshTokenTimeLive;
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateAccessToken(Authentication auth) {
        UserEntity user = (UserEntity) auth.getPrincipal();
        return generateToken(user.getUsername(), accessTokenTimeLive);
    }

    public String generateRefreshToken(Authentication auth) {
        UserEntity user = (UserEntity) auth.getPrincipal();
        return generateToken(user.getUsername(), refreshTokenTimeLive);
    }

    protected String generateToken(String username, Long expirationTime) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);

        Map<String, Object> claims = new HashMap<>();
        claims.put("type", expirationTime.equals(accessTokenTimeLive) ? "ACCESS" : "REFRESH");

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    public String extractName(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            Claims claims = claimsJws.getPayload();
            Date expiration = claims.getExpiration();
            String tokenType = claims.get("type", String.class);

            return expiration != null && 
                   expiration.after(Date.from(Instant.now())) &&
                   "ACCESS".equals(tokenType);
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            Claims claims = claimsJws.getPayload();
            Date expiration = claims.getExpiration();
            String tokenType = claims.get("type", String.class);

            return expiration != null && 
                   expiration.after(Date.from(Instant.now())) &&
                   "REFRESH".equals(tokenType);
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
