package com.coopcredit.credit_application_service.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO: CreateCreditApplicationRequest
 * Request to create a new credit application
 */
public record CreateCreditApplicationRequest(
    @NotNull(message = "El ID del afiliado es requerido")
    Long affiliateId,

    @NotNull(message = "El monto es requerido")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
    BigDecimal amount,

    @NotNull(message = "El plazo es requerido")
    @Min(value = 1, message = "El plazo debe ser mayor a cero meses")
    Integer term
) {}
