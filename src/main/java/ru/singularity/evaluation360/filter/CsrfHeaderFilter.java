package ru.singularity.evaluation360.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CsrfHeaderFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrf != null) {
            String token = csrf.getToken();

            response.setHeader("Set-Cookie", String.format("XSRF-TOKEN=%s; Path=/; HttpOnly=false; SameSite=Strict", token));

            response.setHeader("X-CSRF-TOKEN", token);
            response.setHeader("X-XSRF-TOKEN", token);
            response.setHeader("X-CSRF-HEADER", csrf.getHeaderName());
            response.setHeader("X-CSRF-PARAM", csrf.getParameterName());
        }
        filterChain.doFilter(request, response);
    }
}
