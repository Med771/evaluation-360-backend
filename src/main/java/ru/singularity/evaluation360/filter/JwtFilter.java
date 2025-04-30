package ru.singularity.evaluation360.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.singularity.evaluation360.config.JwtCore;
import ru.singularity.evaluation360.service.CustomUserDetailsService;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtCore jwtCore;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String jwt = "";
        String username = "";
        UserDetails userDetails;
        UsernamePasswordAuthenticationToken authentication;

        try {
            String headersAuth = request.getHeader("Authorization");

            if (headersAuth != null && headersAuth.startsWith("Bearer ")) {
                jwt = headersAuth.substring(7);
            }

            if (!jwt.isEmpty()) {
                try {
                    username = jwtCore.extractName(jwt);
                }
                catch (Exception e) {
                    throw new ServletException(e);
                }

                System.out.println(username);

                if (!username.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
                    userDetails = customUserDetailsService.loadUserByUsername(username);
                    authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        catch (Exception e) {
            throw new ServletException(e);
        }

        filterChain.doFilter(request, response);
    }
}
