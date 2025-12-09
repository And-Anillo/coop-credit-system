package com.coopcredit.credit_application_service.infrastructure.security.config;

import com.coopcredit.credit_application_service.infrastructure.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration: SecurityConfig
 * Spring Security configuration with JWT-based stateless authentication
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configure HTTP security with JWT filter and public/protected endpoints
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> {
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // OpenAPI / Swagger - explicitly allow anonymous access
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/configuration/ui",
                                "/configuration/security")
                        .permitAll()

                        // Allow preflight requests
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()

                        // Public endpoints
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/health/**").permitAll()

                        // Protected endpoints
                        .requestMatchers(HttpMethod.POST, "/affiliates/**").hasAnyRole("ADMIN", "ANALISTA")
                        .requestMatchers(HttpMethod.GET, "/affiliates/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/credit-applications/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/credit-applications/**").authenticated()

                        // All other requests require authentication
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Password encoder bean using BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication manager bean
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
