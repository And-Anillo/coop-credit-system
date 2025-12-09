package com.coopcredit.credit_application_service.infrastructure.persistence.adapter;

import com.coopcredit.credit_application_service.domain.model.User;
import com.coopcredit.credit_application_service.domain.port.output.UserRepository;
import com.coopcredit.credit_application_service.infrastructure.persistence.entity.UserEntity;
import com.coopcredit.credit_application_service.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter: UserRepositoryAdapter
 * Implements the domain UserRepository port using Spring Data JPA
 */
@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username)
                .map(this::entityToDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = domainToEntity(user);
        UserEntity savedEntity = userJpaRepository.save(entity);
        return entityToDomain(savedEntity);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    /**
     * Convert JPA entity to domain model
     */
    private User entityToDomain(UserEntity entity) {
        return User.reconstruct(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getRole(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    /**
     * Convert domain model to JPA entity
     */
    private UserEntity domainToEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
