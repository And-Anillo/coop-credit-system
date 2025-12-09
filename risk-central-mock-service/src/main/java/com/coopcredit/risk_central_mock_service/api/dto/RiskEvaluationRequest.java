package com.coopcredit.risk_central_mock_service.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * DTO for risk evaluation request
 */
public record RiskEvaluationRequest(
    @NotBlank(message = "El documento es requerido")
    String document,

    @NotNull(message = "El monto es requerido")
    @Positive(message = "El monto debe ser mayor a cero")
    BigDecimal amount,

    @NotNull(message = "El plazo es requerido")
    @Positive(message = "El plazo debe ser mayor a cero")
    Integer term
) {}
