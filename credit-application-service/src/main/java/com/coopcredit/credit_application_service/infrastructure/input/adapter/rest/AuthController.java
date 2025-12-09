package com.coopcredit.credit_application_service.infrastructure.input.adapter.rest;

import com.coopcredit.credit_application_service.domain.model.Role;
import com.coopcredit.credit_application_service.domain.model.User;
import com.coopcredit.credit_application_service.domain.port.output.UserRepository;
import com.coopcredit.credit_application_service.infrastructure.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller: AuthController
 * Handles user authentication: login and registration
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    /**
     * POST /auth/login
     * Authenticate user and return JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
        
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("username", user.getUsername());
        response.put("role", user.getRole().name());

        log.info("User {} logged in successfully", user.getUsername());
        return ResponseEntity.ok(response);
    }

    /**
     * POST /auth/register
     * Register new user (ROLE_AFILIADO by default)
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Create new user with AFILIADO role
        User newUser = User.create(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                Role.ROLE_AFILIADO
        );

        userRepository.save(newUser);

        String token = jwtService.generateToken(newUser.getUsername(), newUser.getRole().name());
        
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("username", newUser.getUsername());
        response.put("role", newUser.getRole().name());

        log.info("New user {} registered successfully", newUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

/**
 * DTO: LoginRequest
 */
class LoginRequest {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

/**
 * DTO: RegisterRequest
 */
class RegisterRequest {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
