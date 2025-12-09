package com.coopcredit.credit_application_service.infrastructure.persistence.repository;

import com.coopcredit.credit_application_service.infrastructure.persistence.entity.AffiliateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Spring Data JPA repository for AffiliateEntity.
 */
@Repository
public interface AffiliateJpaRepository extends JpaRepository<AffiliateEntity, Long> {

    boolean existsByDocument(String document);

    java.util.List<AffiliateEntity> findByStatus(String status);
    Optional<AffiliateEntity> findByName(String name);
    Optional<AffiliateEntity> findByDocument(String document);

}
