package com.coopcredit.credit_application_service.domain.port.output;

import com.coopcredit.credit_application_service.domain.entity.CreditApplication;

import java.util.List;
import java.util.Optional;

/**
 * Output Port: CreditApplicationRepository
 * Contract for persisting and retrieving credit applications
 */
public interface CreditApplicationRepository {

    /**
     * Save a credit application
     *
     * @param creditApplication the credit application to save
     * @return the saved credit application with ID
     */
    CreditApplication save(CreditApplication creditApplication);

    /**
     * Find a credit application by ID
     *
     * @param id the credit application ID
     * @return the credit application if found
     */
    Optional<CreditApplication> findById(Long id);

    /**
     * Find all credit applications for a specific affiliate
     *
     * @param affiliateId the affiliate ID
     * @return list of credit applications for the affiliate
     */
    List<CreditApplication> findAllByAffiliateId(Long affiliateId);

    /**
     * Check if a credit application exists
     *
     * @param id the credit application ID
     * @return true if it exists, false otherwise
     */
    boolean existsById(Long id);

    /**
     * Delete a credit application
     *
     * @param id the credit application ID
     */
    void deleteById(Long id);
}
