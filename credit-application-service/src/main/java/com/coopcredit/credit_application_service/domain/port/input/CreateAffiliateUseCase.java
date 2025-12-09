package com.coopcredit.credit_application_service.domain.port.input;

import com.coopcredit.credit_application_service.domain.entity.Affiliate;

/**
 * Input Port: CreateAffiliateUseCase
 * Contract for creating a new affiliate
 * Implementation will be provided by application layer (service)
 */
public interface CreateAffiliateUseCase {

    /**
     * Create a new affiliate
     */
    Affiliate execute(CreateAffiliateCommand command);

    /**
     * Command object for creating an affiliate
     */
    record CreateAffiliateCommand(
        String name,
        String document,
        String salary,
        String registrationDate
    ) {}
}
