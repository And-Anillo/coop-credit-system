package com.coopcredit.credit_application_service.application.dto;

import com.coopcredit.credit_application_service.domain.entity.Affiliate;
import com.coopcredit.credit_application_service.infrastructure.persistence.entity.AffiliateEntity;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;

/**
 * MapStruct mapper for Affiliate <-> DTO and AffiliateEntity mappings.
 * Default methods are used to integrate with domain factory methods.
 */
@Mapper(componentModel = "spring")
public interface AffiliateMapper {

    default com.coopcredit.credit_application_service.domain.entity.Affiliate toDomain(CreateAffiliateRequest request) {
        if (request == null) return null;
        return com.coopcredit.credit_application_service.domain.entity.Affiliate.create(
            request.name(),
            request.salary(),
            request.registrationDate(),
            request.document()
        );
    }

    default AffiliateResponse toResponse(com.coopcredit.credit_application_service.domain.entity.Affiliate affiliate) {
        if (affiliate == null) return null;
        return new AffiliateResponse(
            affiliate.getId(),
            affiliate.getName(),
            null,
            affiliate.getSalary(),
            affiliate.getRegistrationDate(),
            affiliate.getStatus() != null ? affiliate.getStatus().getLabel() : null,
            affiliate.getCreatedAt(),
            affiliate.getUpdatedAt()
        );
    }

    // --- Domain <-> Entity mappings ---

    default AffiliateEntity toEntity(Affiliate affiliate) {
        if (affiliate == null) return null;
        AffiliateEntity entity = new AffiliateEntity();
        entity.setId(affiliate.getId());
        entity.setName(affiliate.getName());
        entity.setDocument(affiliate.getDocument());
        entity.setSalary(affiliate.getSalary());
        entity.setRegistrationDate(affiliate.getRegistrationDate());
        entity.setStatus(affiliate.getStatus());
        entity.setCreatedAt(affiliate.getCreatedAt() != null ? affiliate.getCreatedAt() : LocalDateTime.now());
        entity.setUpdatedAt(affiliate.getUpdatedAt() != null ? affiliate.getUpdatedAt() : LocalDateTime.now());
        return entity;
    }

    default Affiliate toDomain(AffiliateEntity entity) {
        if (entity == null) return null;
        return com.coopcredit.credit_application_service.domain.entity.Affiliate.reconstruct(
            entity.getId(),
            entity.getName(),
            entity.getSalary(),
            entity.getRegistrationDate(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getDocument()
        );
    }

}
