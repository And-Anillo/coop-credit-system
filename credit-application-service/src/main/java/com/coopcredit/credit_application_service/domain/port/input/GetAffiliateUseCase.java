package com.coopcredit.credit_application_service.domain.port.input;

import com.coopcredit.credit_application_service.domain.entity.Affiliate;
import java.util.Optional;

/**
 * Input Port: GetAffiliateUseCase
 * Contract for retrieving affiliate information
 */
public interface GetAffiliateUseCase {

    /**
     * Get affiliate by ID
     */
    Optional<Affiliate> getById(Long id);

    /**
     * Get affiliate by name
     */
    Optional<Affiliate> getByName(String name);

    /**
     * Get affiliate by document
     */
    Optional<Affiliate> getByDocument(String document);
}
