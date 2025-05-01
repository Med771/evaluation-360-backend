package ru.singularity.evaluation360.filter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.singularity.evaluation360.config.JwtCore;
import ru.singularity.evaluation360.service.CustomUserDetailsService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtCore jwtCore;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String token = extractTokenFromCookies(request);

        try {
            if (token != null && jwtCore.validateToken(token)) {
                authenticate(token, request);
            }
        } catch (ExpiredJwtException ex) {
            Cookie expiredCookie = new Cookie("JWT_TOKEN", null);
            expiredCookie.setSecure(false);
            expiredCookie.setPath("/");
            expiredCookie.setHttpOnly(true);
            expiredCookie.setMaxAge(3600);
            response.addCookie(expiredCookie);

            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Token expired, please login again");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie c : request.getCookies()) {
            if ("JWT_TOKEN".equals(c.getName())) { return c.getValue(); }
        }
        return null;
    }

    private void authenticate(String token, HttpServletRequest request) {
        String username = jwtCore.extractName(token);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
