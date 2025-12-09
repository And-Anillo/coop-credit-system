package com.coopcredit.credit_application_service.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO: CreditApplicationResponse
 * Response representing a credit application
 */
public record CreditApplicationResponse(
    Long id,
    Long affiliateId,
    BigDecimal amount,
    Integer term,
    String status,  // Spanish label: "Pendiente", "Aprobado", "Rechazado"
    LocalDate submissionDate,
    Integer riskScore,
    String riskLevel,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
