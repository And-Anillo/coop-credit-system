package com.coopcredit.credit_application_service.domain.port.output;

import java.util.concurrent.CompletableFuture;

/**
 * Output Port: RiskEvaluationPort
 * Contract for external risk evaluation service
 * Implementation will be provided by infrastructure layer (REST client adapter)
 */
public interface RiskEvaluationPort {

    /**
     * Evaluate credit risk for an affiliate based on document number
     * Returns risk score asynchronously
     */
    CompletableFuture<RiskEvaluationResult> evaluateRisk(String documentNumber);

    /**
     * DTO for risk evaluation result from external service
     */
    class RiskEvaluationResult {
        private final String document;
        private final Integer score;
        private final String riskLevel;
        private final String detail;

        public RiskEvaluationResult(String document, Integer score, String riskLevel, String detail) {
            this.document = document;
            this.score = score;
            this.riskLevel = riskLevel;
            this.detail = detail;
        }

        public String getDocument() {
            return document;
        }

        public Integer getScore() {
            return score;
        }

        public String getRiskLevel() {
            return riskLevel;
        }

        public String getDetail() {
            return detail;
        }
    }
}
