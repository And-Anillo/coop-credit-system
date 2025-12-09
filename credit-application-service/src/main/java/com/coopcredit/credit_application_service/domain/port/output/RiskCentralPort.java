package com.coopcredit.credit_application_service.domain.port.output;

import com.coopcredit.credit_application_service.domain.model.RiskEvaluation;

/**
 * Output Port: RiskCentralPort
 * Contract for evaluating credit risk with an external service
 */
public interface RiskCentralPort {

    /**
     * Evaluate the credit risk for a given document, amount, and term
     *
     * @param document the customer's document identifier
     * @param amount the credit amount requested
     * @param term the credit term in months
     * @return the risk evaluation result
     */
    RiskEvaluation evaluateRisk(String document, Double amount, Integer term);
}
