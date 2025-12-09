package com.coopcredit.credit_application_service.domain.model;

/**
 * Domain Value Object representing the result of a risk evaluation
 */
public class RiskEvaluation {

    private final Integer score;
    private final String riskLevel;
    private final String detail;

    public RiskEvaluation(Integer score, String riskLevel, String detail) {
        if (score == null || score < 0) {
            throw new IllegalArgumentException("Score cannot be null or negative");
        }
        if (riskLevel == null || riskLevel.isBlank()) {
            throw new IllegalArgumentException("Risk level cannot be null or blank");
        }
        this.score = score;
        this.riskLevel = riskLevel;
        this.detail = detail != null ? detail : "";
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

    @Override
    public String toString() {
        return "RiskEvaluation{" +
                "score=" + score +
                ", riskLevel='" + riskLevel + '\'' +
                ", detail='" + detail + '\'' +
                '}';
    }
}
