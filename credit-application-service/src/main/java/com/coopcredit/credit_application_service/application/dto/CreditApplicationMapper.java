package com.coopcredit.credit_application_service.application.dto;

import com.coopcredit.credit_application_service.domain.entity.CreditApplication;
import com.coopcredit.credit_application_service.infrastructure.persistence.entity.CreditApplicationEntity;
import org.mapstruct.Mapper;

/**
 * MapStruct Mapper for CreditApplication
 * Converts between Domain, Entity, and DTO layers
 */
@Mapper(componentModel = "spring")
public interface CreditApplicationMapper {

    /**
     * Convert Entity to Domain
     */
    default CreditApplication toDomain(CreditApplicationEntity entity) {
        if (entity == null) {
            return null;
        }
        return CreditApplication.reconstruct(
                entity.getId(),
                entity.getAffiliateId(),
                entity.getAmount(),
                entity.getTerm(),
                entity.getStatus(),
                entity.getSubmissionDate(),
                entity.getRiskScore(),
                entity.getRiskLevel(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    /**
     * Convert Domain to Entity
     */
    default CreditApplicationEntity toEntity(CreditApplication domain) {
        if (domain == null) {
            return null;
        }
        return CreditApplicationEntity.builder()
                .id(domain.getId())
                .affiliateId(domain.getAffiliateId())
                .amount(domain.getAmount())
                .term(domain.getTerm())
                .status(domain.getStatus())
                .submissionDate(domain.getSubmissionDate())
                .riskScore(domain.getRiskScore())
                .riskLevel(domain.getRiskLevel())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    /**
     * Convert Domain to Response DTO
     */
    default CreditApplicationResponse toResponse(CreditApplication domain) {
        if (domain == null) {
            return null;
        }
        return new CreditApplicationResponse(
                domain.getId(),
                domain.getAffiliateId(),
                domain.getAmount(),
                domain.getTerm(),
                domain.getStatus().getLabel(),
                domain.getSubmissionDate(),
                domain.getRiskScore(),
                domain.getRiskLevel(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}
