package com.coopcredit.credit_application_service.infrastructure.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * Filter: JwtAuthenticationFilter
 * Intercepts HTTP requests to extract and validate JWT tokens from Authorization header
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = extractToken(request);

            if (token != null && jwtService.validateToken(token)) {
                String username = jwtService.extractUsername(token);
                String role = jwtService.extractRole(token);

                // Create authentication token with user and role
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(role))
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("JWT authentication successful for user: {}", username);
            }
        } catch (Exception e) {
            log.error("Could not set user authentication in security context: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     * Expected format: "Authorization: Bearer <token>"
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}
