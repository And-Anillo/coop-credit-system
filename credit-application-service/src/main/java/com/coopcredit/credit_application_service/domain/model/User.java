package com.coopcredit.credit_application_service.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain Model: User
 * Represents a system user for authentication and authorization
 */
public class User {

    private Long id;
    private String username;
    private String password;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private User(String username, String password, Role role) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        this.username = username;
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Factory method to create a new User
     */
    public static User create(String username, String password, Role role) {
        return new User(username, password, role);
    }

    /**
     * Reconstructs a User with all properties (used by adapters)
     */
    public static User reconstruct(Long id, String username, String password, Role role,
                                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        User user = new User(username, password, role);
        user.id = id;
        user.createdAt = createdAt;
        user.updatedAt = updatedAt;
        return user;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
