package ru.singularity.evaluation360.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ru.singularity.evaluation360.entity.UserEntity;

import javax.crypto.SecretKey;

import java.util.Date;

@Component
public class JwtCore {
    @Value("${jwt.secret.token}")
    private String jwtSecret;

    @Value("${jwt.time.live}")
    private Long timeLive;

    private final SecretKey key;

    public JwtCore() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(Authentication auth) {
        UserEntity user = (UserEntity) auth.getPrincipal();

        return Jwts.builder()
                .subject((user.getUsername()))
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + timeLive))
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
}
