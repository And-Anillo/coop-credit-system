package com.coopcredit.credit_application_service.infrastructure.persistence.entity;

import com.coopcredit.credit_application_service.domain.entity.CreditApplication;
import com.coopcredit.credit_application_service.infrastructure.persistence.converter.CreditApplicationStatusConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JPA Entity: CreditApplicationEntity
 * Maps the domain CreditApplication entity to the credit_applications table
 */
@Entity
@Table(name = "credit_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "affiliate_id", nullable = false)
    private Long affiliateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliate_id", referencedColumnName = "id", insertable = false, updatable = false)
    private AffiliateEntity affiliate;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "term", nullable = false)
    private Integer term;

    @Column(name = "status", nullable = false)
    @Convert(converter = CreditApplicationStatusConverter.class)
    private CreditApplication.CreditApplicationStatus status;

    @Column(name = "submission_date", nullable = false)
    private LocalDate submissionDate;

    @Column(name = "risk_score")
    private Integer riskScore;

    @Column(name = "risk_level")
    private String riskLevel;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
