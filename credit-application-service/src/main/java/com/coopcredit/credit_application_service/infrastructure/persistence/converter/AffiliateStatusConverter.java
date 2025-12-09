package com.coopcredit.credit_application_service.infrastructure.persistence.converter;

import com.coopcredit.credit_application_service.domain.entity.Affiliate;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA AttributeConverter to map Affiliate.AffiliateStatus to localized DB values.
 * Java ACTIVE <-> DB "ACTIVO"
 * Java INACTIVE <-> DB "INACTIVO"
 */
@Converter(autoApply = false)
public class AffiliateStatusConverter implements AttributeConverter<Affiliate.AffiliateStatus, String> {

    @Override
    public String convertToDatabaseColumn(Affiliate.AffiliateStatus attribute) {
        if (attribute == null) return null;
        return switch (attribute) {
            case ACTIVE -> "ACTIVO";
            case INACTIVE -> "INACTIVO";
            case SUSPENDED -> "SUSPENDIDO";
        };
    }

    @Override
    public Affiliate.AffiliateStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return switch (dbData) {
            case "ACTIVO" -> Affiliate.AffiliateStatus.ACTIVE;
            case "INACTIVO" -> Affiliate.AffiliateStatus.INACTIVE;
            case "SUSPENDIDO" -> Affiliate.AffiliateStatus.SUSPENDED;
            default -> null;
        };
    }
}
