package com.coopcredit.credit_application_service.domain.port.input;

import com.coopcredit.credit_application_service.domain.entity.CreditApplication;

/**
 * Input Port: CreateCreditApplicationUseCase
 * Contract for creating and evaluating credit applications
 */
public interface CreateCreditApplicationUseCase {

    /**
     * Command record for creating a credit application
     */
    record CreateCreditApplicationCommand(
        Long affiliateId,
        java.math.BigDecimal amount,
        Integer term
    ) {}

    /**
     * Execute the create credit application use case
     *
     * @param command the command containing affiliate ID, amount, and term
     * @return the created and evaluated credit application
     */
    CreditApplication execute(CreateCreditApplicationCommand command);
}
