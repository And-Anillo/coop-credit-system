package com.coopcredit.credit_application_service.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain Entity: CreditApplication
 * Pure Java entity representing a credit application from an affiliate.
 */
public class CreditApplication {

    private Long id;
    private Long affiliateId;
    private BigDecimal amount;
    private Integer term;  // in months
    private CreditApplicationStatus status;
    private LocalDate submissionDate;
    private Integer riskScore;
    private String riskLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Enum for Credit Application Status
     */
    public enum CreditApplicationStatus {
        PENDING("Pendiente"),
        APPROVED("Aprobado"),
        REJECTED("Rechazado");

        private final String label;

        CreditApplicationStatus(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private CreditApplication(Long affiliateId) {
        if (affiliateId == null || affiliateId <= 0) {
            throw new IllegalArgumentException("Affiliate ID cannot be null or non-positive");
        }
        this.affiliateId = affiliateId;
    }

    /**
     * Factory method to create a new Credit Application
     */
    public static CreditApplication create(Long affiliateId, BigDecimal amount, Integer term) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (term == null || term <= 0) {
            throw new IllegalArgumentException("Term must be greater than zero months");
        }

        CreditApplication app = new CreditApplication(affiliateId);
        app.amount = amount;
        app.term = term;
        app.status = CreditApplicationStatus.PENDING;
        app.submissionDate = LocalDate.now();
        app.createdAt = LocalDateTime.now();
        app.updatedAt = LocalDateTime.now();
        return app;
    }

    /**
     * Reconstructs a CreditApplication with all properties (used by adapters)
     */
    public static CreditApplication reconstruct(Long id, Long affiliateId, BigDecimal amount,
                                                Integer term, CreditApplicationStatus status,
                                                LocalDate submissionDate, Integer riskScore,
                                                String riskLevel, LocalDateTime createdAt,
                                                LocalDateTime updatedAt) {
        CreditApplication app = new CreditApplication(affiliateId);
        app.id = id;
        app.amount = amount;
        app.term = term;
        app.status = status;
        app.submissionDate = submissionDate;
        app.riskScore = riskScore;
        app.riskLevel = riskLevel;
        app.createdAt = createdAt;
        app.updatedAt = updatedAt;
        return app;
    }

    /**
     * Approve the credit application
     */
    public void approve() {
        if (this.status != CreditApplicationStatus.PENDING) {
            throw new IllegalStateException("Only pending applications can be approved");
        }
        this.status = CreditApplicationStatus.APPROVED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Reject the credit application
     */
    public void reject() {
        if (this.status != CreditApplicationStatus.PENDING) {
            throw new IllegalStateException("Only pending applications can be rejected");
        }
        this.status = CreditApplicationStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Update risk evaluation data
     */
    public void updateRiskEvaluation(Integer score, String riskLevel) {
        if (score == null || score < 0) {
            throw new IllegalArgumentException("Risk score cannot be null or negative");
        }
        if (riskLevel == null || riskLevel.isBlank()) {
            throw new IllegalArgumentException("Risk level cannot be null or blank");
        }
        this.riskScore = score;
        this.riskLevel = riskLevel;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getAffiliateId() {
        return affiliateId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getTerm() {
        return term;
    }

    public CreditApplicationStatus getStatus() {
        return status;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditApplication that = (CreditApplication) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CreditApplication{" +
                "id=" + id +
                ", affiliateId=" + affiliateId +
                ", amount=" + amount +
                ", term=" + term +
                ", status=" + status +
                ", submissionDate=" + submissionDate +
                ", riskScore=" + riskScore +
                ", riskLevel='" + riskLevel + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
