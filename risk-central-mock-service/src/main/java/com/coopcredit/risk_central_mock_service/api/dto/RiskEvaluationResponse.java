package com.coopcredit.risk_central_mock_service.api.dto;

import java.time.LocalDateTime;

/**
 * DTO for risk evaluation response
 */
public record RiskEvaluationResponse(
    String document,
    Integer score,
    String riskLevel,
    String detail,
    LocalDateTime evaluatedAt
) {}
