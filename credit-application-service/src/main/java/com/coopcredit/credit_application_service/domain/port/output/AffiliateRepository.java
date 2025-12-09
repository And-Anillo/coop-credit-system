package com.coopcredit.credit_application_service.domain.port.output;

import com.coopcredit.credit_application_service.domain.entity.Affiliate;
import java.util.Optional;

/**
 * Output Port: AffiliateRepository
 * Contract for affiliate persistence operations
 * Implementation will be provided by infrastructure layer (JPA adapter)
 */
public interface AffiliateRepository {

    /**
     * Save or update an affiliate
     */
    Affiliate save(Affiliate affiliate);

    /**
     * Find an affiliate by ID
     */
    Optional<Affiliate> findById(Long id);

    /**
     * Find an affiliate by name
     */
    Optional<Affiliate> findByName(String name);

    /**
     * Find an affiliate by document
     */
    Optional<Affiliate> findByDocument(String document);

    /**
     * Delete an affiliate by ID
     */
    void deleteById(Long id);

    /**
     * Check if an affiliate exists by ID
     */
    boolean existsById(Long id);

    /**
     * Check if an affiliate exists by document (national identifier)
     */
    boolean existsByDocument(String document);

    /**
     * Get all active affiliates
     */
    Iterable<Affiliate> findAllActive();
}
