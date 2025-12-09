package com.coopcredit.credit_application_service.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for creating an Affiliate
 * All code identifiers are in English. User-facing messages are in Spanish.
 */
public record CreateAffiliateRequest(
    @NotBlank(message = "El nombre es requerido")
    String name,

    @NotBlank(message = "El documento es requerido")
    String document,

    @NotNull(message = "El salario es requerido")
    @Positive(message = "El salario debe ser mayor a cero")
    BigDecimal salary,

    @NotNull(message = "La fecha de registro es requerida")
    @PastOrPresent(message = "La fecha debe ser presente o pasada")
    LocalDate registrationDate
) {}
