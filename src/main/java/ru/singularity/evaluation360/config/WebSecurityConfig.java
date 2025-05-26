package ru.singularity.evaluation360.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpStatus;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ru.singularity.evaluation360.filter.CsrfHeaderFilter;
import ru.singularity.evaluation360.filter.JwtFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private static final String[] AUTH_WHITELIST = {
            "/respondent/**",
            "/result/**",
            "/test/**",
            "/skill/**",
            "/admin/**",
    };

    private static final String[] AUTH_IGNORE_WHITELIST = {
            "/auth/**",
            "/auth/csrf-token",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/favicon.ico"
    };

    private final JwtFilter jwtFilter;
    private final CsrfHeaderFilter csrfHeaderFilter;

    @Bean
    public SecurityFilterChain prodSecurityFilterChain(HttpSecurity http) throws Exception {
        // CSRF configuration
        http.csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers(
                        new AntPathRequestMatcher("/swagger-ui/**"),
                        new AntPathRequestMatcher("/v3/api-docs/**"),
                        new AntPathRequestMatcher("/swagger-ui.html"),
                        new AntPathRequestMatcher("/swagger-resources/**"),
                        new AntPathRequestMatcher("/webjars/**")
                )
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
        );

        // CORS configuration
        http.cors(cors -> cors
                .configurationSource(corsConfigurationSource()));

        // Session management
        http.sessionManagement(sm -> sm
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Authentication
        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

        // Authorization
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(AUTH_IGNORE_WHITELIST).permitAll()
                .requestMatchers(AUTH_WHITELIST).authenticated()
                .anyRequest().denyAll()
        );

        // Filters
        http.addFilterBefore(csrfHeaderFilter, CsrfFilter.class);
        http.addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // Security headers
        http.headers(headers -> headers
                .httpStrictTransportSecurity(hsts -> hsts
                        .includeSubDomains(true)
                        .maxAgeInSeconds(31536000)
                        .preload(true)
                )
                .contentSecurityPolicy(csp -> csp
                        .policyDirectives("default-src 'self'; " +
                                "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                                "style-src 'self' 'unsafe-inline'; " +
                                "img-src 'self' data:; " +
                                "font-src 'self'; " +
                                "connect-src 'self'; " +
                                "frame-ancestors 'none';")
                )
                .referrerPolicy(ref -> ref
                        .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                .xssProtection(HeadersConfigurer.XXssConfig::disable)
                .contentTypeOptions(HeadersConfigurer.ContentTypeOptionsConfig::disable)
        );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(List.of(
                "Authorization",
                "Cache-Control",
                "Content-Type",
                "X-XSRF-TOKEN",
                "X-CSRF-TOKEN",
                "X-Requested-With"
        ));
        config.setExposedHeaders(List.of(
                "X-CSRF-TOKEN",
                "X-XSRF-TOKEN",
                "X-CSRF-HEADER",
                "X-CSRF-PARAM"
        ));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
