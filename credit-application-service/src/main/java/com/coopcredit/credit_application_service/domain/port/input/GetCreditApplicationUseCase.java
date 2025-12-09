package com.coopcredit.credit_application_service.domain.port.input;

import com.coopcredit.credit_application_service.application.dto.CreditApplicationResponse;

import java.util.List;

/**
 * Input Port: GetCreditApplicationUseCase
 * Contract for reading and querying credit applications
 */
public interface GetCreditApplicationUseCase {

    /**
     * Get a credit application by ID
     *
     * @param id the credit application ID
     * @return the credit application response
     * @throws com.coopcredit.credit_application_service.domain.exception.DomainException if not found
     */
    CreditApplicationResponse getById(Long id);

    /**
     * Get all credit applications for an affiliate by document
     *
     * @param affiliateDocument the affiliate's document identifier
     * @return list of credit applications for the affiliate
     */
    List<CreditApplicationResponse> getAllByAffiliate(String affiliateDocument);
}
