package com.coopcredit.credit_application_service.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for Affiliate
 */
public record AffiliateResponse(
    Long id,
    String name,
    String document,
    BigDecimal salary,
    LocalDate registrationDate,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

}
