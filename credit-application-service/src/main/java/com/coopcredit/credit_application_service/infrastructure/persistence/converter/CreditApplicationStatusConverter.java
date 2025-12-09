package com.coopcredit.credit_application_service.infrastructure.persistence.converter;

import com.coopcredit.credit_application_service.domain.entity.CreditApplication;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA Converter for CreditApplication Status
 * Maps Java enum to Spanish database strings
 */
@Converter(autoApply = true)
public class CreditApplicationStatusConverter implements AttributeConverter<CreditApplication.CreditApplicationStatus, String> {

    @Override
    public String convertToDatabaseColumn(CreditApplication.CreditApplicationStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case PENDING -> "PENDIENTE";
            case APPROVED -> "APROBADO";
            case REJECTED -> "RECHAZADO";
        };
    }

    @Override
    public CreditApplication.CreditApplicationStatus convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }
        return switch (value) {
            case "PENDIENTE" -> CreditApplication.CreditApplicationStatus.PENDING;
            case "APROBADO" -> CreditApplication.CreditApplicationStatus.APPROVED;
            case "RECHAZADO" -> CreditApplication.CreditApplicationStatus.REJECTED;
            default -> throw new IllegalArgumentException("Unknown credit application status: " + value);
        };
    }
}
