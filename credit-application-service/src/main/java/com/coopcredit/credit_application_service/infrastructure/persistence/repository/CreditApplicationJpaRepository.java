package com.coopcredit.credit_application_service.infrastructure.persistence.repository;

import com.coopcredit.credit_application_service.infrastructure.persistence.entity.CreditApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for CreditApplicationEntity
 */
@Repository
public interface CreditApplicationJpaRepository extends JpaRepository<CreditApplicationEntity, Long> {

    /**
     * Find all credit applications for a specific affiliate
     */
    List<CreditApplicationEntity> findAllByAffiliateId(Long affiliateId);
}
