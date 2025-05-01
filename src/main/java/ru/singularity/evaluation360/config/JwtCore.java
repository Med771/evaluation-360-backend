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

@Component
public class JwtCore {
    private final Long timeLive;

    private final SecretKey key;

    public JwtCore(
            @Value("${jwt.secret.token}") String jwtSecret,
            @Value("${jwt.time.live}") Long timeLive) {
        this.timeLive = timeLive;

        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(Authentication auth) {
        UserEntity user = (UserEntity) auth.getPrincipal();

        Date now = new Date();

        return Jwts.builder()
                .subject((user.getUsername()))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + timeLive))
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

            Date expiration = claimsJws.getPayload().getExpiration();

            return expiration != null && expiration.after(Date.from(Instant.now()));
        } catch (JwtException | IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }
}
